package tv.vradio.vradiotvserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    /**
     * Basic ping endpoint to check if the REST API is online
     *
     * @return pong
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
