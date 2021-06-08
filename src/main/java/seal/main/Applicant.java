package seal.main;

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
import org.json.JSONException;
import org.json.JSONObject;

public class Applicant {
	private ArrayList<String> dbInfo;
	private ArrayList<String> profileRankings;
	private Map<String, Employer> followedEmps;
	private String hashedID;
	private String username;
	private String fname;
	private String lname;
	private java.sql.Date dob;

	public Applicant() throws ClassNotFoundException, SQLException {
		this.hashedID = "123456";
		this.username = "Test";
		this.fname = "John";
		this.lname = "Doe";
		Calendar cal = Calendar.getInstance();
		this.dob = new java.sql.Date(cal.getTimeInMillis());
		this.dbInfo = new ArrayList<String>();
		this.dbInfo.add("com.mysql.cj.jdbc.Driver");
		this.dbInfo.add(
				"jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false");
		this.dbInfo.add("KingSeal@thesealsearchserver");
		this.dbInfo.add("Password1");
		this.profileRankings = new ArrayList<String>();
		String[] tempRankings = {"1", "1", "1", "1", "1", "1", "1", "1", "1"};
		updateProfile(tempRankings);
		this.followedEmps = new HashMap<>();
	}

	public Applicant(String hid) throws ClassNotFoundException, SQLException {
		this.hashedID = hid;
		this.dbInfo = new ArrayList<String>();
		this.dbInfo.add("com.mysql.cj.jdbc.Driver");
		this.dbInfo.add(
				"jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false");
		this.dbInfo.add("KingSeal@thesealsearchserver");
		this.dbInfo.add("Password1");
		this.profileRankings = new ArrayList<String>();
		this.followedEmps = new HashMap<>();
	}

	public Applicant(String hid, String un, String first, String last, Calendar dobCal)
			throws ClassNotFoundException, SQLException {
		this.hashedID = hid;
		this.username = un;
		this.fname = first;
		this.lname = last;
		Calendar cal = dobCal;
		this.dob = new java.sql.Date(cal.getTimeInMillis());
		this.dbInfo = new ArrayList<String>();
		this.dbInfo.add("com.mysql.cj.jdbc.Driver");
		this.dbInfo.add(
				"jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false");
		this.dbInfo.add("KingSeal@thesealsearchserver");
		this.dbInfo.add("Password1");
		this.profileRankings = new ArrayList<String>();
		this.followedEmps = new HashMap<>();
	}

