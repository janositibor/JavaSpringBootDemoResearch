package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import research.model.ResearchGroup;
import java.net.URI;

public class ResearchGroupAlreadyExistsException extends AbstractThrowableProblem {
    public ResearchGroupAlreadyExistsException(ResearchGroup researchGroup,long id) {
        super(URI.create("research-groups/already-exists"),
                "Already Exists",
                Status.CONFLICT,
                String.format("Group (name:%s) already exists with id: %d", researchGroup.getName(), id));
    }
}
