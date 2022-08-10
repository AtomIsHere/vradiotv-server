package tv.vradio.vradiotvserver.account;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<Account> findByUsername(String username);
    Optional<Account> findByEmail(String email);

    Optional<Account> findById(Long id);
}