	public void createApplicant() throws ClassNotFoundException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con = DriverManager.getConnection(dbInfo.get(1), dbInfo.get(2), dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"INSERT INTO APPLICANT (a_hashedid, a_username, a_fname, a_lname, a_dob) VALUES (?, ?, ?, ?, ?)");
		ps.setString(1, this.hashedID);
		ps.setString(2, this.username);
		ps.setString(3, this.fname);
		ps.setString(4, this.lname);
		ps.setDate(5, this.dob);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By createApplicant() Query = " + count);
		con.close();
		ps.close();
	}

	public void uploadResume(String filepath)
			throws ClassNotFoundException, IOException, SQLException {
		Class.forName(dbInfo.get(0));
		String fp = filepath;
		File pdfFile = new File(fp);
		byte[] pdfData = new byte[(int) pdfFile.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(pdfFile));
		dis.readFully(pdfData);
		dis.close();
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps =
				con.prepareStatement("UPDATE APPLICANT SET a_resumePDF = ? WHERE a_hashedID = ?;");
		ps.setBytes(1, pdfData);
		ps.setString(2, this.hashedID);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By uploadResume() Query = " + count);
		con.close();
		ps.close();
	}

	public void retrieveResume() throws ClassNotFoundException, IOException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps =
				con.prepareStatement("SELECT a_resumePDF FROM APPLICANT WHERE a_hashedID = ?;");
		ps.setString(1, this.hashedID);
		ResultSet rs = ps.executeQuery();
		Random random = new Random();
		String ext = ".pdf";
		String name = String.format("%s%s", System.currentTimeMillis(), random.nextInt(100000) + ext);
		File file = new File(name);
		FileOutputStream output = new FileOutputStream(file);
		System.out.println("Writing to file...");
		while (rs.next()) {
			InputStream input = rs.getBlob("a_resumePDF").getBinaryStream();
			byte[] buffer = new byte[(int) rs.getBlob("a_resumePDF").length()];
			while (input.read(buffer) > 0) {
				output.write(buffer);
			}
		}
		System.out.println("File Saved Here: " + file.getAbsolutePath());
		output.close();
		ps.close();
		con.close();
	}

	public void updateProfile(String[] rankings) throws ClassNotFoundException, SQLException {
		Class.forName(dbInfo.get(0));
		for (int i = 0; i < rankings.length; i++) {
			this.profileRankings.add(rankings[i]);
		}
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"UPDATE APPLICANT SET a_tech_yearsofexp = ?, a_tech_problemsolving = ?, a_tech_degree = ?, a_busi_jobtype = ?, a_busi_growthopp = ?, a_busi_companysize = ?, a_cult_consistency = ?, a_cult_communication = ?, a_cult_leadership = ? WHERE a_hashedid = ?;");
		for (int i = 0; i < rankings.length; i++) {
			ps.setString(i + 1, rankings[i]);
		}
		ps.setString(10, this.hashedID);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By updateProfile() Query = " + count);
		con.close();
		ps.close();
	}

	public void updateApplicant() throws ClassNotFoundException, ParseException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement("SELECT * FROM APPLICANT WHERE a_hashedID = ?;");
		ps.setString(1, this.hashedID);
		ResultSet rs = ps.executeQuery();
		ArrayList<String> appInfo = new ArrayList<String>();
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				appInfo.add(rs.getString(i));
			}
		}
		this.hashedID = appInfo.get(0);
		this.username = appInfo.get(1);
		this.fname = appInfo.get(2);
		this.lname = appInfo.get(3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date parsed = sdf.parse(appInfo.get(4));
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		this.dob = sql;
		this.profileRankings = new ArrayList<String>();
		for (int i = 7; i < appInfo.size() - 1; i++) {
			this.profileRankings.add(appInfo.get(i));
		}
	}

	public void updateRankings() throws ClassNotFoundException, SQLException {
		Class.forName(dbInfo.get(0));
		this.profileRankings = new ArrayList<String>();
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT a_tech_yearsofexp, a_tech_problemsolving, a_tech_degree, a_busi_jobtype, a_busi_growthopp, a_busi_companysize, a_cult_consistency, a_cult_communication, a_cult_leadership FROM APPLICANT WHERE a_hashedid = ?;");
		ps.setString(1, this.hashedID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				this.profileRankings.add(rs.getString(i));
			}
		}
		System.out.println("Retrieved Rankings: " + this.profileRankings.toString());
		con.close();
		ps.close();
		rs.close();
	}

	public JSONObject viewEmployer(String e_hid) throws ClassNotFoundException, SQLException, JSONException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT e_hashedID, e_companyName, e_username, e_fname, e_lname, e_dob, e_tech_yearsofexp, e_tech_problemsolving, e_tech_degree, e_busi_jobtype, e_busi_growthopp, e_busi_companysize, e_cult_consistency, e_cult_communication, e_cult_leadership FROM EMPLOYER WHERE e_hashedID = ?;");
		ps.setString(1, e_hid);
		ResultSet rs = ps.executeQuery();
		JSONObject empObject = null;
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				empObject = new JSONObject();
				ArrayList<String> ranks = new ArrayList<String>();
				empObject.put("e_hashedID", rs.getString(i));
				empObject.put("e_companyName", rs.getString(i + 1));
				empObject.put("e_username", rs.getString(i + 2));
				empObject.put("e_fname", rs.getString(i + 3));
				empObject.put("e_lname", rs.getString(i + 4));
				empObject.put("e_dob", rs.getString(i + 5));
				ranks.add(rs.getString(i + 6));
				ranks.add(rs.getString(i + 7));
				ranks.add(rs.getString(i + 8));
				ranks.add(rs.getString(i + 9));
				ranks.add(rs.getString(i + 10));
				ranks.add(rs.getString(i + 11));
				ranks.add(rs.getString(i + 12));
				ranks.add(rs.getString(i + 13));
				ranks.add(rs.getString(i + 14));
				empObject.put("e_rankings", ranks.toString());
				i += 15;
			}
		}
		return empObject;
	}

	public JSONArray browseJobs() throws ClassNotFoundException, SQLException, JSONException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT e_hashedID, e_companyName, e_username, e_fname, e_lname, e_dob, e_tech_yearsofexp, e_tech_problemsolving, e_tech_degree, e_busi_jobtype, e_busi_growthopp, e_busi_companysize, e_cult_consistency, e_cult_communication, e_cult_leadership FROM EMPLOYER WHERE e_jobListingPDF IS NOT NULL;");
		ResultSet rs = ps.executeQuery();
		JSONArray fl = new JSONArray();
		JSONObject empObject;
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				empObject = new JSONObject();
				ArrayList<String> ranks = new ArrayList<String>();
				empObject.put("e_hashedID", rs.getString(i));
				empObject.put("e_companyName", rs.getString(i + 1));
				empObject.put("e_username", rs.getString(i + 2));
				empObject.put("e_fname", rs.getString(i + 3));
				empObject.put("e_lname", rs.getString(i + 4));
				empObject.put("e_dob", rs.getString(i + 5));
				ranks.add(rs.getString(i + 6));
				ranks.add(rs.getString(i + 7));
				ranks.add(rs.getString(i + 8));
				ranks.add(rs.getString(i + 9));
				ranks.add(rs.getString(i + 10));
				ranks.add(rs.getString(i + 11));
				ranks.add(rs.getString(i + 12));
				ranks.add(rs.getString(i + 13));
				ranks.add(rs.getString(i + 14));
				empObject.put("e_rankings", ranks.toString());
				fl.put(empObject);
				i += 15;
			}
		}
		return fl;
	}

	public void followEmployer(String e_hid)
			throws ClassNotFoundException, ParseException, SQLException {
		Employer emp = new Employer(e_hid);
		emp.updateEmployer();
		followedEmps.put(emp.getHashedID(), emp);
	}

	public void updateFollowedList() throws ClassNotFoundException, SQLException, JSONException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		JSONArray fl = new JSONArray();
		JSONObject empObject;
		PreparedStatement ps =
				con.prepareStatement("UPDATE APPLICANT SET a_followedEmployers = ? WHERE a_hashedID = ?;");
		for (Employer value : this.followedEmps.values()) {
			empObject = new JSONObject();
			empObject.put("HID", value.getHashedID());
			empObject.put("CompanyName", value.getCompanyName());
			empObject.put("Username", value.getUsername());
			empObject.put("FirstName", value.getFirstName());
			empObject.put("LastName", value.getLastName());
			empObject.put("DOB", value.getDOB());
			empObject.put("Rankings", value.getProfileRankings().toString());
			fl.put(empObject);
		}
		ps.setString(1, fl.toString());
		ps.setString(2, this.hashedID);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By updateFollowedList() Query = " + count);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sb.append("HashedID: " + this.hashedID + "\n" + "Username: " + this.username + "\n"
				+ "First Name: " + this.fname + "\n" + "Last Name: " + this.lname + "\n" + "DOB: "
				+ sdf.format(this.dob) + "\n");
		sb.append("Years Of Experience: " + this.profileRankings.get(0) + "\n" + "Problem Solving: "
				+ this.profileRankings.get(1) + "\n" + "Degree: " + this.profileRankings.get(2) + "\n");
		sb.append("Job Type: " + this.profileRankings.get(3) + "\n" + "Growth Opportunity: "
				+ this.profileRankings.get(4) + "\n" + "Company Size: " + this.profileRankings.get(5)
				+ "\n");
		sb.append("Consistency: " + this.profileRankings.get(6) + "\n" + "Communication: "
				+ this.profileRankings.get(7) + "\n" + "Leadership: " + this.profileRankings.get(8) + "\n");
		sb.append("Followed Employers: " + this.followedEmps.toString());
		return sb.toString();
	}

	public void setHashedID(String hid) {
		this.hashedID = hid;
	}

	public void setUsername(String un) {
		this.username = un;
	}

	public void setFname(String first) {
		this.fname = first;
	}

	public void setLname(String last) {
		this.lname = last;
	}

	public void setDOB(Calendar cal) {
		this.dob = new java.sql.Date(cal.getTimeInMillis());
	}

	public ArrayList<String> getDBInfo() {
		return this.dbInfo;
	}

	public ArrayList<String> getProfileRankings() {
		return this.profileRankings;
	}

	public Map<String, Employer> getFollowedEmps() {
		return this.followedEmps;
	}

	public String getHashedID() {
		return this.hashedID;
	}

	public String getUsername() {
		return this.username;
	}

	public String getFirstName() {
		return this.fname;
	}

	public String getLastName() {
		return this.lname;
	}

	public java.sql.Date getDOB() {
		return this.dob;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, ParseException, SQLException, JSONException {
		Applicant appDefault = new Applicant();
		appDefault.updateRankings();
		System.out.println(appDefault.getHashedID() + "\n" + appDefault.getUsername() + "\n"
				+ appDefault.getFirstName() + " " + appDefault.getLastName() + "\n"
				+ appDefault.getDOB().toString());
		System.out.println(
				appDefault.getDBInfo().toString() + "\n" + appDefault.getProfileRankings().toString() + "\n"
						+ appDefault.getFollowedEmps().toString() + "\n");
		Applicant appExists = new Applicant("2129704133");
		System.out.println("Before: " + appExists.hashedID + " " + appExists.username + " "
				+ appExists.fname + " " + appExists.lname + " " + " " + appExists.dob + " "
				+ appExists.profileRankings + " " + appExists.followedEmps);
		appExists.updateApplicant();
		System.out.println("After: " + appExists.hashedID + " " + appExists.username + " "
				+ appExists.fname + " " + appExists.lname + " " + appExists.dob + " "
				+ appExists.profileRankings.toString() + " " + appExists.followedEmps + "\n");
		Applicant appSetTest = new Applicant();
		appSetTest.setHashedID("987654320");
		appSetTest.setUsername("Captain America");
		appSetTest.setFname("Steve");
		appSetTest.setLname("Rogers");
		Calendar cal = Calendar.getInstance();
		cal.set(1918, 06, 04);
		appSetTest.setDOB(cal);
		String[] rankings2 = {"100", "101", "102", "103", "104", "105", "106", "107", "108"};
		appSetTest.createApplicant();
		appSetTest.uploadResume("C:/Users/Jeremy/Desktop/captain.pdf");
		appSetTest.updateProfile(rankings2);
		appSetTest.retrieveResume();
		System.out.println("\n" + appSetTest.toString() + "\n");
		JSONArray test = appSetTest.browseJobs();
		for (int i = 0; i < test.length(); i++) {
			System.out.println(test.get(i).toString());
		}
		System.out.println("\n");
		appSetTest.followEmployer("9^-*l#PWxi6}j,w");
		appSetTest.followEmployer("Xp2s5v8y/A?D(G+K");
		appSetTest.followEmployer("123456");
		appSetTest.updateFollowedList();
		System.out.println("\n");
		System.out.println(appSetTest.toString());
	}

}