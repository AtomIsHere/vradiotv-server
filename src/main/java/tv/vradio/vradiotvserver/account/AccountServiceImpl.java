package tv.vradio.vradiotvserver.account;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountServiceImpl implements AccountService {
    private final Map<UUID, String> activeTokens = new ConcurrentHashMap<>();

    @Override
    public String generateToken(Account account) {
        String token = UUID.randomUUID().toString();

        activeTokens.put(account.getId(), token);
        return token;
    }

    @Override
    public boolean confirmToken(Account account, String token) {
        return activeTokens.containsKey(account.getId()) && activeTokens.get(account.getId()).equals(token);
    }

    @Override
    public void deauth(Account account) {
        activeTokens.remove(account.getId());
    }
}
