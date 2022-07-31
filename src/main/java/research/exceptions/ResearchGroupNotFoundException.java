package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import java.net.URI;

public class ResearchGroupNotFoundException extends AbstractThrowableProblem {
    public ResearchGroupNotFoundException(long id) {
        super(URI.create("research-groups/not-found"),
                "Not found",
                Status.NOT_FOUND,
                String.format("Research group with id: %d not found", id));
    }
}
