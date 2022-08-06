package tv.vradio.vradiotvserver.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final UUID id = UUID.randomUUID();

    private String username;
    private String email;
    private String hashedPassword;
    private boolean verified;

    protected Account() {}

    public Account(String username, String email, String hashedPassword, boolean verified) {
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.verified = verified;
    }

    @Override
    public String toString() {
        return String.format(
                "Account[id='%s', username='%s', email='%s', hashedPassword='%s', verified=%s",
                id, username, email, hashedPassword, verified);
    }
}
