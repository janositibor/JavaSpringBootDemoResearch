package research.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import research.model.Location;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroupDto {
    private Long id;
    private String name;
    private LocalDate founded;
    private int countOfResearchers;
    private Location location;
    private int budget;
    private Set<ProjectWithoutGroupsDto> projectSet = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchGroupDto that = (ResearchGroupDto) o;
        return countOfResearchers == that.countOfResearchers && budget == that.budget && Objects.equals(name, that.name) && Objects.equals(founded, that.founded) && location == that.location && Objects.equals(projectSet, that.projectSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, founded, countOfResearchers, location, budget, projectSet);
    }
}
