package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.ApiUser;
import jhyun.loanmowerman.storage.repositories.ApiUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApiUserServiceTest {

    @Mock
    private PasswordHashingService passwordHashingService;

    @Mock
    private ApiUserRepository apiUserRepository;

    @InjectMocks
    private ApiUserService apiUserService;

    @Test
    public void testSignUpOk() {
        mockPasswordHashingService();
        //
        final Optional<ApiUser> apiUser = apiUserService.signUp("foobar", "spameggs");
        //
        final ApiUser expect = new ApiUser("foobar", ">>>spameggs<<<");
        verify(apiUserRepository, times(1))
                .save(eq(expect));
        assertThat(apiUser).isEqualTo(expect);
    }

    private void mockPasswordHashingService() {
        given(passwordHashingService.hash(anyString()))
                .will(invocation -> {
                    String input = invocation.getArgument(0);
                    return String.format(">>>%s<<<", input);
                });
    }

    @Test
    public void testSignInOk() {
        mockPasswordHashingService();
        given(apiUserRepository.findById(anyString()))
                .will(invocation ->
                        Optional.of(new ApiUser(invocation.getArgument(0), ">>>foo<<<")));
        //
        final Optional<ApiUser> result = apiUserService.signIn("foobar", "foo");
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(new ApiUser("foobar", ">>>foo<<<"));
    }

    @Test
    public void testSignInNotFound() {
        mockPasswordHashingService();
        given(apiUserRepository.findById(anyString()))
                .will(invocation -> Optional.empty());
        //
        final Optional<ApiUser> result = apiUserService.signIn("foobar", "foo");
        assertThat(result).isNotPresent();
    }

    @Test
    public void testSignInPasswordMismatch() {
        given(passwordHashingService.hash(anyString())).willReturn("");
        given(apiUserRepository.findById(anyString()))
                .will(invocation ->
                        Optional.of(new ApiUser(invocation.getArgument(0), ">>><<<")));
        //
        final Optional<ApiUser> result = apiUserService.signIn("foobar", "foo");
        assertThat(result).isNotPresent();
    }
}