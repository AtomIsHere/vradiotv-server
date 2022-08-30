package tv.vradio.vradiotvserver.stations.join;

import tv.vradio.vradiotvserver.stations.Station;

public interface JoinService {
    int generateJoinCode(Station station);
}
