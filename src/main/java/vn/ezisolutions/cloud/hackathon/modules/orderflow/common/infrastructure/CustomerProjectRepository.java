package vn.ezisolutions.cloud.hackathon.modules.orderflow.common.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.modules.orderflow.common.domain.CustomerProjectEntity;

import java.util.List;
import java.util.UUID;

public interface CustomerProjectRepository extends JpaRepository<CustomerProjectEntity, UUID> {
    List<CustomerProjectEntity> findByOrganizationIdAndCustomerIdAndActiveTrueOrderByName(UUID organizationId, UUID customerId);
}
