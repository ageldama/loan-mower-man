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
    public Optional<ApiUser> signUp(final String id, final String password) {
        Optional<ApiUser> apiUser = apiUserRepository.findById(id);
        if (apiUser.isPresent()) {
            return Optional.empty(); // Should not create/overwrite!
        } else {
            final ApiUser apiUser_ = new ApiUser(id, passwordHashingService.hash(password));
            apiUserRepository.save(apiUser_);
            return Optional.of(apiUser_);
        }
    }

    public Optional<ApiUser> signIn(final String id, final String password) {
        final String passwordHash = passwordHashingService.hash(password);
        final Optional<ApiUser> apiUser = apiUserRepository.findById(id);
        if (apiUser.isPresent() && passwordHash.equals(apiUser.get().getPassword())) {
            return apiUser;
        }
        return Optional.empty();
    }

    public Optional<ApiUser> findById(final String id) {
        return apiUserRepository.findById(id);
    }

    // Testing Support
    @Transactional
    public void purgeAllApiUsers() {
        apiUserRepository.deleteAll();
    }
}
