package vn.ezisolutions.cloud.hackathon.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.entities.RoleEntity;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);
}
