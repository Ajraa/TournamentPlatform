package cz.ajraa.tournament.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRegistrationDtoValidator implements ConstraintValidator<ValidUserRegistration, UserRegistrationDto> {
    @Override
    public boolean isValid(UserRegistrationDto dto, ConstraintValidatorContext context) {
        if (dto.getRole() == RoleType.PLAYER) return true;

        boolean isValid = true;

        if (isBlank(dto.getFirstName())) {
            addError(context, "Jméno je pro zakladatele povinné.", "firstName");
            isValid = false;
        }
        if (isBlank(dto.getLastName())) {
            addError(context, "Příjmení je pro zakladatele povinné.", "lastName");
            isValid = false;
        }
        if (isBlank(dto.getBankAccount())) {
            addError(context, "Bankovní účet je pro zakladatele povinný.", "bankAccount");
            isValid = false;
        }
        if (isBlank(dto.getStreet())) {
            addError(context, "Ulice je pro zakladatele povinná.", "street");
            isValid = false;
        }
        if (isBlank(dto.getCity())) {
            addError(context, "Město je pro zakladatele povinné.", "city");
            isValid = false;
        }
        if (isBlank(dto.getPostcode())) {
            addError(context, "PSČ je pro zakladatele povinné.", "postcode");
            isValid = false;
        }
        if (isBlank(dto.getCountry())) {
            addError(context, "Země je pro zakladatele povinná.", "country");
            isValid = false;
        }
        if (isBlank(dto.getHouseNumber()))
        {
            addError(context, "Číslo domu je pro zakladatele povinná.", "houseNumber");
            isValid = false;
        }

        return isValid;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void addError(ConstraintValidatorContext context, String message, String field) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
