package tv.vradio.vradiotvserver.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "hashed_password")
    private String hashedPassword;
    @Column(name = "verified")
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
