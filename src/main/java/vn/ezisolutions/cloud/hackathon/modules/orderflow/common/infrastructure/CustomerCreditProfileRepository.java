package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.CustomerCreditProfileEntity;

import java.util.UUID;

public interface CustomerCreditProfileRepository extends JpaRepository<CustomerCreditProfileEntity, UUID> {
}
