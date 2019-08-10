package jhyun.loanmowerman.tabular_data;

import com.google.common.base.Strings;
import jhyun.loanmowerman.value_sanitizers.IntegerSanitizer;
import org.apache.commons.csv.CSVRecord;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoanAmountHistoryIterator implements Iterator<LoanAmountHistory> {
    private Iterator<CSVRecord> it;
    private List<InstituteIndexAndName> instituteIndexAndNameList;
    private IntegerSanitizer integerSanitizer;

    public LoanAmountHistoryIterator(final Iterator<CSVRecord> it,
                                     final List<InstituteIndexAndName> instituteIndexAndNameList,
                                     final IntegerSanitizer integerSanitizer) {
        this.it = it;
        this.instituteIndexAndNameList = instituteIndexAndNameList;
        this.integerSanitizer = integerSanitizer;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public LoanAmountHistory next() {
        if (!this.it.hasNext()) {
            throw new RuntimeException("expected more records");
        }

        final CSVRecord record = it.next();

        final Integer year = Integer.valueOf(record.get(0)); // year
        final Integer month = Integer.valueOf(record.get(1)); // month
        final Map<InstituteIndexAndName, Integer> amountsPerInstitute = new HashMap<>();

        for (InstituteIndexAndName institute : instituteIndexAndNameList) {
            final String val = record.get(institute.getIndex());
            if (Strings.isNullOrEmpty(val)) continue;
            final Integer amount = this.integerSanitizer.sanitize(val);
            amountsPerInstitute.put(institute, amount);
        }

        return new LoanAmountHistory(year, month, amountsPerInstitute);
    }
}
