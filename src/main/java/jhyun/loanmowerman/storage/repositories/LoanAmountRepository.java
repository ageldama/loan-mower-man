package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface LoanAmountRepository extends PagingAndSortingRepository<LoanAmount, Integer> {

    @Query("SELECT DISTINCT l.year FROM LoanAmount l ORDER BY l.year ASC")
    Collection<Integer> listAllYears();

    @Query("SELECT SUM(l.amount) FROM LoanAmount l WHERE l.year = ?2 AND l.institute.code = ?1")
    Long sumAmountOfYearByInstitute(String instituteCode, Integer year);

    @Query("SELECT AVG(l.amount) FROM LoanAmount l WHERE l.month = ?2 AND l.institute.code = ?1")
    Long averageAmountOfMonthByInstitute(String instituteCode, Integer month);

    @Query("SELECT AVG(l.amount) FROM LoanAmount l WHERE l.institute.code = ?1")
    Long averageAmountByInstitute(String instituteCode);

    @Query("SELECT l FROM LoanAmount l WHERE l.institute.code = ?1 ORDER BY l.year ASC")
    Iterable<LoanAmount> findByInstituteOrderByYearAsc(String instituteCode);
}
