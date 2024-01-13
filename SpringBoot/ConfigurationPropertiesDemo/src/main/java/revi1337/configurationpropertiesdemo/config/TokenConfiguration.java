package revi1337.configurationpropertiesdemo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import revi1337.configurationpropertiesdemo.property.TokenProperties;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenConfiguration {

    private final TokenProperties tokenProperties;

    @PostConstruct
    void init() {
        log.info("tokenProperties = {}", tokenProperties);
    }
}
