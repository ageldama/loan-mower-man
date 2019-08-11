package jhyun.loanmowerman.services;

import com.google.common.base.Verify;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.MinMaxOfInstitute;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.YearAndAmountEntry;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.google.common.base.Verify.verify;

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
        // NOTE: maybe I can introduce new custom query and its' matching Repository-Spring-Data-interface by using GROUP-BY statement.
        Institute mostInstitute = null;
        Long mostAmount = 0L;
        for (Institute institute : institutes) {
            Long sum = loanAmountRepository.sumAmountOfYearByInstitute(institute.getCode(), year);
            log.trace("year({}) institute({}) -- {}", year, institute.getCode(), sum);
            if (sum != null && mostAmount < sum) {
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

    /**
     * 외환은행의 지급액 평균(연도별) 최소/최대값?
     * 전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력하는 API 개발
     * o 예를들어, 2005 년 ~ 2016 년 외환은행의 평균 지원금액 (매년 12 달의 지원금액 평균값)을 계산하여 가장 작은 값과 큰 값을 출력합니다.
     * 소수점 이하는 반올림해서 계산하세요.
     */
    public MinMaxOfInstitute findMinMaxOfInstitute(final String instituteNamePart) {
        final ArrayList<String> instituteCodes = new ArrayList<>(instituteRepository.findInstituteCodeByName(instituteNamePart));
        verify(instituteCodes.size() == 1);
        final String instituteCode = instituteCodes.get(0);
        final Optional<Institute> institute = instituteRepository.findById(instituteCode);

        // NOTE: maybe I can introduce new custom query and its' matching Repository-Spring-Data-interface by using GROUP-BY statement.
        Integer minYear = 0;
        Long minAmount = Long.MAX_VALUE;
        Integer maxYear = 0;
        Long maxAmount = 0L;
        final Collection<Integer> years = loanAmountRepository.listAllYears();
        for (Integer year : years) {
            Long sumOfYear = loanAmountRepository.sumAmountOfYearByInstitute(instituteCode, year);
            if (minAmount > sumOfYear) {
                minYear = year;
                minAmount = sumOfYear;
            }
            if (maxAmount < sumOfYear) {
                maxYear = year;
                maxAmount = sumOfYear;
            }
        }

        return MinMaxOfInstitute.builder()
                .instituteName(institute.get().getName())
                .minMax(ImmutableList.of(
                        YearAndAmountEntry.builder().year(minYear).amount(minAmount).build(),
                        YearAndAmountEntry.builder().year(maxYear).amount(maxAmount).build()
                ))
                .build();
    }
}
