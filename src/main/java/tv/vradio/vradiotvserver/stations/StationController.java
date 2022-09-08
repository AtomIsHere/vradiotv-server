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

    /**
     * Get every single active station
     *
     * @return a list of stations
     */
    @GetMapping("/stations/get-all")
    public Collection<Station> getAll() {
        return stationService.getAll();
    }

    /**
     * Get a station based upon its id
     *
     * @param stationId v4 UUID
     * @return Station
     */
    @GetMapping("/stations/get")
    public Station get(@RequestParam("id") String stationId) {
        UUID id;
        try {
            // Parse string as UUID
            id = UUID.fromString(stationId);
        } catch(IllegalArgumentException ex) {
            // Change the IllegalArgumentException into a StationNotFoundException in order for it to be handled
            throw new StationNotFoundException(stationId);
        }

        // Find the station and return it, if not found throw an exception
        return stationService.getStation(id).orElseThrow(() -> new StationNotFoundException(stationId));
    }

    /**
     * Get a station by its owner
     *
     * @param owner the username of the owner
     * @return Station
     */
    @GetMapping("/stations/get-by-owner")
    public Station getByOwner(@RequestParam("owner") String owner) {
        // Search station for one owned by the supplied username, if one isn't found throw an exception
         return stationService.findByOwnerUsername(owner).orElseThrow(() -> new StationNotFoundException(owner));
    }

    /**
     * Create a station
     *
     * @param authToken The token of the station's owner
     * @param name The name of the station
     * @return Created station
     */
    @GetMapping("/stations/create")
    public Station create(@RequestParam("auth-token") String authToken, @RequestParam("name") String name) {
        AuthToken token;

        UUID auth;
        try {
            // Parse auth token as a UUID
            auth = UUID.fromString(authToken);
        } catch(IllegalArgumentException ex) {
            // Convert IllegalArgumentException into an exception which can be handled
            throw new AuthenticationFailureException(authToken);
        }

        // Find the AuthToken based upon the UUID
        token = authRepository.findById(auth).orElse(null);

        // If the token is not found throw an exception
        if(token == null) {
            throw new AuthenticationFailureException(authToken);
        }

        String accountOwner = token.accountName();

        // Create station based upon the owner in the token
        Station station = stationService.findByOwnerUsername(accountOwner).orElse(null);
        if(station != null) {
            return station;
        }

        // Create a new instance of the station
        station = new Station(UUID.randomUUID(), accountOwner, name);
        try {
            // Add the station to redis
            stationService.save(station);
        } catch (JsonProcessingException ex) {
            // Throw exception if it is unable to save
            throw new RuntimeException(ex.getMessage());
        }

        // Return the created station
        return station;
    }

    /**
     * Queue some media to a given station
     *
     * @param id The id of a station
     * @param token Auth token of the station's owner
     * @param url url to media
     * @param name name of the media
     * @param service Streaming service in which the media is on
     * @param duration The length of the media in seconds
     * @return The queued media
     */
    @GetMapping("/stations/{id}/queue-media")
    public Media queueMedia(@PathVariable("id") String id,
                            @RequestParam("auth-token") String token,
                            @RequestParam("url") String url,
                            @RequestParam("name") String name,
                            @RequestParam("service") Media.StreamingService service,
                            @RequestParam(value = "duration", required = false, defaultValue = "0") long duration) {
        UUID stationId;
        try {
            // Parse station id as UUID
            stationId = UUID.fromString(id);
        } catch(IllegalArgumentException ex) {
            // Convert exception into an exception which can be handled
            throw new StationNotFoundException(id);
        }

        // Find station based on the id, if none is found throw an exception.
        Station target = stationService.getStation(stationId).orElseThrow(() -> new StationNotFoundException(id));

        UUID authToken;
        try {
            // Parse the auth token as an UUID
            authToken = UUID.fromString(token);
        } catch(IllegalArgumentException ex) {
            // Convert exception into an exception which can be handled
            throw new AuthenticationFailureException(token);
        }

        // Check if the token is valid
        if(!authRepository.confirmToken(authToken, target.getOwnerUsername())) {
            throw new AuthenticationFailureException(token);
        }

        // TODO: url sanity check

        // Create a new instance of the media
        Media media = new Media(name, url, duration, service);
        // Add media to the station queue
        target.getMediaQueue().add(media);
        // Delete old station from redis
        stationService.delete(stationId);
        try {
            // Add new station to redis
            stationService.save(target);
        } catch(JsonProcessingException ex) {
            // Throw exception if error is encountered
            throw new RuntimeException(ex);
        }

        // Return created media
        return media;
    }

    /**
     * Generate a join code for the station
     *
     * @param id The id of a station
     * @return Generated join code
     */
    @GetMapping("/stations/{id}/join")
    public int join(@PathVariable("id") String id) {
        UUID stationId;
        try {
            // Parse station id as UUID
            stationId = UUID.fromString(id);
        } catch (IllegalArgumentException iae) {
            // Convert exception into an exception which can be handled
            throw new StationNotFoundException(id);
        }

        // Find station based upon the id, if none is found throw an exception
        Station target = stationService.getStation(stationId).orElseThrow(() -> new StationNotFoundException(id));
        // Generate the join code
        return joinService.generateJoinCode(target);
    }
}
