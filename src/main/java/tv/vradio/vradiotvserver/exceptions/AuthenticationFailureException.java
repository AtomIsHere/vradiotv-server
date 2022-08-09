package tv.vradio.vradiotvserver.exceptions;

public class AuthenticationFailureException extends RuntimeException {
    public AuthenticationFailureException(String token) {
        super("Invalid token: " + token);
    }
}
