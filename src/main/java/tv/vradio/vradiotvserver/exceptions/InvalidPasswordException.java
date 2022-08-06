package tv.vradio.vradiotvserver.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Password is invalid");
    }
}
