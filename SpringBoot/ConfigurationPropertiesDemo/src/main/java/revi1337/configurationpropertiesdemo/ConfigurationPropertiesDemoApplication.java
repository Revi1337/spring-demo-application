package revi1337.configurationpropertiesdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ConfigurationPropertiesDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationPropertiesDemoApplication.class, args);
    }
}