package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import research.model.Project;
import java.net.URI;

public class ProjectAlreadyExistsException extends AbstractThrowableProblem {
    public ProjectAlreadyExistsException(Project project, long id) {
        super(URI.create("projects/already-exists"),
                "Already Exists",
                Status.CONFLICT,
                String.format("Project (name:%s) already exists with id: %d", project.getName(), id));
    }
}
