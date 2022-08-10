package tv.vradio.vradiotvserver.exceptions;

import tv.vradio.vradiotvserver.account.Account;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(Account owner) {
        super("Could not find station with owner: " + owner.getUsername());
    }
}
