package tv.vradio.vradiotvserver.stations.join;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tv.vradio.vradiotvserver.stations.Station;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class JoinServiceImpl implements JoinService {
    private static final String PREFIX = "join-code:";

    private final RedisTemplate<String, String> redis;
    private final AtomicInteger count;

    public JoinServiceImpl(RedisTemplate<String, String> redis) {
        this.redis = redis;
        this.count = new AtomicInteger(1);
    }

    @Override
    public int generateJoinCode(Station station) {
        int current = count.getAndIncrement();
        redis.opsForValue().set(PREFIX + current, station.getId().toString());
        return current;
    }
}
