package research.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import java.net.URI;

public class ParameterNotValidException extends AbstractThrowableProblem {
    public ParameterNotValidException(String message) {
        super(URI.create("parameter/not-valid"),
                "Not Valid Parameter",
                Status.BAD_REQUEST,
                String.format(message));
    }
}
