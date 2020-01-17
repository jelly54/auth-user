package com.jelly.authuser;

import com.jelly.authuser.util.id.IdWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author guodongzhang
 */
@SpringBootApplication
@MapperScan(basePackages = "com.jelly.authuser.dao")
public class AuthUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthUserApplication.class, args);
    }

    @Value("${auth-user.id-worker.data-center-id}")
    private final Long DATA_CENTER_ID = 1L;

    @Value("${auth-user.id-worker.machine-id}")
    private final Long MACHINE_ID = 2L;

    @Bean
    public IdWorker commonIdWorker() {
        return new IdWorker(MACHINE_ID, DATA_CENTER_ID);
    }
}
