package tv.vradio.vradiotvserver.account.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash("AuthToken")
public record AuthToken(@Id UUID token, String accountName) {}
