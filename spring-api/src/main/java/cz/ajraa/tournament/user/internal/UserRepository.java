package cz.ajraa.tournament.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    @Query("Select u from User u JOIN FETCH u.roles WHERE u.nickname = :nickname")
    Optional<User> findByNickname(@Param("nickname") String nickname);
}
