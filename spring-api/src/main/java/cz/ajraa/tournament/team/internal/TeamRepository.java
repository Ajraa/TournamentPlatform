package cz.ajraa.tournament.team.internal;

import org.springframework.data.jpa.repository.JpaRepository;

interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    boolean existsByTag(String tag);
}
