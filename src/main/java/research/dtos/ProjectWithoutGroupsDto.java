package research.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWithoutGroupsDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private int budget;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectWithoutGroupsDto that = (ProjectWithoutGroupsDto) o;
        return budget == that.budget && Objects.equals(name, that.name) && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate, budget);
    }
}
