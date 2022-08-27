package tv.vradio.vradiotvserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tv.vradio.vradiotvserver.account.AccountController;
import tv.vradio.vradiotvserver.stations.StationController;

@SpringBootTest
@ExtendWith(RedisExtension.class)
@DirtiesContext
class VRadioTVServerSanityCheck {

    @Autowired
    private PingController pingController;
    @Autowired
    private AccountController accountController;
    @Autowired
    private StationController stationController;

    @Test
    void contextLoads() {
        assertNotNull(pingController);
        assertNotNull(accountController);
        assertNotNull(stationController);
    }
}
