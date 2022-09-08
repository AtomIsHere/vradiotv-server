package tv.vradio.vradiotvserver.stations.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tv.vradio.vradiotvserver.stations.Station;

import java.util.*;

@Service
public class StationJsonServiceImpl implements StationJsonService {
    private static final String REDIS_PREFIX = "Station_";

    private final RedisTemplate<String, String> redis;
    private final ObjectMapper json;

    public StationJsonServiceImpl(RedisTemplate<String, String> redis, ObjectMapper json) {
        this.redis = redis;
        this.json = json;
    }

    @Override
    public Optional<Station> getStation(UUID id) {
        // Get JSON from redis
        String fromRedis = redis.opsForValue().get(REDIS_PREFIX + id.toString());
        if(fromRedis == null) {
            // If key is not found in redis return nothing
            return Optional.empty();
        }

        try {
            // Convert JSON into Station then wrap it in an Optional
            return Optional.of(json.readValue(fromRedis, Station.class));
        } catch (JsonProcessingException e) {
            // If unable to convert json throw an exception
            throw new RuntimeException(e);
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
        // Create pattern to get all keys
        Set<String> keys = redis.keys(REDIS_PREFIX + "*");
        if(keys == null) {
            // Return empty set if it fails
            return Collections.emptySet();
        }

        // Stream the key list to perform filtration operations
        return keys.stream()
                // Convert the key set into a station collection
                .map(k -> getStation(UUID.fromString(k.replaceAll(REDIS_PREFIX, ""))))
                // Filter out all values which aren't found
                .filter(Optional::isPresent)
                // Unwrap optionals
                .map(Optional::get)
                // Convert to a list
                .toList();
    }

    @Override
    public void save(Station station) throws JsonProcessingException {
        // Convert Station into JSON
        String stationJson = json.writeValueAsString(station);

        // Add JSON to redis
        redis.opsForValue().set(REDIS_PREFIX + station.getId().toString(), stationJson);
    }
}
