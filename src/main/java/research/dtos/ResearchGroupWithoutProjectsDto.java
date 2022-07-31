package research.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import research.model.Location;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchGroupWithoutProjectsDto {
    private Long id;
    private String name;
    private LocalDate founded;
    private int countOfResearchers;
    private Location location;
    private int budget;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchGroupWithoutProjectsDto that = (ResearchGroupWithoutProjectsDto) o;
        return countOfResearchers == that.countOfResearchers && budget == that.budget && Objects.equals(name, that.name) && Objects.equals(founded, that.founded) && location == that.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, founded, countOfResearchers, location, budget);
    }
}
