package pt.ulisboa.tecnico.sirs.xwriter3000ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountController {

    //TODO check the field size
    protected static boolean verifyPassword(String password) {
        int length = password.length();
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password);
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d+.*");
        boolean hasSpace = password.contains(" ");
        return length > 5 && length < 129 && m.find() && hasUppercase && hasLowercase && hasNumber && !hasSpace;
    }

    //TODO check the field size
    protected static boolean verifyUserId(String userId) {
        int length = userId.length();
        return length > 3 && length < 129;
    }

}
