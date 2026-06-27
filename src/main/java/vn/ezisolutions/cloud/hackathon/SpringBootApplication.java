package vn.ezisolutions.cloud.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableScheduling
public class SpringBootApplication {

    public static void main(String[] args) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(vn.ezisolutions.cloud.hackathon.core.shared.TimeZone.DEFAULT));
        SpringApplication.run(SpringBootApplication.class, args);
    }

}
