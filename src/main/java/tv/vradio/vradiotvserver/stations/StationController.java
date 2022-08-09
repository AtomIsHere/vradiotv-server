package tv.vradio.vradiotvserver.stations;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tv.vradio.vradiotvserver.account.Account;
import tv.vradio.vradiotvserver.account.AccountRepository;
import tv.vradio.vradiotvserver.account.AccountService;
import tv.vradio.vradiotvserver.exceptions.AccountNotFoundException;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;
import tv.vradio.vradiotvserver.exceptions.StationNotFoundException;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StationController {
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final StationService stationService;

    @GetMapping("/stations/get-all")
    public Collection<Station> getAll() {
        return stationService.getAll();
    }

    @GetMapping("/stations/get")
    public Station get(@RequestParam("owner-id") String ownerId) {
         Account account = accountRepository.findById(UUID.fromString(ownerId)).orElseThrow(() -> new AccountNotFoundException(ownerId));

         if(stationService.hasStation(account)) {
             return stationService.findStation(account);
         } else {
            throw new StationNotFoundException(account);
         }
    }

    @GetMapping("/stations/create")
    public Station create(@RequestParam("auth-token") String authToken, @RequestParam("owner") String accountOwner, @RequestParam("name") String name) {
        Account account = accountRepository.findById(UUID.fromString(accountOwner)).orElseThrow(() -> new AccountNotFoundException(accountOwner));

        if(!accountService.confirmToken(account, authToken)) {
            throw new AuthenticationFailureException(authToken);
        }
        if(stationService.hasStation(account)) {
            return stationService.findStation(account);
        }

        Station station = new Station(account.getId(), name);
        stationService.registerStation(station);
        return station;
    }


}
