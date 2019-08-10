package jhyun.loanmowerman.value_sanitizers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class BankNameSanitizerTest {

    @Parameterized.Parameters
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][]{
                {"주택도시기금1)(억원)", "주택도시기금"},
                {"국민은행(억원)", "국민은행"},
                {"우리은행(억원)", "우리은행"},
                {"신한은행(억원)", "신한은행"},
                {"한국시티은행(억원)", "한국시티은행"},
                {"하나은행(억원)", "하나은행"},
                {"농협은행/수협은행(억원)", "농협은행/수협은행"},
                {"외환은행(억원)", "외환은행"},
                {"기타은행(억원)", "기타은행"},
                {"DB", "DB"},
                {"", ""},
                {null, null}
        });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public String expect;

    private BankNameSanitizer sanitizer = new BankNameSanitizer();

    @Test
    public void test() {
        final String result = sanitizer.sanitize(input);
        assertThat(result).isEqualTo(expect);
    }
}