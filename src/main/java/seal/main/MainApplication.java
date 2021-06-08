package seal.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

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
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	cal.setTime(sdf.parse(DOB));
		toReturn.setDOB(cal);
		toReturn.setHashedID(hash);
		return toReturn;
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
