package jhyun.loanmowerman.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class LoanAmountHistoryAggregationService {

    private LoanAmountRepository loanAmountRepository;

    private InstituteRepository instituteRepository;

    @Autowired
    public LoanAmountHistoryAggregationService(
            LoanAmountRepository loanAmountRepository,
            InstituteRepository instituteRepository
    ) {
        this.loanAmountRepository = loanAmountRepository;
        this.instituteRepository = instituteRepository;
    }

    /**
     * 특정연도의 가장 지원금액 큰 은행
     */
    public Optional<Institute> mostLoanAllowedInstitute(final Integer year) {
        final List<Institute> institutes = Lists.newArrayList(instituteRepository.findAll());
        Institute mostInstitute = null;
        Long mostAmount = 0L;
        for (Institute institute : institutes) {
            Long sum = loanAmountRepository.sumAmountOfYearByInstitute(institute.getCode(), year);
            log.trace("year({}) institute({}) -- {}", year, institute.getCode(), sum);
            if (mostAmount < sum) {
                mostInstitute = institute;
                mostAmount = sum;
            }
        }
        return Optional.ofNullable(mostInstitute);
    }

    /**
     * 연도별 각 금융기관의 지원금액 합계
     */
    public Map<String, Object> totalLoanAmountsByYear() {
        final Map<String, Object> result = new HashMap<>();
        result.put("name", "주택금융 공급현황");
        final List<Map<String, Object>> entries = new ArrayList<>();
        result.put("entries", entries);

        final Collection<Integer> years = loanAmountRepository.listAllYears();
        final List<Institute> institutes = Lists.newArrayList(instituteRepository.findAll());
        for (Integer year : years) {
            Long total = 0L;
            final Map<String, Object> detailAmounts = new HashMap<>();
            for (Institute institute : institutes) {
                Long sum = loanAmountRepository.sumAmountOfYearByInstitute(institute.getCode(), year);
                log.trace("year({}) institute({}) -- {}", year, institute.getCode(), sum);
                detailAmounts.put(institute.getName(), sum);
                total += sum;
            }
            entries.add(ImmutableMap.<String, Object>builder()
                    .put("detail_amount", detailAmounts)
                    .put("total_amount", total)
                    .put("year", String.format("%s 년", year))
                    .build()
            );
        }

        return result;
    }

}
