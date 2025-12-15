package com.academic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@MapperScan("com.academic.mapper")
@EnableScheduling
public class AcademicWarningApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicWarningApplication.class, args);
    }

}
