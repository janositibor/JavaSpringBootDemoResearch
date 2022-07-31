package research.service;

import research.exceptions.ProjectNotValidException;
import research.exceptions.ResearchGroupNotValidException;
import research.model.Location;
import research.model.Project;
import research.model.ResearchGroup;
import java.time.LocalDate;

public class Validation {
    public Project validProject(Project projectToValidate){
        boolean isValid=checkDate(projectToValidate.getStartDate(),Project.FIRST_DATE.minusDays(1),Project.LAST_DATE.plusDays(1))
                && checkNotBlankString(projectToValidate.getName())
                && checkNotNegativeInteger(projectToValidate.getBudget());

        if (!isValid) {
            throw new ProjectNotValidException(projectToValidate);
        }
        return projectToValidate;
    }

    public boolean checkNotBlankString(String s){
        return s != null && !s.isBlank();
    }

    public boolean checkDate(LocalDate actual, LocalDate start, LocalDate end){
        return actual.isAfter(start) && actual.isBefore(end);
    }

    public boolean checkNotNegativeInteger(int i){
        return i>=0;
    }

    public ResearchGroup validResearchGroup(ResearchGroup researchGroupToValidate) {
        boolean isValid=checkNotBlankString(researchGroupToValidate.getName())
                && checkDate(researchGroupToValidate.getFounded(),Project.FIRST_DATE.minusDays(1),LocalDate.now().plusDays(1))
                && checkNotNegativeInteger(researchGroupToValidate.getCountOfResearchers())
                && checkNotNegativeInteger(researchGroupToValidate.getBudget())
                && checkValidLocation(researchGroupToValidate.getLocation());

        if (!isValid) {
            throw new ResearchGroupNotValidException(researchGroupToValidate);
        }
        return researchGroupToValidate;
    }

    public boolean checkValidLocation(Location location) {
        for (Location validLocation : Location.values()) {
                if (validLocation==location) {
                    return true;
                }
            }
            return false;
    }
}
