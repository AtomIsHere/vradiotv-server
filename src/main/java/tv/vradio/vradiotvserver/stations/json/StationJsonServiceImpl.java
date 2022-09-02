package tv.vradio.vradiotvserver.stations.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tv.vradio.vradiotvserver.stations.Station;

import java.util.*;

@Service
public class StationJsonServiceImpl implements StationJsonService {
    private static final String REDIS_PREFIX = "Station:";

    private final RedisTemplate<String, String> redis;
    private final ObjectMapper json;

    public StationJsonServiceImpl(RedisTemplate<String, String> redis, ObjectMapper json) {
        this.redis = redis;
        this.json = json;
    }

    @Override
    public Optional<Station> getStation(UUID id) {
        String fromRedis = redis.opsForValue().get(REDIS_PREFIX + id.toString());
        if(fromRedis == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(json.convertValue(fromRedis, Station.class));
        } catch(IllegalArgumentException iae) {
            return Optional.empty();
        }
    }

    @Override
    public boolean exists(UUID id) {
        return Boolean.TRUE.equals(redis.hasKey(REDIS_PREFIX + id.toString()));
    }

    @Override
    public void delete(UUID id) {
        redis.delete(REDIS_PREFIX + id.toString());
    }

    @Override
    public Collection<Station> getAll() {
        Set<String> keys = redis.keys(REDIS_PREFIX + "*");
        if(keys == null) {
            return Collections.emptySet();
        }

        return keys.stream()
                .map(k -> getStation(UUID.fromString(k.replaceAll(REDIS_PREFIX, ""))))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public void save(Station station) throws JsonProcessingException {
        String stationJson = json.writeValueAsString(station);

        redis.opsForValue().set(REDIS_PREFIX + station.getId().toString(), stationJson);
    }
}
