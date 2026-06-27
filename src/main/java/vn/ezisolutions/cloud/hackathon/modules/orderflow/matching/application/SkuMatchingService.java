package vn.ezisolutions.cloud.hackathon.modules.orderflow.matching.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.application.VietnameseTextNormalizer;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderLineEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderLineStatus;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProductAliasEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.ProductSkuEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.SkuCandidateEntity;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.DraftOrderLineRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProductAliasRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.ProductSkuRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure.SkuCandidateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkuMatchingService {
    private final VietnameseTextNormalizer normalizer;
    private final ProductSkuRepository productSkuRepository;
    private final ProductAliasRepository productAliasRepository;
    private final SkuCandidateRepository skuCandidateRepository;
    private final DraftOrderLineRepository draftOrderLineRepository;

    @Transactional
    public List<SkuCandidateEntity> matchLine(DraftOrderLineEntity line) {
        skuCandidateRepository.deleteByDraftOrderLineId(line.getId());
        String normalized = normalizer.normalize(line.getRawLineText() + " " + nullToEmpty(line.getItemDescription()));
        List<ProductSkuEntity> skus = productSkuRepository.findByOrganizationIdAndActiveTrueOrderBySkuCode(line.getOrganizationId());
        List<ProductAliasEntity> aliases = productAliasRepository.findByOrganizationIdAndActiveTrue(line.getOrganizationId());
        List<ScoredSku> scored = skus.stream()
                .map(sku -> score(sku, aliases, normalized, line.getExtractedAttributes()))
                .filter(candidate -> candidate.score().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(ScoredSku::score).reversed())
                .limit(5)
                .toList();
        int rank = 1;
        for (ScoredSku item : scored) {
            SkuCandidateEntity candidate = new SkuCandidateEntity();
            candidate.setOrganizationId(line.getOrganizationId());
            candidate.setDraftOrderLineId(line.getId());
            candidate.setSkuId(item.sku().getId());
            candidate.setRankNo(rank++);
            candidate.setConfidenceScore(item.score());
            candidate.setMatchReason(item.reason());
            candidate.setMatchedAttributes(item.matchedAttributes());
            candidate.setMissingAttributes(item.missingAttributes());
            candidate.setSource("RULE_MATCHER");
            candidate.setCreatedAt(OffsetDateTime.now());
            skuCandidateRepository.save(candidate);
        }
        if (!scored.isEmpty() && shouldAutoMatch(scored)) {
            ScoredSku best = scored.getFirst();
            line.setSelectedSkuId(best.sku().getId());
            line.setStatus(DraftOrderLineStatus.MATCHED);
        } else {
            line.setStatus(DraftOrderLineStatus.PENDING_MATCH);
        }
        draftOrderLineRepository.save(line);
        return skuCandidateRepository.findByDraftOrderLineIdOrderByRankNo(line.getId());
    }

    public List<SkuCandidateEntity> candidates(UUID lineId) {
        return skuCandidateRepository.findByDraftOrderLineIdOrderByRankNo(lineId);
    }

    private boolean shouldAutoMatch(List<ScoredSku> scored) {
        if (scored.isEmpty()) {
            return false;
        }
        BigDecimal best = scored.getFirst().score();
        if (best.compareTo(new BigDecimal("0.82")) < 0) {
            return false;
        }
        if (scored.size() == 1) {
            return true;
        }
        return best.subtract(scored.get(1).score()).compareTo(new BigDecimal("0.12")) >= 0;
    }

    private ScoredSku score(ProductSkuEntity sku, List<ProductAliasEntity> aliases, String normalized, Map<String, Object> attributes) {
        BigDecimal score = BigDecimal.ZERO;
        Map<String, Object> matched = new LinkedHashMap<>();
        Map<String, Object> missing = new LinkedHashMap<>();
        for (ProductAliasEntity alias : aliases) {
            if (alias.getSkuId() != null && !alias.getSkuId().equals(sku.getId())) {
                continue;
            }
            if (normalized.contains(alias.getNormalizedAlias())) {
                score = score.add(nullSafe(alias.getConfidenceWeight()));
                matched.put("alias", alias.getAliasText());
            }
        }
        score = score.add(matchText(normalized, sku.getMaterial(), "material", matched));
        score = score.add(matchText(normalized, sku.getBrand(), "brand", matched));
        score = score.add(matchText(normalized, sku.getFittingType(), "fittingType", matched));
        score = score.add(matchNumber(normalized, sku.getDiameterMm(), "diameterMm", matched));
        if (sku.getAngleDegree() != null && normalized.contains(String.valueOf(sku.getAngleDegree()))) {
            score = score.add(new BigDecimal("0.12"));
            matched.put("angleDegree", sku.getAngleDegree());
        }
        if (attributes != null) {
            score = score.add(attributeMatch(attributes, sku, matched));
        }
        if (!matched.containsKey("diameterMm") && sku.getDiameterMm() != null) {
            missing.put("diameterMm", true);
        }
        BigDecimal capped = score.min(BigDecimal.ONE).setScale(4, RoundingMode.HALF_UP);
        return new ScoredSku(sku, capped, "Matched " + matched.keySet(), matched, missing);
    }

    private BigDecimal attributeMatch(Map<String, Object> attributes, ProductSkuEntity sku, Map<String, Object> matched) {
        BigDecimal score = BigDecimal.ZERO;
        String material = stringAttr(attributes, "material");
        if (material != null && sku.getMaterial() != null && normalizer.normalize(material).contains(normalizer.normalize(sku.getMaterial()))) {
            matched.put("material", sku.getMaterial());
            score = score.add(new BigDecimal("0.12"));
        }
        String brand = stringAttr(attributes, "brand");
        if (brand != null && sku.getBrand() != null && normalizer.normalize(brand).contains(normalizer.normalize(sku.getBrand()))) {
            matched.put("brand", sku.getBrand());
            score = score.add(new BigDecimal("0.10"));
        }
        String fittingType = stringAttr(attributes, "fittingType");
        if (fittingType != null && sku.getFittingType() != null && normalizer.normalize(fittingType).contains(normalizer.normalize(sku.getFittingType()))) {
            matched.put("fittingType", sku.getFittingType());
            score = score.add(new BigDecimal("0.12"));
        }
        Object diameter = attributes.get("diameterMm");
        if (diameter != null && sku.getDiameterMm() != null && String.valueOf(diameter).contains(sku.getDiameterMm().stripTrailingZeros().toPlainString())) {
            matched.put("diameterMm", sku.getDiameterMm());
            score = score.add(new BigDecimal("0.15"));
        }
        return score;
    }

    private BigDecimal matchText(String normalized, String value, String key, Map<String, Object> matched) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        if (normalized.contains(normalizer.normalize(value))) {
            matched.put(key, value);
            return new BigDecimal("0.10");
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal matchNumber(String normalized, BigDecimal value, String key, Map<String, Object> matched) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        String text = value.stripTrailingZeros().toPlainString();
        if (normalized.contains(text)) {
            matched.put(key, value);
            return new BigDecimal("0.15");
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String stringAttr(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record ScoredSku(ProductSkuEntity sku, BigDecimal score, String reason,
                             Map<String, Object> matchedAttributes, Map<String, Object> missingAttributes) {
    }
}
