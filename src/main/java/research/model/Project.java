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
@Table(name="projects")
public class Project {
    public static final LocalDate FIRST_DATE=LocalDate.of(1899, 12, 31);
    public static final LocalDate LAST_DATE=LocalDate.of(2100, 12, 31);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="project_name")
    private String name;
    private LocalDate startDate;
    private int budget;
    @ManyToMany
    @JoinTable(name="project_researchgroup",
            joinColumns=@JoinColumn(name="project_ID"),
            inverseJoinColumns=@JoinColumn(name="researchgroup_ID"))
    private Set<ResearchGroup> researchGroupSet = new HashSet<>();

    public void addGroup(ResearchGroup researchGroup){
        researchGroupSet.add(researchGroup);
    }
    public void removeGroup(ResearchGroup researchGroup){
        researchGroupSet.remove(researchGroup);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return name.equals(project.name) && startDate.equals(project.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startDate);
    }
}
