package seal.main;

import org.json.JSONException;
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
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
@RestController
public class MainApplication {

	@GetMapping("/createApplicant")
	public Applicant createApplicant  (
			@RequestParam (value="hashedid", defaultValue = "hashedID40210") String hashedid,
			@RequestParam (value="username", defaultValue = "testUserName") String username,
			@RequestParam (value="fname", defaultValue = "first") String fname,
			@RequestParam (value="lname", defaultValue = "last") String lname,
			@RequestParam (value="dob", defaultValue = "1969-05-04") String dob,
			@RequestParam (value="a_city", defaultValue = "1969-05-04") String a_city,
			@RequestParam (value="a_state", defaultValue = "1969-05-04") String a_state,
			@RequestParam (value="a_phonenumber", defaultValue = "1969-05-04") String a_phonenumber,
			@RequestParam (value="a_email", defaultValue = "1969-05-04") String a_email
	) throws SQLException, ClassNotFoundException, IOException, ParseException {
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	cal.setTime(sdf.parse(dob));
		Applicant toReturn = new Applicant(hashedid, username, fname, lname, cal, a_city, a_state, a_phonenumber, a_email);
		toReturn.createApplicant();
		return toReturn;
	}

	@GetMapping(value="/fetchApplicants", produces = {"application/json"})
	public String fetchApplicants () throws SQLException, ClassNotFoundException, JSONException {
		JSONArray jobs = new Applicant().browseJobs();

		return jobs.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
