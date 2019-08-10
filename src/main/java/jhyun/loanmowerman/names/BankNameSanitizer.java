package jhyun.loanmowerman.names;

public class BankNameSanitizer {

    public String sanitize(final String input) {
        if (input == null) return null;
        return input.replaceFirst("[\\d\\(\\)]+.+[\\(\\)]+$", "");
    }

}
