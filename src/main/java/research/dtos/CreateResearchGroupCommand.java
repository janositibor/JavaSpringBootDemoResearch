package research.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import research.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateResearchGroupCommand {
    @Schema(description = "Name of research group",example ="Molekuláris sejtbiológia")
    @NotBlank(message = "The name of the research group mustn't be blank!")
    private String name;
    @Schema(description = "Date of foundation of the research group",example ="2019-07-19")
    @NotNull(message = "The research group's date of foundation must be valid!")
    private LocalDate founded;
    @Schema(description = "Number of researchers in the group",example ="4")
    @Positive(message = "The number of researchers in the research group must be positive!")
    private int countOfResearchers;
    @Schema(description = "Location of the research group",example ="ÁOK_ÚJ_ÉPÜLET")
    private Location location;
    @Schema(description = "Budget of the research group in million HUF",example ="7")
    @PositiveOrZero(message = "The budget of research group mustn't be negative!")
    private int budget;
}
