package tv.vradio.vradiotvserver.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String username) {
        super("Could not find account: " + username);
    }
}
