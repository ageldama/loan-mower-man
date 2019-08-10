package jhyun.loanmowerman.tabular_data;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import jhyun.loanmowerman.value_sanitizers.BankNameSanitizer;
import jhyun.loanmowerman.value_sanitizers.IntegerSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public final class LoanAmountHistoryColumnarCsvReader {
    private BankNameSanitizer bankNameSanitizer;
    private IntegerSanitizer integerSanitizer;

    public LoanAmountHistoryColumnarCsvReader(final BankNameSanitizer bankNameSanitizer,
                                              final IntegerSanitizer integerSanitizer) {
        this.bankNameSanitizer = bankNameSanitizer;
        this.integerSanitizer = integerSanitizer;
    }

    public static List<InstituteIndexAndName> collectInstituteIndexesAndNames(
            final BankNameSanitizer bankNameSanitizer,
            final CSVRecord headerRecord
    ) {
        final List<InstituteIndexAndName> result = new ArrayList<>();
        // Skip first two items (연도, 월)
        for (int i = 2; i < headerRecord.size(); i ++) {
            final String val = headerRecord.get(i);
            if (Strings.isNullOrEmpty(val)) {
                // met an empty column, no need to read adv columns
                break;
            }
            final String name = bankNameSanitizer.sanitize(val);
            result.add(new InstituteIndexAndName(i, name));
        }
        return result;
    }

    public Iterator<LoanAmountHistory> iterator(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
        final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);

        final Iterator<CSVRecord> it = parser.iterator();

        // collect headers
        if (!it.hasNext()) {
            throw new IOException("an empty InputStream");
        }

        final CSVRecord headerRecord = it.next();
        final List<InstituteIndexAndName> headerIdxNames = collectInstituteIndexesAndNames(this.bankNameSanitizer, headerRecord);
        return new LoanAmountHistoryIterator(it, headerIdxNames, integerSanitizer);
    }
}
