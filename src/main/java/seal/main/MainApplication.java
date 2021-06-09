package seal.main;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;



import seal.main.Applicant;
import seal.main.Employer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
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

	@GetMapping("/login")
	public Applicant foundApplicant(@RequestParam (value="username", defaultValue = "") String usernameParam)
			throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException, ParseException {
		Applicant validApplicant;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String QUERY = "SELECT * FROM applicant WHERE a_username = '%s'";
		Class.forName("com.mysql.jdbc.Driver");
		String azureURL = "jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false";
		Connection conn = DriverManager.getConnection(azureURL, "KingSeal@thesealsearchserver", "Password1");

		Statement queryStatement = conn.createStatement();
		QUERY = String.format(QUERY, usernameParam);

		ResultSet rs = queryStatement.executeQuery(QUERY);
		rs.next();

		cal.setTime(sdf.parse(rs.getString("a_dob")));
		validApplicant = new Applicant(
				rs.getString("a_hashedID"),
				rs.getString("a_username"),
				rs.getString("a_fname"),
				rs.getString("a_lname"),
											cal,
				rs.getString("a_city"),
				rs.getString("a_state"),
				rs.getString("a_phoneNumber"),
				rs.getString("a_email")
		);

		return validApplicant;
	}

	@PostMapping(value="/follow", produces = {"application/json"})
	// need to get a hashed employer ID from front end
	public String follow(@RequestParam (value="employerID", defaultValue = "hashed") String employerID) {
		//todo -> how are employers saved?
		return "";
	}

	@GetMapping(value="/viewCompany", produces = {"application/json"})
	public String employer(@RequestParam (value="hashedID", defaultValue = "hashedID40210") String hashedID)
	throws SQLException, ClassNotFoundException, JSONException {
		return new Applicant().viewEmployer(hashedID).toString();
	}

	@GetMapping(value="/browseJobs", produces = {"application/json"})
	public String fetchApplicants () throws SQLException, ClassNotFoundException, JSONException {
		JSONArray jobs = new Applicant().browseJobs();

		return jobs.toString();
	}

	@GetMapping(value = "/viewResumePDF")
    public ResponseEntity<InputStreamResource> getResume (
		@RequestParam (value="hashedid", defaultValue = "hashedID40210") String hashedid
	)throws SQLException, ClassNotFoundException, IOException, ParseException {

        Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con =
        DriverManager.getConnection("jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false", "KingSeal@thesealsearchserver", "Password1");
    PreparedStatement ps =
        con.prepareStatement("SELECT a_resumePDF FROM APPLICANT WHERE a_hashedID = ?;");
    ps.setString(1, hashedid);
    ResultSet rs = ps.executeQuery();
    Random random = new Random();
    String ext = ".pdf";
    String name = String.format("%s%s", System.currentTimeMillis(), random.nextInt(100000) + ext);
    File file = new File(name);
    FileOutputStream output = new FileOutputStream(file);
    while (rs.next()) {
      InputStream input = rs.getBlob("a_resumePDF").getBinaryStream();
      byte[] buffer = new byte[(int) rs.getBlob("a_resumePDF").length()];
      while (input.read(buffer) > 0) {
        output.write(buffer);
      }
    }
        HttpHeaders headers = new HttpHeaders();      
        headers.add("content-disposition", "inline;filename=" +file.getAbsolutePath());
        
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

	@GetMapping(value = "/viewProfilePic")
    public ResponseEntity<InputStreamResource> ProfilePic (
		@RequestParam (value="hashedid", defaultValue = "hashedID40210") String hashedid
	)throws SQLException, ClassNotFoundException, IOException, ParseException {

        Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con =
        DriverManager.getConnection("jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false", "KingSeal@thesealsearchserver", "Password1");
    PreparedStatement ps =
        con.prepareStatement("SELECT a_profilePicture FROM APPLICANT WHERE a_hashedID = ?;");
    ps.setString(1, hashedid);
    ResultSet rs = ps.executeQuery();
    Random random = new Random();
    String ext = ".pdf";
    String name = String.format("%s%s", System.currentTimeMillis(), random.nextInt(100000) + ext);
    File file = new File(name);
    FileOutputStream output = new FileOutputStream(file);
    while (rs.next()) {
      InputStream input = rs.getBlob("a_profilePicture").getBinaryStream();
      byte[] buffer = new byte[(int) rs.getBlob("a_profilePicture").length()];
      while (input.read(buffer) > 0) {
        output.write(buffer);
      }
    }
        HttpHeaders headers = new HttpHeaders();      
        headers.add("content-disposition", "inline;filename=" +file.getAbsolutePath());
        
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
