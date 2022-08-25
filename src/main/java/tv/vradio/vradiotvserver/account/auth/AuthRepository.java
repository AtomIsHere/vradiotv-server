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

    default boolean confirmToken(UUID token, String accountName) {
        AtomicBoolean auth = new AtomicBoolean(false);

        findByToken(token).ifPresent(at -> auth.set(at.accountName().equals(accountName)));
        return auth.get();
    }
}
