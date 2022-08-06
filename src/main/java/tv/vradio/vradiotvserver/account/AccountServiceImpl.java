package tv.vradio.vradiotvserver.account;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    private final Map<Account, String> activeTokens = new HashMap<>();

    @Override
    public String generateToken(Account account) {
        String token = UUID.randomUUID().toString();

        activeTokens.put(account, token);
        return token;
    }

    @Override
    public boolean confirmToken(Account account, String token) {
        return !activeTokens.containsKey(account) || activeTokens.get(account).equals(token);
    }

    @Override
    public void deauth(Account account) {
        activeTokens.remove(account);
    }
}
