package jhyun.loanmowerman.value_sanitizers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class IntegerSanitizerTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"12345", 12345},
                {"'314'", 314}, // Quotes
                {"\"314\"", 314}, // Double Quotes
                {"3.1.4", 314}, // Period
                {"3,1,4", 314}, // Comma
                {"abc", null}, // NOTE: no `NumberFormatException`
                {"", null},
                {null, null}
        });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public Integer expect;

    private IntegerSanitizer sanitizer = new IntegerSanitizer();

    @Test
    public void test() {
        final Integer result = sanitizer.sanitize(input);
        assertThat(result).isEqualTo(expect);
    }
}