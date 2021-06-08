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
	public Applicant createApplicant  (
			@RequestParam (value="fName", defaultValue = "first") String firstName,
			@RequestParam (value="lName", defaultValue = "last") String lastName,
			@RequestParam (value="DOB", defaultValue = "69/69/6969") String DOB,
			@RequestParam (value="hashedID", defaultValue = "hashedID40210") String hash
		) throws SQLException, ClassNotFoundException {

		Applicant toReturn = new Applicant();
		toReturn.setFname(firstName);
		toReturn.setLname(lastName);
//		toReturn.setDOB(DOB); too lazy, need to turn string to calendar
		toReturn.setHashedID(hash);

		return toReturn;
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
