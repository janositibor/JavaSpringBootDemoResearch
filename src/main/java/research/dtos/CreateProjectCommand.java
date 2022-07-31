package research.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectCommand {
    @Schema(description = "Name of project",example ="Ampa receptor vizsgálata")
    @NotBlank(message = "The name of the project mustn't be blank!")
    private String name;
    @Schema(description = "Starting date of the project",example ="2022-07-19")
    @NotNull(message = "The starting date of the project must be valid!")
    private LocalDate startDate;
    @Schema(description = "Budget of the project in million HUF",example ="52")
    @PositiveOrZero(message = "The budget of project mustn't be negative!")
    private int budget;
}
