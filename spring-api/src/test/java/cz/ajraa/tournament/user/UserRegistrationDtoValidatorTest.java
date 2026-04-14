package cz.ajraa.tournament.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationDtoValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private UserRegistrationDto validPlayerDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("hrac@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("hrac1");
        dto.setRole(RoleType.PLAYER);
        return dto;
    }

    private UserRegistrationDto validFounderDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("zakladatel@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("zakladatel1");
        dto.setRole(RoleType.FOUNDER);
        dto.setFirstName("Jan");
        dto.setLastName("Novák");
        dto.setBankAccount("123456789/0100");
        dto.setStreet("Hlavní");
        dto.setHouseNumber("1");
        dto.setCity("Praha");
        dto.setPostcode("10000");
        dto.setCountry("CZ");
        return dto;
    }

    private boolean hasViolationOnField(Set<ConstraintViolation<UserRegistrationDto>> violations, String field) {
        return violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    // ─── player – základní validace ────────────────────────────────────────

    @Test
    void player_validniDto_bezChyb() {
        assertThat(validator.validate(validPlayerDto())).isEmpty();
    }

    @Test
    void player_prazdnyEmail_chybaEmail() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setEmail("");
        assertThat(hasViolationOnField(validator.validate(dto), "email")).isTrue();
    }

    @Test
    void player_nullEmail_chybaEmail() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setEmail(null);
        assertThat(hasViolationOnField(validator.validate(dto), "email")).isTrue();
    }

    @Test
    void player_spatnyFormatEmailu_chybaEmail() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setEmail("neni-email");
        assertThat(hasViolationOnField(validator.validate(dto), "email")).isTrue();
    }

    @Test
    void player_kratkeHeslo_chybaPassword() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setPassword("1234567"); // 7 znaků — pod minimem 8
        assertThat(hasViolationOnField(validator.validate(dto), "password")).isTrue();
    }

    @Test
    void player_prazdneHeslo_chybaPassword() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setPassword("");
        assertThat(hasViolationOnField(validator.validate(dto), "password")).isTrue();
    }

    @Test
    void player_hesloPresnoOsmiZnaku_bezChyb() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setPassword("12345678"); // přesně 8
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void player_prazdneNickname_chybaNickname() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setNickname("");
        assertThat(hasViolationOnField(validator.validate(dto), "nickname")).isTrue();
    }

    @Test
    void player_nullNickname_chybaNickname() {
        UserRegistrationDto dto = validPlayerDto();
        dto.setNickname(null);
        assertThat(hasViolationOnField(validator.validate(dto), "nickname")).isTrue();
    }

    // ─── founder – validace specifická pro roli ─────────────────────────────

    @Test
    void founder_validniDto_bezChyb() {
        assertThat(validator.validate(validFounderDto())).isEmpty();
    }

    @Test
    void founder_chybejiciFirstName_chybaFirstName() {
        UserRegistrationDto dto = validFounderDto();
        dto.setFirstName(null);
        assertThat(hasViolationOnField(validator.validate(dto), "firstName")).isTrue();
    }

    @Test
    void founder_prazdnyFirstName_chybaFirstName() {
        UserRegistrationDto dto = validFounderDto();
        dto.setFirstName("   ");
        assertThat(hasViolationOnField(validator.validate(dto), "firstName")).isTrue();
    }

    @Test
    void founder_chybejiciLastName_chybaLastName() {
        UserRegistrationDto dto = validFounderDto();
        dto.setLastName(null);
        assertThat(hasViolationOnField(validator.validate(dto), "lastName")).isTrue();
    }

    @Test
    void founder_chybejiciBankAccount_chybaBankAccount() {
        UserRegistrationDto dto = validFounderDto();
        dto.setBankAccount(null);
        assertThat(hasViolationOnField(validator.validate(dto), "bankAccount")).isTrue();
    }

    @Test
    void founder_chybejiciStreet_chybaStreet() {
        UserRegistrationDto dto = validFounderDto();
        dto.setStreet(null);
        assertThat(hasViolationOnField(validator.validate(dto), "street")).isTrue();
    }

    @Test
    void founder_chybejiciHouseNumber_chybaHouseNumber() {
        UserRegistrationDto dto = validFounderDto();
        dto.setHouseNumber(null);
        assertThat(hasViolationOnField(validator.validate(dto), "houseNumber")).isTrue();
    }

    @Test
    void founder_chybejiciCity_chybaCity() {
        UserRegistrationDto dto = validFounderDto();
        dto.setCity(null);
        assertThat(hasViolationOnField(validator.validate(dto), "city")).isTrue();
    }

    @Test
    void founder_chybejiciPostcode_chybaPostcode() {
        UserRegistrationDto dto = validFounderDto();
        dto.setPostcode(null);
        assertThat(hasViolationOnField(validator.validate(dto), "postcode")).isTrue();
    }

    @Test
    void founder_chybejiciCountry_chybaCountry() {
        UserRegistrationDto dto = validFounderDto();
        dto.setCountry(null);
        assertThat(hasViolationOnField(validator.validate(dto), "country")).isTrue();
    }

    @Test
    void founder_viceMissingPoli_vsechnaChybySoucastiVypisu() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("zakladatel@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("zakladatel1");
        dto.setRole(RoleType.FOUNDER);
        // žádné zakladatelské pole

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(8);
        for (String field : new String[]{"firstName", "lastName", "bankAccount", "street", "houseNumber", "city", "postcode", "country"}) {
            assertThat(hasViolationOnField(violations, field))
                .as("očekávána chyba pro pole '%s'", field)
                .isTrue();
        }
    }

    // ─── výchozí role ────────────────────────────────────────────────────────

    @Test
    void player_roleNullPoDefault_bezChybValidator() {
        // role má default PLAYER — null role obchází validator, ten zpracuje null jako non-PLAYER
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("test@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("test1");
        // role není nastavena — default je PLAYER přes field initializer
        assertThat(validator.validate(dto)).isEmpty();
    }
}
