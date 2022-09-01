package tv.vradio.vradiotvserver.stations;

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

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final AuthRepository authRepository;
    private final StationRepository stationRepository;
    private final JoinService joinService;

    @GetMapping("/stations/get-all")
    public Collection<Station> getAll() {
        return StreamSupport.stream(stationRepository.findAll().spliterator(), false)
                .collect(Collectors.toUnmodifiableSet());
    }

    @GetMapping("/stations/get")
    public Station get(@RequestParam("id") String stationId) {
        UUID id;
        try {
            id = UUID.fromString(stationId);
        } catch(IllegalArgumentException ex) {
            throw new StationNotFoundException(stationId);
        }

        return stationRepository.findById(id).orElseThrow(() -> new StationNotFoundException(stationId));
    }

    @GetMapping("/stations/get-by-owner")
    public Station getByOwner(@RequestParam("owner") String owner) {
         return stationRepository.findOwnerName(owner).orElseThrow(() -> new StationNotFoundException(owner));
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

        Station station = stationRepository.findOwnerName(accountOwner).orElse(null);
        if(station != null) {
            return station;
        }

        station = new Station(UUID.randomUUID(), accountOwner, name);
        station.getMediaQueue().add(new Media("_", "_", 0L, Media.StreamingService.SPOTIFY));
        stationRepository.save(station);
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

        Station target = stationRepository.findById(stationId).orElseThrow(() -> new StationNotFoundException(id));

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
        stationRepository.deleteById(stationId);
        stationRepository.save(target);
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

        Station target = stationRepository.findById(stationId).orElseThrow(() -> new StationNotFoundException(id));
        return joinService.generateJoinCode(target);
    }
}
