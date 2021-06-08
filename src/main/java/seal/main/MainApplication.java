package seal.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@SpringBootApplication
@RestController
public class MainApplication {

	@GetMapping("/applicant")
	public Applicant createApplicant(@RequestParam(value="name", defaultValue = "test") String name) throws SQLException, ClassNotFoundException {
		return new Applicant();
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
