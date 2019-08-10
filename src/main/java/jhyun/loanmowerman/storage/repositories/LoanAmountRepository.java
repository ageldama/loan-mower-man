package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.LoanAmount;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LoanAmountRepository extends PagingAndSortingRepository<LoanAmount, Integer> {
}
