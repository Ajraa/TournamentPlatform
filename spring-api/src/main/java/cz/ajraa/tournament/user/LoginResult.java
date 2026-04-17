package cz.ajraa.tournament.user;

public record LoginResult(String token, UserDto userDto) {
}
