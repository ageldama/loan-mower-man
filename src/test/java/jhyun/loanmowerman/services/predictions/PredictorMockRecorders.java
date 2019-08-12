package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Component
public final class PredictorMockRecorders {
    public Stream<LoanAmount> loanAmounts(Institute institute) {
        return Stream.of(
                new LoanAmount(institute, 1982, 1, 1),
                new LoanAmount(institute, 1982, 2, 1),
                new LoanAmount(institute, 1982, 3, 2),
                new LoanAmount(institute, 1982, 4, 3),
                new LoanAmount(institute, 1982, 5, 5),
                new LoanAmount(institute, 1982, 6, 8),
                new LoanAmount(institute, 1982, 7, 13),
                new LoanAmount(institute, 1982, 8, 21),
                new LoanAmount(institute, 1982, 9, 34)
        );
    }

    public void recordLoanAmountHistory_findByInstituteOrderByYearAsc(LoanAmountRepository loanAmountRepositoryMock, String bankCode, String bankName) {
        final Institute institute = new Institute(bankCode, bankName);
        given(loanAmountRepositoryMock.findByInstituteOrderByYearAsc(anyString())).willReturn(
                loanAmounts(institute));
    }
}
