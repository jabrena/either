package info.jab.examples;

import info.jab.fp.util.Either;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EitherReadmeExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger(EitherReadmeExamplesTest.class);

    @Test
    void showLearnAboutEither() {
        //1. Learn to instanciate an Either object.
        enum ConnectionProblem {
            INVALID_URL,
            INVALID_CONNECTION,
        }

        Either<ConnectionProblem, String> resultLeft = Either.left(ConnectionProblem.INVALID_URL);
        Either<ConnectionProblem, String> resultRight = Either.right("Success");

        Either<ConnectionProblem, String> eitherLeft = new Either.Left<>(ConnectionProblem.INVALID_CONNECTION);
        Either<ConnectionProblem, String> eitherRight = new Either.Right<>("Success");

        //2. Learn to use Either to not propagate Exceptions any more
        Function<String, Either<ConnectionProblem, URI>> toURI = address -> {
            try {
                var uri = new URI(address);
                return Either.right(uri);
            } catch (URISyntaxException | IllegalArgumentException ex) {
                logger.warn(ex.getLocalizedMessage(), ex);
                return Either.left(ConnectionProblem.INVALID_URL);
            }
        };

        //3. Process results
        Function<Either<ConnectionProblem, URI>, String> process = param -> {
            return switch (param) {
                case Either.Right<ConnectionProblem, URI> right -> right.get().toString();
                case Either.Left<ConnectionProblem, URI> left -> "";
            };
        };

        var case1 = "https://www.juanantonio.info";
        var result = toURI.andThen(process).apply(case1);
        System.out.println("Result: " + result);

        var case2 = "https://";
        var result2 = toURI.andThen(process).apply(case2);
        System.out.println("Result: " + result2);

        // @formatter:off

        Function<Either<ConnectionProblem, URI>, String> process2 = param -> {
            return switch (param) {
                case Either.Right<ConnectionProblem, URI> e when e.isRight() -> e.get().toString();
                case Either.Left<ConnectionProblem, URI> e when e.isLeft() -> "";
                default -> ""; //TODO the switch expression does not cover all possible input values 
            };
        };

        // @formatter:on

        var case3 = "https://www.juanantonio.info";
        var result3 = toURI.andThen(process2).apply(case1);
        System.out.println("Result: " + result3);
    }
}
