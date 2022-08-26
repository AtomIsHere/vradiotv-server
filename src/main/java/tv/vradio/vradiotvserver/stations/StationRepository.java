package tv.vradio.vradiotvserver.stations;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Repository
public interface StationRepository extends CrudRepository<Station, UUID> {
    boolean existsById(UUID id);

    Optional<Station> findById(UUID id);

    default Optional<Station> findOwnerName(String ownerName) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(s -> s.getOwnerUsername().equals(ownerName))
                .findAny();
    }
}
