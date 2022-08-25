package tv.vradio.vradiotvserver.account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tv.vradio.vradiotvserver.account.auth.AuthRepository;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.exceptions.AccountNotFoundException;
import tv.vradio.vradiotvserver.exceptions.InvalidPasswordException;

import java.util.UUID;

@RestController
public class AccountController {
    private final AccountRepository accountRepository;
    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountController(AccountRepository accountRepository, AuthRepository authRepository) {
        this.accountRepository = accountRepository;
        this.authRepository = authRepository;
    }

    @GetMapping("/account/create-account")
    public CreationResult createAccount(@RequestParam(name = "username") String username, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        if(accountRepository.existsByUsername(username)) {
            return CreationResult.USERNAME_EXISTS;
        } else if(accountRepository.existsByEmail(email)) {
            return CreationResult.EMAIL_EXISTS;
        }

        String hashedPassword = passwordEncoder.encode(password);
        accountRepository.save(new Account(username, email, hashedPassword, false));

        //TODO: Verify emails

        return CreationResult.SUCCESS;
    }

    @GetMapping("/account/login")
    public AuthToken login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        Account target = accountRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));

        if(passwordEncoder.matches(password, target.getHashedPassword())) {
            if(authRepository.existsByAccountName(target.getUsername())) {
                authRepository.deleteByAccountName(target.getUsername());
            }

            AuthToken token = new AuthToken(UUID.randomUUID(), target.getUsername());
            authRepository.save(token);
            return token;
        } else {
            throw new InvalidPasswordException();
        }
    }

    @GetMapping("/account/check-auth")
    public boolean checkAuth(@RequestParam(name = "username") String username, @RequestParam(name = "auth-key") String authKey) {
       return authRepository.confirmToken(UUID.fromString(authKey), username);
    }

    @GetMapping("/account/logout")
    public boolean logout(@RequestParam(name = "auth-key") String authKey) {
        authRepository.deleteByToken(UUID.fromString(authKey));

        return true;
    }

    public enum CreationResult {
        SUCCESS,
        USERNAME_EXISTS,
        EMAIL_EXISTS
    }

}
