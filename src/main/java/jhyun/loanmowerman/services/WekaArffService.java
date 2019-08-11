package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.util.stream.Stream;

@Service
public class WekaArffService {

    private LoanAmountRepository loanAmountRepository;

    @Autowired
    public WekaArffService(LoanAmountRepository loanAmountRepository) {
        this.loanAmountRepository = loanAmountRepository;
    }

    @Transactional(readOnly = true)
    public void writeLoanHistoryToFile(PrintWriter writer, final String instituteCode) {
        writer.println("@RELATION loan");
        writer.println();
        writer.println("@ATTRIBUTE year NUMERIC");
        writer.println("@ATTRIBUTE month NUMERIC");
        writer.println("@ATTRIBUTE amount NUMERIC");
        writer.println();
        writer.println("@DATA");
        try (Stream<LoanAmount> stream = loanAmountRepository.findByInstituteOrderByYearAsc(instituteCode)) {
            stream.forEach(loanAmount -> writer.println(String.format("%s,%s,%s",
                    loanAmount.getYear(), loanAmount.getMonth(), loanAmount.getAmount())));
        }
        writer.flush();
    }
}
