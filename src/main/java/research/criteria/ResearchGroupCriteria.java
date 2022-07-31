package research.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroupCriteria {
    private String nameLike="";
    private int minCountOfResearchers=0;
    private int minBudget=0;

    private ResearchGroupOrderBy orderBy=ResearchGroupOrderBy.id;
    private OrderType orderType=OrderType.asc;
}