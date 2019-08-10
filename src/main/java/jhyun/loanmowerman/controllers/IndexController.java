package jhyun.loanmowerman.controllers;

import io.swagger.annotations.*;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class IndexController {

    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    public IndexController(LoanAmountHistoryService loanAmountHistoryService) {
        this.loanAmountHistoryService = loanAmountHistoryService;
    }

    @ApiOperation(value = "CSV 파일을 파싱하여 DB에 저장한다")
    @ApiResponses({
            @ApiResponse(code = 201, message = "저장성공")
    })
    @RequestMapping(path = "/history", method = RequestMethod.PUT,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> putCsv(
            @ApiParam(value = "DB에 저장하려는 CSV 파일 내용")
            @RequestBody String requestBody
    ) throws URISyntaxException, IOException {
        loanAmountHistoryService.saveCsv(new StringBufferInputStream(requestBody));
        return ResponseEntity.created(new URI("/history")).body("Created");
    }

    @ApiOperation(value = "DB을 모두 비운다")
    @ApiResponses({
            @ApiResponse(code = 205, message = "비우기 성공")
    })
    @RequestMapping(path = "/history", method = RequestMethod.DELETE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> purgeDb() {
        loanAmountHistoryService.purgeAll();
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Purged");
    }

    @RequestMapping(path = "/institute-names", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<String> listAllInstituteNames() {
        return new ArrayList<>(loanAmountHistoryService.listAllInstituteNames());
    }
}
