package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.storage.entities.Institute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface InstituteRepository extends PagingAndSortingRepository<Institute, String> {

    @Query("SELECT i.name FROM Institute i")
    Collection<String> listAllInstituteNames();

    @Query("SELECT i.code FROM Institute i WHERE i.name LIKE CONCAT('%', ?1 ,'%')")
    Collection<String> findInstituteCodeByName(String instituteNamePart);
}
