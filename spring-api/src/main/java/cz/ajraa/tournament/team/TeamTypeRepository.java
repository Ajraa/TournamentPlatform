package cz.ajraa.tournament.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamTypeRepository extends JpaRepository<TeamTypeEntity, Long> {
    Optional<TeamTypeEntity> findByCode(String code);
}
