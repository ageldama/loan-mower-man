package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.Institute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface InstituteRepository extends PagingAndSortingRepository<Institute, String> {

    @Query("SELECT i.name FROM Institute i")
    Collection<String> listAllInstituteNames();
}
