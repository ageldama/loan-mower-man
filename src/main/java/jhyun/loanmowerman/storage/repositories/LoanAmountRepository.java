package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.LoanAmount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface LoanAmountRepository extends PagingAndSortingRepository<LoanAmount, Integer> {

    @Query("SELECT DISTINCT l.year FROM LoanAmount l ORDER BY l.year ASC")
    Collection<Integer> listAllYears();

    @Query("SELECT DISTINCT l.month FROM LoanAmount l WHERE l.year =?1 ORDER BY l.month ASC")
    Collection<Integer> listAllMonthsOfYear(Integer year);

    @Query("SELECT SUM(l.amount) FROM LoanAmount l WHERE l.year = ?2 AND l.institute.code = ?1")
    Long sumAmountOfYearByInstitute(String instituteCode, Integer year);

    @Query("SELECT AVG(l.amount) FROM LoanAmount l WHERE l.month = ?2 AND l.institute.code = ?1")
    Long averageAmountOfMonthByInstitute(String instituteCode, Integer month);

    @Query("SELECT AVG(l.amount) FROM LoanAmount l WHERE l.institute.code = ?1")
    Long averageAmountByInstitute(String instituteCode);

    @Query("SELECT l FROM LoanAmount l WHERE l.institute.code = ?1 ORDER BY l.year ASC")
    Stream<LoanAmount> findByInstituteOrderByYearAsc(String instituteCode);

    @Query("SELECT l FROM LoanAmount l WHERE l.institute.code = ?1 AND l.year = ?2 AND l.month = ?3")
    Optional<LoanAmount> findExactOne(String instituteCode, Integer year, Integer month);
}
