package com.oweu.authorizationserver;

import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthorizationserverApplication {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationserverApplication.class);

    public static void main(String[] args) {

        logger.info("Our auth server is ready to start");
        SpringApplication.run(AuthorizationserverApplication.class, args);
        logger.info("Our auth server is running!");

    }
}
