package clickme.clickme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HeartItApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeartItApplication.class, args);
	}

}
