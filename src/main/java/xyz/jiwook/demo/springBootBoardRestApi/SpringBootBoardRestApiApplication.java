package xyz.jiwook.demo.springBootBoardRestApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringBootBoardRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBoardRestApiApplication.class, args);
	}

}
