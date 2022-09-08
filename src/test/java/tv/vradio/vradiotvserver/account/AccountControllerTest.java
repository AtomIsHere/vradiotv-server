package tv.vradio.vradiotvserver.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tv.vradio.vradiotvserver.RedisExtension;
import tv.vradio.vradiotvserver.account.auth.AuthRepository;
import tv.vradio.vradiotvserver.account.auth.AuthToken;
import tv.vradio.vradiotvserver.exceptions.AuthenticationFailureException;

import java.util.UUID;

@SpringBootTest
@ExtendWith(RedisExtension.class)
@DirtiesContext
@Order(2)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest {
    @Autowired
    private AccountController controller;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthRepository authRepository;

    @Test
    @Order(1)
    public void createAccountTest() {
        // Create account and check if it's successful
        assertEquals(controller.createAccount(TestAccountData.USERNAME, TestAccountData.EMAIL, TestAccountData.PASSWORD), AccountController.CreationResult.SUCCESS);

        // Check if it is present in the repository
        assertTrue(accountRepository.existsByUsername(TestAccountData.USERNAME));
        assertTrue(accountRepository.existsByEmail(TestAccountData.EMAIL));
    }

    @Test
    @Order(2)
    public void checkAuthTest() {
        // Create token
        AuthToken token = controller.login(TestAccountData.USERNAME, TestAccountData.PASSWORD);

        // Check if token is valid
        assertTrue(controller.checkAuth(TestAccountData.USERNAME, token.id().toString()));
        // Check if invalid token throws an exception
        assertThrows(AuthenticationFailureException.class, () -> controller.checkAuth(TestAccountData.USERNAME, "a"));
        // Check if incorrect token is detected as such
        assertFalse(controller.checkAuth(TestAccountData.USERNAME, UUID.randomUUID().toString()));

        authRepository.deleteAll();
    }

    @Test
    @Order(2)
    public void repeatedAuthTest() {
        // Generate two tokens
        AuthToken oldToken = controller.login(TestAccountData.USERNAME, TestAccountData.PASSWORD);
        AuthToken newToken = controller.login(TestAccountData.USERNAME, TestAccountData.PASSWORD);

        // Ensure the tokens aren't equal
        assertNotEquals(oldToken.id().toString(), newToken.id().toString());

        // Ensure the right token is authed
        assertTrue(controller.checkAuth(TestAccountData.USERNAME, newToken.id().toString()));
        assertFalse(controller.checkAuth(TestAccountData.PASSWORD, oldToken.id().toString()));

        authRepository.deleteAll();
    }

    @Test
    @Order(2)
    public void logoutTest() {
        // Create token
        AuthToken token = controller.login(TestAccountData.USERNAME, TestAccountData.PASSWORD);

        // Ensure token is authed
        assertTrue(controller.checkAuth(TestAccountData.USERNAME, token.id().toString()));
        // Deauth token
        assertTrue(controller.logout(token.id().toString()));
        // Ensure token is deauthed
        assertFalse(controller.checkAuth(TestAccountData.USERNAME, token.id().toString()));
    }
}
