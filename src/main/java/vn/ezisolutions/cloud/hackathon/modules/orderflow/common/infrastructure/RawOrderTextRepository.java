package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.RawOrderTextEntity;

import java.util.UUID;

public interface RawOrderTextRepository extends JpaRepository<RawOrderTextEntity, UUID> {
}
