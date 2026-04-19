package cz.ajraa.tournament.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TeamTypeRepository extends JpaRepository<TeamTypeEntity, Long> {
    Optional<TeamTypeEntity> findByCode(String code);
}
