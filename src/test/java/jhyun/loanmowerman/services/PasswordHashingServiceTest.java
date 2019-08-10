package jhyun.loanmowerman.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PasswordHashingServiceTest {

    @InjectMocks
    private PasswordHashingService passwordHashingService;

    @Test
    public void testHash() {
        final String result = passwordHashingService.hash("foobar");
        final String result2 = passwordHashingService.hash("foobar");
        final String result3 = passwordHashingService.hash("foobarzoo");
        assertThat(result).isEqualTo(result2).isNotEqualTo(result3);
    }
}