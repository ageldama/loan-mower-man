package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.LoanAmount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface LoanAmountRepository extends PagingAndSortingRepository<LoanAmount, Integer> {

    @Query("SELECT DISTINCT l.year FROM LoanAmount l ORDER BY l.year ASC")
    Collection<Integer> listAllYears();

    @Query("SELECT SUM(l.amount) FROM LoanAmount l WHERE l.year = ?2 AND l.institute.code = ?1")
    Long sumAmountOfYearByInstitute(String instituteCode, Integer year);

    @Query("SELECT AVG(l.amount) FROM LoanAmount l WHERE l.month = ?2 AND l.institute.code = ?1")
    Long averageAmountOfYearAndMonthByInstitute(String instituteCode, Integer month);
}
