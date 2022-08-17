package tv.vradio.vradiotvserver;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class VRadioTVServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VRadioTVServerApplication.class, args);
    }
}
