package tv.vradio.vradiotvserver.account.auth;

import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@RedisHash("AuthToken")
public record AuthToken(UUID id, String accountName) {}