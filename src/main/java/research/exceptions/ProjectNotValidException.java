package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import research.model.Project;
import java.net.URI;

public class ProjectNotValidException extends AbstractThrowableProblem {
    public ProjectNotValidException(Project project) {
        super(URI.create("projects/not-valid"),
                "Not Valid",
                Status.BAD_REQUEST,
                String.format("Project (name:%s, startDate:%s, budget:%d) not valid", project.getName(),project.getStartDate(),project.getBudget()));
    }
}
