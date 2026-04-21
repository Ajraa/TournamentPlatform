package cz.ajraa.tournament.tournament.internal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
class CreateTournamentDto {

    @NotBlank(message = "Jméno turnaje nesmí být prázdné.")
    private String name;

    @NotNull(message = "Začátek turnaje nemsmí být prázdný.")
    @Future(message = "Začátek turnaje se musí nacházet v budoucnosti.")
    private LocalDateTime startTime;

    private BigDecimal prize;

    private BigDecimal price;

    private Integer minimalRating;

    private Integer maximalRating;

    @NotNull(message = "Počet hráčů v týmu nesmí být prázdný.")
    @Min(value = 1, message = "Počet hráčů v týmu musí být minimálně 1.")
    private Integer playersPerTeam;

    @NotNull(message = "Minimální počet týmů v turnaji nesmí být prázdný.")
    @Min(value = 2, message = "Minimální počet týmů v turnaji musí být minimlně 2")
    private Integer minimalTeamAmount;

    @NotNull(message = "Maximální počet týmů v turnaji nesmí být prázdný.")
    @Min(value = 2, message = "Maximální počet týmů v turnaji musí být minimlně 2")
    private Integer maximalTeamAmount;
}
