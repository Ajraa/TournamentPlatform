package cz.ajraa.tournament.user;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private int rating;
    private float winrate;
    private String street;
    private String city;
    private String postcode;
    private String country;
    private String houseNumber;
    private String bankNumber;
    private Set<RoleType> roles;
}
