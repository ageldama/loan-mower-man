package jhyun.loanmowerman.controllers.api_user;

public class ApiUserDuplicatedException extends Throwable {
    public ApiUserDuplicatedException(String id) {
        super(id);
    }
}
