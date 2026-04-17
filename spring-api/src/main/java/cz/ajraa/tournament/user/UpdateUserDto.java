package cz.ajraa.tournament.user;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postcode;
    private String country;
    private String houseNumber;
    private String bankNumber;
}
