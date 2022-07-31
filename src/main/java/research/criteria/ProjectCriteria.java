package research.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import research.model.Project;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCriteria {
    private String nameLike="";
    private LocalDate startBefore=Project.LAST_DATE;
    private LocalDate startAfter=Project.FIRST_DATE;
    private int minBudget=0;

    private ProjectOrderBy orderBy=ProjectOrderBy.id;
    private OrderType orderType=OrderType.asc;
}
