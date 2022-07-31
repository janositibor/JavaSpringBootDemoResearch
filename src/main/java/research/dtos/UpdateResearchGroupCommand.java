package research.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import research.model.Location;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResearchGroupCommand {
    @Schema(description = "Name of research group",example ="Molekuláris sejtbiológia")
    private String name;
    @Schema(description = "Date of foundation of the research group",example ="2018-07-19")
    private LocalDate founded;
    @Schema(description = "Number of researchers in the group",example ="5")
    private Integer countOfResearchers;
    @Schema(description = "Location of the research group",example ="ÁOK_RÉGI_ÉPÜLET")
    private Location location;
    @Schema(description = "Budget of the research group in million HUF",example ="12")
    private Integer budget;
}
