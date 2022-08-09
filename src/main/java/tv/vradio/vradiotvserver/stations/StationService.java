package tv.vradio.vradiotvserver.stations;

import tv.vradio.vradiotvserver.account.Account;

import java.util.Collection;
import java.util.UUID;

public interface StationService {
    Station findStation(Account account);
    Collection<Station> getAll();

    boolean hasStation(Account account);

    void registerStation(Station station);
    void removeStation(Account account);
}
