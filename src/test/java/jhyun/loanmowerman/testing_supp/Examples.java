package jhyun.loanmowerman.testing_supp;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 테스팅용 예제 리소스/픽스쳐
 */
@Slf4j
public final class Examples {

    public static URL exampleCsv() {
        return Resources.getResource("example01-utf8.csv");
    }

    public static String urlAsString(final URL url) throws IOException {
        return IOUtils.toString(url, Charsets.UTF_8);
    }

    public static InputStream urlAsInputStream(final URL url) throws IOException {
        return url.openStream();
    }

    @Test
    public void testExampleCsv() {
        final URL url = exampleCsv();
        assertThat(url).isNotNull();
    }

    @Test
    public void testExampleCsvAsString() throws IOException {
        final String content = urlAsString(exampleCsv());
        assertThat(content).isNotEmpty();
        log.trace(content);
    }

    @Test
    public void testExampleCsvAsInputStream() throws IOException {
        final InputStream inputStream = urlAsInputStream(exampleCsv());
        assertThat(inputStream).isNotNull();
        final String content = IOUtils.toString(inputStream, Charsets.UTF_8);
        assertThat(content).isNotBlank();
        log.trace(content);
    }
}
