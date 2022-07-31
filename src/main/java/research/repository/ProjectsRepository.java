package research.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import research.model.Project;
import research.model.ResearchGroup;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProjectsRepository extends JpaRepository<Project,Long> {
    @Query("FROM Project p WHERE :researchGroup MEMBER p.researchGroupSet")
    List<Project> findProjectsByParticipatingGroup(@Param("researchGroup") ResearchGroup researchGroup);

    @Query("select  p from Project p where (:nameLike ='' or p.name like %:nameLike%) and (p.startDate<=:startBefore) and (p.startDate>=:startAfter) and (:minBudget is null or p.budget>=:minBudget)")
    List<Project> findAllByCriteria(@Param("nameLike") String nameLike, @Param("startBefore") LocalDate startBefore, @Param("startAfter") LocalDate startAfter, @Param("minBudget") int minBudget);

    Project findByNameIgnoreCase(String name);
}
