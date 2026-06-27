package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.DraftOrderDocumentEntity;

import java.util.List;
import java.util.UUID;

public interface DraftOrderDocumentRepository extends JpaRepository<DraftOrderDocumentEntity, UUID> {
    List<DraftOrderDocumentEntity> findByDraftOrderIdOrderByCreatedAtDesc(UUID draftOrderId);
}
