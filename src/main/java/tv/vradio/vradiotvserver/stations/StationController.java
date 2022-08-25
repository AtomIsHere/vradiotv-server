package tv.vradio.vradiotvserver.stations;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tv.vradio.vradiotvserver.account.auth.AuthRepository;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;
import tv.vradio.vradiotvserver.exceptions.StationNotFoundException;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final AuthRepository authRepository;
    private final StationRepository stationRepository;

    @GetMapping("/stations/get-all")
    public Collection<Station> getAll() {
        return StreamSupport.stream(stationRepository.findAll().spliterator(), false)
                .collect(Collectors.toUnmodifiableSet());
    }

    @GetMapping("/stations/get")
    public Station get(@RequestParam("owner") String owner) {
         return stationRepository.findByOwnerUsername(owner).orElseThrow(() -> new StationNotFoundException(owner));
    }

    @GetMapping("/stations/create")
    public Station create(@RequestParam("auth-token") String authToken, @RequestParam("name") String name) {
        AuthToken token = authRepository.findByToken(UUID.fromString(authToken)).orElse(null);
        if(token == null) {
            throw new AuthenticationFailureException(authToken);
        }

        String accountOwner = token.accountName();

        Station station = stationRepository.findByOwnerUsername(accountOwner).orElse(null);
        if(station != null) {
            return station;
        }

        station = new Station(UUID.randomUUID(), accountOwner, name);
        stationRepository.save(station);
        return station;
    }


}
