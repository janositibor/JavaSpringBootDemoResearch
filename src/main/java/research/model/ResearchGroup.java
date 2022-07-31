package research.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "research_groups")
public class ResearchGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="researchgroup_name")
    private String name;
    private LocalDate founded;
    private int countOfResearchers;
    @Enumerated(EnumType.STRING)
    private Location location;
    private int budget;
    @ManyToMany(mappedBy="researchGroupSet")
    private Set<Project> projectSet = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchGroup that = (ResearchGroup) o;
        return name.equals(that.name) && founded.equals(that.founded) && location == that.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, founded, location);
    }
}