package research.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectCommand {
    @Schema(description = "Name of project",example ="Ampa receptor szuperrezolúciós vizsgálata")
    private String name;
    @Schema(description = "Starting date of the project",example ="2022-08-19")
    private LocalDate startDate;
    @Schema(description = "Budget of the project in million HUF",example ="72")
    private Integer budget;
}
