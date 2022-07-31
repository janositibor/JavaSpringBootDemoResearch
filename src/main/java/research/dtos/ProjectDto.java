package research.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private int budget;
    private Set<ResearchGroupWithoutProjectsDto> researchGroupSet = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectDto that = (ProjectDto) o;
        return budget == that.budget && Objects.equals(name, that.name) && Objects.equals(startDate, that.startDate) && Objects.equals(researchGroupSet, that.researchGroupSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate, budget, researchGroupSet);
    }
}
