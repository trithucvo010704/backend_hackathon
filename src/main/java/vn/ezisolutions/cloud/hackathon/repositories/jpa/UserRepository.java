package vn.ezisolutions.cloud.hackathon.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ezisolutions.cloud.hackathon.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByRememberToken(String rememberToken);
}
