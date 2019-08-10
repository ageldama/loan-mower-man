package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.tabular_data.InstituteIndexAndName;
import jhyun.loanmowerman.tabular_data.LoanAmountHistory;
import jhyun.loanmowerman.tabular_data.LoanAmountHistoryColumnarCsvReader;
import jhyun.loanmowerman.value_sanitizers.BankNameSanitizer;
import jhyun.loanmowerman.value_sanitizers.IntegerSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoanAmountHistoryService {

    private InstituteRepository instituteRepository;
    private LoanAmountRepository loanAmountRepository;

    private LoanAmountHistoryColumnarCsvReader csvReader;

    @Autowired
    public LoanAmountHistoryService(
            InstituteRepository instituteRepository,
            LoanAmountRepository loanAmountRepository
    ) {
        this.instituteRepository = instituteRepository;
        this.loanAmountRepository = loanAmountRepository;
        //
        this.csvReader = new LoanAmountHistoryColumnarCsvReader(
                new BankNameSanitizer(),
                new IntegerSanitizer()
        );
    }

    @Transactional
    public void saveCsv(final InputStream csvInputStream) throws IOException {
        final Iterator<LoanAmountHistory> it = this.csvReader.iterator(csvInputStream);
        while (it.hasNext()) {
            final LoanAmountHistory hist = it.next();
            // get-or-create(institute)
            final Map<InstituteIndexAndName, Integer> amountsPerInstitute = hist.getAmountsPerInstitute();
            for (InstituteIndexAndName idxAndName : amountsPerInstitute.keySet()) {
                final String instituteCode = Objects.toString(idxAndName.getIndex());
                Optional<Institute> institute = this.instituteRepository.findById(instituteCode);
                if (!institute.isPresent()) {
                    // not-found: create-new
                    final Institute institute_ = new Institute(instituteCode, idxAndName.getName());
                    this.instituteRepository.save(institute_);
                    institute = Optional.of(institute_);
                }
                // create(loan-amount)
                final Integer amount = amountsPerInstitute.get(idxAndName);
                this.loanAmountRepository.save(
                        new LoanAmount(institute.get(), hist.getYear(), hist.getMonth(), amount));
            }
        }
    }

    @Transactional
    public void purgeAll() {
        loanAmountRepository.deleteAll();
        instituteRepository.deleteAll();
    }

}
