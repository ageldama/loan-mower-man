package jhyun.loanmowerman.value_sanitizers;

public class IntegerSanitizer {
    public Integer sanitize(final String input) {
        if (input == null) return null;
        final String onlyDigits = input.replaceAll("[^\\d]", "");
        try {
            return Integer.valueOf(onlyDigits);
        } catch (NumberFormatException exc) {
            return null;
        }
    }
}
