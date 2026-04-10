package cz.ajraa.tournament.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long userId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    @JsonIgnore
    private String passwordHash;
    private int rating;
    private float winrate;
    private String street;
    private String city;
    private String postcode;
    private String country;
    private String houseNumber;
    private String bankNumber;
    @ManyToMany(fetch =  FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
