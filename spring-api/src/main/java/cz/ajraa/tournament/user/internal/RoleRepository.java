package cz.ajraa.tournament.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType roleType);
}
