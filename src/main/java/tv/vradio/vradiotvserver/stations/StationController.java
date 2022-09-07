package tv.vradio.vradiotvserver.stations;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tv.vradio.vradiotvserver.account.auth.AuthRepository;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;
import tv.vradio.vradiotvserver.exceptions.StationNotFoundException;
import tv.vradio.vradiotvserver.stations.join.JoinService;
import tv.vradio.vradiotvserver.stations.json.StationJsonService;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final AuthRepository authRepository;
    private final JoinService joinService;
    private final StationJsonService stationService;

    @GetMapping("/stations/get-all")
    public Collection<Station> getAll() {
        return stationService.getAll();
    }

    @GetMapping("/stations/get")
    public Station get(@RequestParam("id") String stationId) {
        UUID id;
        try {
            id = UUID.fromString(stationId);
        } catch(IllegalArgumentException ex) {
            throw new StationNotFoundException(stationId);
        }

        return stationService.getStation(id).orElseThrow(() -> new StationNotFoundException(stationId));
    }

    @GetMapping("/stations/get-by-owner")
    public Station getByOwner(@RequestParam("owner") String owner) {
         return stationService.findByOwnerUsername(owner).orElseThrow(() -> new StationNotFoundException(owner));
    }

    @GetMapping("/stations/create")
    public Station create(@RequestParam("auth-token") String authToken, @RequestParam("name") String name) {
        AuthToken token;

        UUID auth;
        try {
            auth = UUID.fromString(authToken);
        } catch(IllegalArgumentException ex) {
            throw new AuthenticationFailureException(authToken);
        }

        token = authRepository.findById(auth).orElse(null);

        if(token == null) {
            throw new AuthenticationFailureException(authToken);
        }

        String accountOwner = token.accountName();

        Station station = stationService.findByOwnerUsername(accountOwner).orElse(null);
        if(station != null) {
            return station;
        }

        station = new Station(UUID.randomUUID(), accountOwner, name);
        try {
            stationService.save(station);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return station;
    }

    @GetMapping("/stations/{id}/queue-media")
    public Media queueMedia(@PathVariable("id") String id,
                            @RequestParam("auth-token") String token,
                            @RequestParam("url") String url,
                            @RequestParam("name") String name,
                            @RequestParam("service") Media.StreamingService service,
                            @RequestParam(value = "duration", required = false, defaultValue = "0") long duration) {
        UUID stationId;
        try {
            stationId = UUID.fromString(id);
        } catch(IllegalArgumentException ex) {
            throw new StationNotFoundException(id);
        }

        Station target = stationService.getStation(stationId).orElseThrow(() -> new StationNotFoundException(id));

        UUID authToken;
        try {
            authToken = UUID.fromString(token);
        } catch(IllegalArgumentException ex) {
            throw new AuthenticationFailureException(token);
        }

        if(!authRepository.confirmToken(authToken, target.getOwnerUsername())) {
            throw new AuthenticationFailureException(token);
        }

        // TODO: url sanity check

        Media media = new Media(name, url, duration, service);
        target.getMediaQueue().add(media);
        stationService.delete(stationId);
        try {
            stationService.save(target);
        } catch(JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return media;
    }

    @GetMapping("/stations/{id}/join")
    public int join(@PathVariable("id") String id) {
        UUID stationId;
        try {
            stationId = UUID.fromString(id);
        } catch (IllegalArgumentException iae) {
            throw new StationNotFoundException(id);
        }

        Station target = stationService.getStation(stationId).orElseThrow(() -> new StationNotFoundException(id));
        return joinService.generateJoinCode(target);
    }
}
