package tv.vradio.vradiotvserver.exceptions.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tv.vradio.vradiotvserver.exceptions.StationNotFoundException;

@ControllerAdvice
public class StationNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(StationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String stationNotFoundHandler(StationNotFoundException ex) {
        return ex.getMessage();
    }
}
