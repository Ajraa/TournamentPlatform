package cz.ajraa.tournament.team.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TeamTypeRepository extends JpaRepository<TeamTypeEntity, Long> {
    Optional<TeamTypeEntity> findByCode(TeamType code);
}
