package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.ApiUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApiUserRepository extends PagingAndSortingRepository<ApiUser, String> {
}
