package info.jab.concepts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MethodSignatureTest {

    Function<String, Integer> parseInt = param -> {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            return -99;
        }
    };

    Function<String, Integer> parseInt2 = param -> {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Katakroker");
        }
    };

    // @formatter:off
    private static List<String> generateNumbers() {
        return IntStream.rangeClosed(-100, 100)
            .mapToObj(String::valueOf)
            .toList();
    }

    // @formatter:on

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers(String input) {
        int result = parseInt.apply(input);
        assertThat(result + "").isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("generateNumbers")
    void should_parse_valid_integers_v2(String input) {
        int result = parseInt2.apply(input);
        assertThat(result + "").isEqualTo(input);
    }

    // @formatter:off
    public static List<String> getNonLetterChars() {
        final char[] punctuation = {'.', ',', '!', '@', '#', '$', '%', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', '\\', '|', ';', ':', '"', '\''};
        
        return Arrays.asList(punctuation).stream()
                .map(String::valueOf)
                .toList();
    }

    // @formatter:on

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_not_work_for_non_valid_integers(String input) {
        int expectedReslut = -99;
        int result = parseInt.apply(input);
        assertThat(result).isEqualTo(expectedReslut);
    }

    @ParameterizedTest
    @MethodSource("getNonLetterChars")
    void should_not_work_for_non_valid_integers_v2(String input) {
        assertThrows(RuntimeException.class, () -> parseInt2.apply(input), "Katakroker");
    }
}
