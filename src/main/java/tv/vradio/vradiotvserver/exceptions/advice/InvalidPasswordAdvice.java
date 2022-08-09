package tv.vradio.vradiotvserver.exceptions.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tv.vradio.vradiotvserver.exceptions.AccountNotFoundException;
import tv.vradio.vradiotvserver.exceptions.InvalidPasswordException;

@ControllerAdvice
public class InvalidPasswordAdvice {
    @ResponseBody
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String invalidPasswordHandler(InvalidPasswordException ex) {
        return ex.getMessage();
    }
}
