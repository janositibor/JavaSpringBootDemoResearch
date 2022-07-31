package research.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import research.model.Location;
import research.model.ResearchGroup;

import java.util.List;

public interface ResearchGroupsRepository extends JpaRepository<ResearchGroup,Long> {
    @Query("select  rg from ResearchGroup rg where (:nameLike ='' or rg.name like %:nameLike%) and (:minCountOfResearchers is null or rg.countOfResearchers>=:minCountOfResearchers) and (:minBudget is null or rg.budget>=:minBudget)")
    List<ResearchGroup> findAllByCriteria(@Param("nameLike") String nameLike, @Param("minCountOfResearchers") Integer minCountOfResearchers, @Param("minBudget")  Integer minBudget);

    ResearchGroup findByNameIgnoreCaseAndLocation(String name, Location location);
}
