package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WekaArffService {

    private LoanAmountRepository loanAmountRepository;

    @Autowired
    public WekaArffService(LoanAmountRepository loanAmountRepository) {
        this.loanAmountRepository = loanAmountRepository;
    }

    // TODO: writeLoanHistoryToFile

    public void f() {

    }
}
