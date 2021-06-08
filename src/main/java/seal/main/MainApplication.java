package seal.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import seal.main.Applicant;
import seal.main.Employer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
@RestController
public class MainApplication {

	@GetMapping("/applicant")
	public Applicant createApplicant  (
			@RequestParam (value="hashedID", defaultValue = "hashedID40210") String hashedID,
			@RequestParam (value="username", defaultValue = "testUserName") String username,
			@RequestParam (value="fname", defaultValue = "first") String fname,
			@RequestParam (value="lname", defaultValue = "last") String lname,
			@RequestParam (value="dob", defaultValue = "1969-05-04") String dob
		) throws SQLException, ClassNotFoundException, IOException, ParseException {
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	cal.setTime(sdf.parse(dob));
		Applicant toReturn = new Applicant(hashedID, username, fname, lname, cal);
		toReturn.createApplicant();
		return toReturn;
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
