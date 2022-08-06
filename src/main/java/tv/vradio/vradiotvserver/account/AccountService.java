package tv.vradio.vradiotvserver.account;

public interface AccountService {
    String generateToken(Account account);
    boolean confirmToken(Account account, String token);
    void deauth(Account account);
}
