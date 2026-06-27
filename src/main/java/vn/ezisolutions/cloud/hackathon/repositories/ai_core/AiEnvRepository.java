package vn.ezisolutions.cloud.hackathon.repositories.ai_core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AiEnv;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiEnvRepository extends JpaRepository<AiEnv, UUID> {
    Optional<AiEnv> findFirstByName(String name);

    @Query("""
            SELECT e FROM AiEnv e
            WHERE e.enabled = true
              AND LOWER(e.provider) = LOWER(:provider)
              AND (e.cooldownUntil IS NULL OR e.cooldownUntil <= :now)
            ORDER BY e.priority ASC,
                     e.failureCount ASC,
                     CASE WHEN e.lastUsedAt IS NULL THEN 0 ELSE 1 END ASC,
                     e.lastUsedAt ASC
            """)
    List<AiEnv> findAvailable(@Param("provider") String provider, @Param("now") LocalDateTime now);
}
