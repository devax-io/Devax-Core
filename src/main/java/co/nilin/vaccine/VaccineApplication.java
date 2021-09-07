package co.nilin.vaccine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = {"co.nilin.vaccine"})
@EntityScan(basePackages = {"co.nilin.vaccine.model"})
@Configuration
public class VaccineApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaccineApplication.class, args);
    }




}
