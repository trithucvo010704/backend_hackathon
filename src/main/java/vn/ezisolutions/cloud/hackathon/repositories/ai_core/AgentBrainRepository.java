package vn.ezisolutions.cloud.hackathon.repositories.ai_core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentBrain;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentBrainRepository extends JpaRepository<AgentBrain, UUID> {
    Optional<AgentBrain> findFirstByName(String name);
}
