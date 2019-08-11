package jhyun.loanmowerman.controllers.aggregations;

public class NoDataException extends Exception {
    public NoDataException(String reason) {
        super(reason);
    }
}
