package revi1337.configurationpropertiesdemo.property;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.context.properties.bind.Nested;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@Getter
@ToString
@ConfigurationProperties("token")
public class TokenProperties {

    private AccessToken accessToken;

    private RefreshToken refreshToken;

    public TokenProperties(
            @DefaultValue AccessToken accessToken,
            @DefaultValue RefreshToken refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Getter
    @ToString
    static class AccessToken {

        @DurationMin(seconds = 1) @DurationMax(seconds = 30) private Duration expired;
        @Length(min = 1, max = 20) private String secretKey;

        public AccessToken(
                @DefaultValue("1s") Duration expired,
                @DefaultValue("default-access-key") String secretKey
        ) {
            this.expired = expired;
            this.secretKey = secretKey;
        }
    }

    @Getter
    @ToString
    static class RefreshToken {

        @DurationMin(seconds = 1) @DurationMax(seconds = 30) private Duration expired;
        @Length(min = 1, max = 20) private String secretKey;

        public RefreshToken(
                @DefaultValue("3s") Duration expired,
                @DefaultValue("default-refresh-key") String secretKey
        ) {
            this.expired = expired;
            this.secretKey = secretKey;
        }
    }
}

