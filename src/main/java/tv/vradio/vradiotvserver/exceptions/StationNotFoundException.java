package tv.vradio.vradiotvserver.exceptions;

import tv.vradio.vradiotvserver.account.Account;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException(Account owner) {
        this(owner.getUsername());
    }

    public StationNotFoundException(String username) {
        super("Could not find station: " + username);
    }
}
