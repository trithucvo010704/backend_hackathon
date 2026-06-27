package vn.ezisolutions.cloud.hackathon.repositories.ai_core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ezisolutions.cloud.hackathon.documents.ai_core.AgentResponse;

import java.util.UUID;

@Repository
public interface AgentResponseRepository extends JpaRepository<AgentResponse, UUID> {
}
