package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import java.net.URI;

public class ProjectNotFoundException extends AbstractThrowableProblem {
    public ProjectNotFoundException(long id) {
        super(URI.create("projects/not-found"),
                "Not found",
                Status.NOT_FOUND,
                String.format("Project with id: %d not found", id));
    }
}