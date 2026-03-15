package org.cybercaelum.household_management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
@Slf4j
public class HouseholdManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(HouseholdManagementApplication.class, args);
        log.info("Application started");
    }
}
