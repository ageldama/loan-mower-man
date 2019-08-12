package jhyun.loanmowerman.services;

import com.google.common.collect.Lists;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.predictions.LoanAmountPrediction;
import jhyun.loanmowerman.services.predictions.NoSuchPredictorStrategyException;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.testing_supp.Examples;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoanAmountPredictionServiceCheckerTest {

    private static final String[] STRATEGIES = {
            "linear_regression", "average", "average_all"
    };

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private LoanAmountPredictionService loanAmountPredictionService;

    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private LoanAmountRepository loanAmountRepository;

    private boolean inited = false;
    private boolean cleaned = false;

    @Before
    public void prepare() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        if (!inited) {
            final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
            loanAmountHistoryService.saveCsv(inputStream);
            final Future<?> fut = loanAmountPredictionService.prepareForStrategies();
            fut.get(5L, TimeUnit.SECONDS);
            inited = true;
        }
    }

    @After
    public void teardown() {
        if (!cleaned) {
            loanAmountHistoryService.purgeAll();
            cleaned = true;
        }
    }

    // TODO: test-name?
    @Test
    public void predictAndDiff()
            throws NoDataException, PredictionNotPreparedException, NoSuchPredictorStrategyException {
        //
        final ArrayList<Institute> banks = Lists.newArrayList(instituteRepository.findAll());
        final Collection<Integer> years = loanAmountRepository.listAllYears();
        // NOTE: crazy nested loops here
        for (String strategy : STRATEGIES) {
            Long count = 0L;
            BigDecimal diffs = BigDecimal.ZERO;
            //
            for (Integer year : years) {
                final Collection<Integer> months = loanAmountRepository.listAllMonthsOfYear(year);
                for (Integer month : months) {
                    for (Institute bank : banks) {
                        final Optional<LoanAmount> loanAmount = loanAmountRepository.findExactOne(bank.getCode(), year, month);
                        if (loanAmount.isPresent() && loanAmount.get().getAmount() != null) {
                            final LoanAmountPrediction prediction =
                                    loanAmountPredictionService.predict(strategy, year, month, bank.getCode());
                            final Long actualAmt = loanAmount.get().getAmount().longValue();
                            final Long predAmt = prediction.getAmount();
                            log.debug("DIFF: {}/{}/{}/{}, ACTUAL({}) - PREDICTION({}) = {}",
                                    strategy, bank.getCode(), year, month,
                                    actualAmt, predAmt, actualAmt - predAmt);
                            diffs = diffs.add(BigDecimal.valueOf(Math.abs(actualAmt - predAmt)));
                            count ++;
                        } else {
                            log.debug("SKIP: {}/{}/{}/{}", strategy, bank.getCode(), year, month);
                        }
                    }
                }
            }
            log.info("DIFFs: {}'s DIFFS({}) / COUNT({}) = AVG({})",
                    strategy, diffs, count,
                    diffs.divideToIntegralValue(BigDecimal.valueOf(count)));
        }
    }
}
