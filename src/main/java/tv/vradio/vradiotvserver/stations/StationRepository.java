package tv.vradio.vradiotvserver.stations;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository extends CrudRepository<Station, UUID> {
    boolean existsById(UUID id);
    boolean existsByOwnerUsername(String ownerUsername);

    Optional<Station> findById(UUID id);
    Optional<Station> findByOwnerUsername(String ownerUsername);
}
