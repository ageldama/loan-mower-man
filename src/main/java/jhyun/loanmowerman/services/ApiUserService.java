package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.ApiUser;
import jhyun.loanmowerman.storage.repositories.ApiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ApiUserService {

    private ApiUserRepository apiUserRepository;

    private PasswordHashingService passwordHashingService;

    @Autowired
    public ApiUserService(
            ApiUserRepository apiUserRepository,
            PasswordHashingService passwordHashingService
    ) {
        this.apiUserRepository = apiUserRepository;
        this.passwordHashingService = passwordHashingService;
    }

    @Transactional
    public ApiUser signUp(final String id, final String password) {
        final ApiUser apiUser = new ApiUser(id, passwordHashingService.hash(password));
        apiUserRepository.save(apiUser);
        return apiUser;
    }

    public Optional<ApiUser> signIn(final String id, final String password) {
        final String passwordHash = passwordHashingService.hash(password);
        final Optional<ApiUser> apiUser = apiUserRepository.findById(id);
        if (apiUser.isPresent() && passwordHash.equals(apiUser.get().getPassword())) {
            return apiUser;
        }
        return Optional.empty();
    }
}
