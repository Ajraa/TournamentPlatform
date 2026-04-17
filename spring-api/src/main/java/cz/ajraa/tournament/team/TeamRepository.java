package cz.ajraa.tournament.team;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    boolean existsByTag(String tag);
}
