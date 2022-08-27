package tv.vradio.vradiotvserver.stations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@RedisHash("Station")
@Getter
public class Station {
    private UUID id;
    private String ownerUsername;

    private String name;
    private List<Media> mediaQueue = new CopyOnWriteArrayList<>();

    @Setter
    private StationStatus status;

    public Station(UUID id, String ownerUsername, String name) {
        this.id = id;
        this.ownerUsername = ownerUsername;
        this.name = name;
    }

    public Station() {}
}
