package tv.vradio.vradiotvserver.account.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends CrudRepository<AuthToken, UUID> {
    boolean existsByToken(UUID token);
    boolean existsByAccountName(String accountName);

    Optional<AuthToken> findByToken(UUID token);
    Optional<AuthToken> findByAccountName(String accountName);

    void deleteByToken(UUID token);
    void deleteByAccountName(String accountName);
}
