package tv.vradio.vradiotvserver.account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tv.vradio.vradiotvserver.exceptions.AccountNotFoundException;
import tv.vradio.vradiotvserver.exceptions.InvalidPasswordException;

@RestController
public class AccountController {
    private final AccountRepository repository;
    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountController(AccountRepository repository, AccountService service) {
        this.repository = repository;
        this.accountService = service;
    }

    @GetMapping("/account/create-account")
    public CreationResult createAccount(@RequestParam(name = "username") String username, @RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        if(repository.existsByUsername(username)) {
            return CreationResult.USERNAME_EXISTS;
        } else if(repository.existsByEmail(email)) {
            return CreationResult.EMAIL_EXISTS;
        }

        String hashedPassword = passwordEncoder.encode(password);
        repository.save(new Account(username, email, hashedPassword, false));

        //TODO: Verify emails

        return CreationResult.SUCCESS;
    }

    @GetMapping("/account/login")
    public String login(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        Account target = repository.findByUsername(username).orElseThrow(() -> new AccountNotFoundException(username));

        if(passwordEncoder.matches(password, target.getHashedPassword())) {
            return accountService.generateToken(target);
        } else {
            throw new InvalidPasswordException();
        }
    }

    public enum CreationResult {
        SUCCESS,
        USERNAME_EXISTS,
        EMAIL_EXISTS
    }

}
