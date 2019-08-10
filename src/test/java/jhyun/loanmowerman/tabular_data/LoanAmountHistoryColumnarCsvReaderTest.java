package jhyun.loanmowerman.tabular_data;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import jhyun.loanmowerman.value_sanitizers.BankNameSanitizer;
import jhyun.loanmowerman.testing_supp.Examples;
import jhyun.loanmowerman.value_sanitizers.IntegerSanitizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LoanAmountHistoryColumnarCsvReaderTest {

    @Test
    public void testCollectBankColumnIndexesAndNames() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                Examples.urlAsInputStream(Examples.exampleCsv()), Charsets.UTF_8));
        final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);

        final Iterator<CSVRecord> it = parser.iterator();
        if (!it.hasNext()) {
            throw new Exception("shit happened here");
        }


        final CSVRecord headerRecord = it.next();
        log.trace("header-record = {}", headerRecord);

        final BankNameSanitizer bankNameSanitizer = new BankNameSanitizer();
        final List<InstituteIndexAndName> pairs = LoanAmountHistoryColumnarCsvReader.collectInstituteIndexesAndNames(bankNameSanitizer, headerRecord);
        log.trace("pairs = {}", pairs);

        final List<InstituteIndexAndName> expect = Lists.newArrayList(
                new InstituteIndexAndName(2, "주택도시기금"),
                new InstituteIndexAndName(3, "국민은행"),
                new InstituteIndexAndName(4, "우리은행"),
                new InstituteIndexAndName(5, "신한은행"),
                new InstituteIndexAndName(6, "한국시티은행"),
                new InstituteIndexAndName(7, "하나은행"),
                new InstituteIndexAndName(8, "농협은행/수협은행"),
                new InstituteIndexAndName(9, "외환은행"),
                new InstituteIndexAndName(10, "기타은행")
        );

        assertThat(pairs).isEqualTo(expect);
    }

    @Test
    public void testIterator() throws IOException {
        final BankNameSanitizer bankNameSanitizer = new BankNameSanitizer();
        final IntegerSanitizer integerSanitizer = new IntegerSanitizer();
        final LoanAmountHistoryColumnarCsvReader csvReader =
                new LoanAmountHistoryColumnarCsvReader(bankNameSanitizer, integerSanitizer);
        final Iterator<LoanAmountHistory> it = csvReader.iterator(Examples.urlAsInputStream(Examples.exampleCsv()));
        while (it.hasNext()) {
            final LoanAmountHistory hist = it.next();
            log.trace("hist-entry = {}", hist);
        }
    }

    @Test
    public void testIterator_3lines() throws IOException {
        final BankNameSanitizer bankNameSanitizer = new BankNameSanitizer();
        final IntegerSanitizer integerSanitizer = new IntegerSanitizer();
        final LoanAmountHistoryColumnarCsvReader csvReader =
                new LoanAmountHistoryColumnarCsvReader(bankNameSanitizer, integerSanitizer);
        final Iterator<LoanAmountHistory> it = csvReader.iterator(
                Examples.urlAsInputStream(Examples.exampleCsv3Lines()));
        final List<LoanAmountHistory> result = new ArrayList<>();
        while (it.hasNext()) {
            final LoanAmountHistory hist = it.next();
            result.add(hist);
        }
        log.trace("{}", result);
        assertThat(result).isEqualTo(Lists.newArrayList(
                new LoanAmountHistory(2005, 1, ImmutableMap.<InstituteIndexAndName, Integer>builder()
                        .put(new InstituteIndexAndName(2, "주택도시기금"), 1019)
                        .put(new InstituteIndexAndName(3, "국민은행"), 846)
                        .put(new InstituteIndexAndName(4, "우리은행"), 82)
                        .put(new InstituteIndexAndName(5, "신한은행"), 95)
                        .put(new InstituteIndexAndName(6, "한국시티은행"), 30)
                        .put(new InstituteIndexAndName(7, "하나은행"), 157)
                        .put(new InstituteIndexAndName(8, "농협은행/수협은행"), 57)
                        .put(new InstituteIndexAndName(9, "외환은행"), 80)
                        .put(new InstituteIndexAndName(10, "기타은행"), 99)
                        .build()),
                new LoanAmountHistory(2005, 2, ImmutableMap.<InstituteIndexAndName, Integer>builder()
                        .put(new InstituteIndexAndName(2, "주택도시기금"), 1144)
                        .put(new InstituteIndexAndName(3, "국민은행"), 864)
                        .put(new InstituteIndexAndName(4, "우리은행"), 91)
                        .put(new InstituteIndexAndName(5, "신한은행"), 97)
                        .put(new InstituteIndexAndName(6, "한국시티은행"), 35)
                        .put(new InstituteIndexAndName(7, "하나은행"), 168)
                        .put(new InstituteIndexAndName(8, "농협은행/수협은행"), 36)
                        .put(new InstituteIndexAndName(9, "외환은행"), 111)
                        .put(new InstituteIndexAndName(10, "기타은행"), 2114)
                        .build()),
                new LoanAmountHistory(2005, 3, ImmutableMap.<InstituteIndexAndName, Integer>builder()
                        .put(new InstituteIndexAndName(2, "주택도시기금"), 1144)
                        .put(new InstituteIndexAndName(3, "국민은행"), 864)
                        .put(new InstituteIndexAndName(4, "우리은행"), 91)
                        .put(new InstituteIndexAndName(5, "신한은행"), 97)
                        .put(new InstituteIndexAndName(6, "한국시티은행"), 35)
                        .put(new InstituteIndexAndName(7, "하나은행"), 168)
                        // NOTE: .put(new InstituteIndexAndName(8, "농협은행/수협은행"), 0)
                        .put(new InstituteIndexAndName(9, "외환은행"), 111)
                        .put(new InstituteIndexAndName(10, "기타은행"), 2114)
                        .build())
        ));
    }
}