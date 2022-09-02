package tv.vradio.vradiotvserver.stations.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import tv.vradio.vradiotvserver.stations.Station;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface StationJsonService {
    Optional<Station> getStation(UUID id);
    boolean exists(UUID id);
    void delete(UUID id);

    Collection<Station> getAll();

    void save(Station station) throws JsonProcessingException;

    default Optional<Station> findByOwnerUsername(String owner) {
        return getAll().stream().filter(s -> s.getOwnerUsername().equals(owner)).findAny();
    }
}
