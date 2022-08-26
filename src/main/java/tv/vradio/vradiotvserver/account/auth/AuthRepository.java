package tv.vradio.vradiotvserver.account.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Repository
public interface AuthRepository extends CrudRepository<AuthToken, UUID> {
    @Override
    boolean existsById(UUID id);

    @Override
    Optional<AuthToken> findById(UUID id);

    void deleteById(UUID id);

    default boolean confirmToken(UUID token, String accountName) {
        AtomicBoolean auth = new AtomicBoolean(false);

        findById(token).ifPresent(at -> auth.set(at.accountName().equals(accountName)));
        return auth.get();
    }

    default Optional<AuthToken> findName(String accountName) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(at -> at.accountName().equals(accountName))
                .findAny();
    }
}
