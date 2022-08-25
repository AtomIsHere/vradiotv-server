package tv.vradio.vradiotvserver.account.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public interface AuthRepository extends CrudRepository<AuthToken, UUID> {
    boolean existsByToken(UUID token);
    boolean existsByAccountName(String accountName);

    Optional<AuthToken> findByToken(UUID token);
    Optional<AuthToken> findByAccountName(String accountName);

    void deleteByToken(UUID token);
    void deleteByAccountName(String accountName);

    default boolean confirmToken(String token, String accountName) {
        AtomicBoolean auth = new AtomicBoolean(false);

        findByAccountName(accountName).ifPresent(at -> auth.set(at.token().toString().equals(token)));
        return auth.get();
    }
}
