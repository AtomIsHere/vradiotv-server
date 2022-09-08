package tv.vradio.vradiotvserver.account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tv.vradio.vradiotvserver.account.auth.AuthRepository;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.exceptions.AccountNotFoundException;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;
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

    /**
     * Create an account
     *
     * @param username Account's username
     * @param email Account's email
     * @param password Account's password
     * @return Whether the creation was successful
     */
    @GetMapping("/account/create-account")
    public CreationResult createAccount(@RequestParam(name = "username") String username, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        if(accountRepository.existsByUsername(username)) {
            // If the account's username already exists do not proceed
            return CreationResult.USERNAME_EXISTS;
        } else if(accountRepository.existsByEmail(email)) {
            // If the account's email already exists do not proceed
            return CreationResult.EMAIL_EXISTS;
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(password);
        // Add account to SQL
        accountRepository.save(new Account(username, email, hashedPassword, false));

        //TODO: Verify emails

        // Return a successful results
        return CreationResult.SUCCESS;
    }

    /**
     * Generate an auth token for an account
     *
     * @param username Account username
     * @param password Account password
     * @return Generated auth token
     */
    @GetMapping("/account/login")
    public AuthToken login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        // Find the account based upon a username, if none is found throw an excetpion
        Account target = accountRepository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));

        // Check if hashed password matches the provided password
        if(passwordEncoder.matches(password, target.getHashedPassword())) {
            // Check if an auth token already exists, if so delete it
            authRepository.findName(username).ifPresent(at -> authRepository.deleteById(at.id()));

            // Generate an auth token
            AuthToken token = new AuthToken(UUID.randomUUID(), target.getUsername());
            // Add it to redis
            authRepository.save(token);
            // Return the token
            return token;
        } else {
            // Throw exception if password is not valid
            throw new InvalidPasswordException();
        }
    }

    /**
     * Check if an auth token is valid
     *
     * @param username Username of account to check
     * @param authKey Auth key to check
     * @return Whether the token is valid
     */
    @GetMapping("/account/check-auth")
    public boolean checkAuth(@RequestParam(name = "username") String username, @RequestParam(name = "auth-key") String authKey) {
       UUID auth;
       try {
           // Parse token as a uuid
           auth = UUID.fromString(authKey);
       } catch(IllegalArgumentException ex) {
           // Convert exception into an exception which can be handled
           throw new AuthenticationFailureException(authKey);
       }

       // Check if token is valid
       return authRepository.confirmToken(auth, username);
    }

    /**
     * Delete an auth token
     *
     * @param authKey Auth token to delete
     * @return result
     */
    @GetMapping("/account/logout")
    public boolean logout(@RequestParam(name = "auth-key") String authKey) {
        UUID auth;
        try {
            // Parse token as a uuid
            auth = UUID.fromString(authKey);
        } catch(IllegalArgumentException ex) {
            // Convert exception into an exception which can be handled
            throw new AuthenticationFailureException(authKey);
        }

        // Delete the token from redis
        authRepository.deleteById(auth);

        return true;
    }

    public enum CreationResult {
        SUCCESS,
        USERNAME_EXISTS,
        EMAIL_EXISTS
    }

}
