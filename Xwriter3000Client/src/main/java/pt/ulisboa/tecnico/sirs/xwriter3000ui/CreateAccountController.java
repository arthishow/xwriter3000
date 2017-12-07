package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CreateAccountController {

    /**
     * Verify if the given password is strong enough
     * by verifying a few constraints.
     *
     * @param password the given password
     * @return a boolean indicating if it is strong enough or not
     */
    protected static boolean verifyPassword(String password) {
        int length = password.length();
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password);
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d+.*");
        boolean hasSpace = password.contains(" ");
        return length > 5 && length < 101 && m.find() && hasUppercase && hasLowercase && hasNumber && !hasSpace;
    }

    /**
     * Verify if the size of the given username is
     * of respectable size.
     *
     * @param userId the given username
     * @return a boolean indicating if the size is correct or not
     */
    protected static boolean verifyUserId(String userId) {
        int length = userId.length();
        return length > 3 && length < 101;
    }

}
