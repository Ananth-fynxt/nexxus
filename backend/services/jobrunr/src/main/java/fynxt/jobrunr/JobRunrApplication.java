package fynxt.jobrunr;

import org.jobrunr.spring.autoconfigure.JobRunrAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {JobRunrAutoConfiguration.class})
public class JobRunrApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobRunrApplication.class, args);
	}
}
