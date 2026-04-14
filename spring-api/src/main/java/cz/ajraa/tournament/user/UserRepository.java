package cz.ajraa.tournament.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByEmail(String email);
    public boolean existsByNickname(String nickname);
    public Optional<User> findByNickname(String nickname);
}
