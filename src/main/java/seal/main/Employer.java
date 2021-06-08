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
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Employer {
	private ArrayList<String> dbInfo;
	private ArrayList<String> profileRankings;
	private String hashedID;
	private String companyName;
	private String username;
	private String fname;
	private String lname;
	private java.sql.Date dob;

	public Employer() throws ClassNotFoundException, SQLException {
		this.hashedID = "123456";
		this.companyName = "Testudios";
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
	}

	public Employer(String hid) {
		this.hashedID = hid;
		this.dbInfo = new ArrayList<String>();
		this.dbInfo.add("com.mysql.cj.jdbc.Driver");
		this.dbInfo.add(
				"jdbc:mysql://thesealsearchserver.mysql.database.azure.com:3306/sealdb?useSSL=true&requireSSL=false");
		this.dbInfo.add("KingSeal@thesealsearchserver");
		this.dbInfo.add("Password1");
		this.profileRankings = new ArrayList<String>();
	}

	public Employer(String hid, String compName, String un, String first, String last,
					Calendar dobCal) {
		this.hashedID = hid;
		this.companyName = compName;
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
	}

	public void createEmployer() throws ClassNotFoundException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con = DriverManager.getConnection(dbInfo.get(1), dbInfo.get(2), dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"INSERT INTO EMPLOYER (e_hashedid, e_companyName, e_username, e_fname, e_lname, e_dob) VALUES (?, ?, ?, ?, ?, ?)");
		ps.setString(1, this.hashedID);
		ps.setString(2, this.companyName);
		ps.setString(3, this.username);
		ps.setString(4, this.fname);
		ps.setString(5, this.lname);
		ps.setDate(6, this.dob);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By createEmployer() Query = " + count);
		con.close();
		ps.close();
	}

	public void uploadJobListing(String filepath)
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
				con.prepareStatement("UPDATE EMPLOYER SET e_jobListingPDF = ? WHERE e_hashedID = ?;");
		ps.setBytes(1, pdfData);
		ps.setString(2, this.hashedID);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By uploadJobListing() Query = " + count);
		con.close();
		ps.close();
	}

	public void retrieveJobListing() throws ClassNotFoundException, IOException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps =
				con.prepareStatement("SELECT e_jobListingPDF FROM EMPLOYER WHERE e_hashedID = ?;");
		ps.setString(1, this.hashedID);
		ResultSet rs = ps.executeQuery();
		Random random = new Random();
		String ext = ".pdf";
		String name = String.format("%s%s", System.currentTimeMillis(), random.nextInt(100000) + ext);
		File file = new File(name);
		FileOutputStream output = new FileOutputStream(file);
		System.out.println("Writing to file...");
		while (rs.next()) {
			InputStream input = rs.getBlob("e_jobListingPDF").getBinaryStream();
			byte[] buffer = new byte[(int) rs.getBlob("e_jobListingPDF").length()];
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
		this.profileRankings = new ArrayList<String>();
		for (int i = 0; i < rankings.length; i++) {
			this.profileRankings.add(rankings[i]);
		}
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"UPDATE EMPLOYER SET e_tech_yearsofexp = ?, e_tech_problemsolving = ?, e_tech_degree = ?, e_busi_jobtype = ?, e_busi_growthopp = ?, e_busi_companysize = ?, e_cult_consistency = ?, e_cult_communication = ?, e_cult_leadership = ? WHERE e_hashedid = ?;");
		for (int i = 0; i < this.profileRankings.size(); i++) {
			ps.setString(i + 1, this.profileRankings.get(i));
		}
		ps.setString(10, this.hashedID);
		int count = ps.executeUpdate();
		System.out.println("Rows Affected By updateProfile() Query = " + count);
		con.close();
		ps.close();
	}

	public void updateEmployer() throws ClassNotFoundException, ParseException, SQLException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement("SELECT * FROM EMPLOYER WHERE e_hashedID = ?;");
		ps.setString(1, this.hashedID);
		ResultSet rs = ps.executeQuery();
		ArrayList<String> empInfo = new ArrayList<String>();
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				empInfo.add(rs.getString(i));
			}
		}
		this.hashedID = empInfo.get(0);
		this.companyName = empInfo.get(1);
		this.username = empInfo.get(2);
		this.fname = empInfo.get(3);
		this.lname = empInfo.get(4);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date parsed = sdf.parse(empInfo.get(5));
		java.sql.Date sql = new java.sql.Date(parsed.getTime());
		this.dob = sql;
		this.profileRankings = new ArrayList<String>();
		for (int i = 8; i < empInfo.size(); i++) {
			this.profileRankings.add(empInfo.get(i));
		}
	}

	public void updateRankings() throws ClassNotFoundException, SQLException {
		Class.forName(dbInfo.get(0));
		this.profileRankings = new ArrayList<String>();
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT e_tech_yearsofexp, e_tech_problemsolving, e_tech_degree, e_busi_jobtype, e_busi_growthopp, e_busi_companysize, e_cult_consistency, e_cult_communication, e_cult_leadership FROM EMPLOYER WHERE e_hashedid = ?;");
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

	public JSONObject viewApplicant(String a_hid) throws ClassNotFoundException, SQLException, JSONException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT a_hashedid, a_username, a_fname, a_lname, a_dob, a_tech_yearsofexp, a_tech_problemsolving, a_tech_degree, a_busi_jobtype, a_busi_growthopp, a_busi_companysize, a_cult_consistency, a_cult_communication, a_cult_leadership FROM APPLICANT WHERE a_hashedID = ?;");
		ps.setString(1, a_hid);
		ResultSet rs = ps.executeQuery();
		JSONObject empObject = null;
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				empObject = new JSONObject();
				ArrayList<String> ranks = new ArrayList<String>();
				empObject.put("a_hashedID", rs.getString(i));
				empObject.put("a_username", rs.getString(i + 1));
				empObject.put("a_fname", rs.getString(i + 2));
				empObject.put("a_lname", rs.getString(i + 3));
				empObject.put("a_dob", rs.getString(i + 4));
				ranks.add(rs.getString(i + 5));
				ranks.add(rs.getString(i + 6));
				ranks.add(rs.getString(i + 7));
				ranks.add(rs.getString(i + 8));
				ranks.add(rs.getString(i + 9));
				ranks.add(rs.getString(i + 10));
				ranks.add(rs.getString(i + 11));
				ranks.add(rs.getString(i + 12));
				ranks.add(rs.getString(i + 13));
				empObject.put("a_rankings", ranks.toString());
				i += 14;
			}
		}
		return empObject;
	}

	public JSONArray browseApplicants() throws ClassNotFoundException, SQLException, JSONException {
		Class.forName(dbInfo.get(0));
		Connection con =
				DriverManager.getConnection(this.dbInfo.get(1), this.dbInfo.get(2), this.dbInfo.get(3));
		PreparedStatement ps = con.prepareStatement(
				"SELECT a_hashedid, a_username, a_fname, a_lname, a_dob, a_tech_yearsofexp, a_tech_problemsolving, a_tech_degree, a_busi_jobtype, a_busi_growthopp, a_busi_companysize, a_cult_consistency, a_cult_communication, a_cult_leadership FROM APPLICANT WHERE a_resumePDF IS NOT NULL;");
		ResultSet rs = ps.executeQuery();
		JSONArray fl = new JSONArray();
		JSONObject empObject;
		while (rs.next()) {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				empObject = new JSONObject();
				ArrayList<String> ranks = new ArrayList<String>();
				empObject.put("a_hashedID", rs.getString(i));
				empObject.put("a_username", rs.getString(i + 1));
				empObject.put("a_fname", rs.getString(i + 2));
				empObject.put("a_lname", rs.getString(i + 3));
				empObject.put("a_dob", rs.getString(i + 4));
				ranks.add(rs.getString(i + 5));
				ranks.add(rs.getString(i + 6));
				ranks.add(rs.getString(i + 7));
				ranks.add(rs.getString(i + 8));
				ranks.add(rs.getString(i + 9));
				ranks.add(rs.getString(i + 10));
				ranks.add(rs.getString(i + 11));
				ranks.add(rs.getString(i + 12));
				ranks.add(rs.getString(i + 13));
				empObject.put("a_rankings", ranks.toString());
				fl.put(empObject);
				i += 14;
			}
		}
		return fl;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sb.append("HashedID: " + this.hashedID + "\n" + "Company Name: " + this.companyName + "\n"
				+ "Username: " + this.username + "\n" + "First Name: " + this.fname + "\n" + "Last Name: "
				+ this.lname + "\n" + "DOB: " + sdf.format(this.dob) + "\n");
		sb.append("Years Of Experience: " + this.profileRankings.get(0) + "\n" + "Problem Solving: "
				+ this.profileRankings.get(1) + "\n" + "Degree: " + this.profileRankings.get(2) + "\n");
		sb.append("Job Type: " + this.profileRankings.get(3) + "\n" + "Growth Opportunity: "
				+ this.profileRankings.get(4) + "\n" + "Company Size: " + this.profileRankings.get(5)
				+ "\n");
		sb.append("Consistency: " + this.profileRankings.get(6) + "\n" + "Communication: "
				+ this.profileRankings.get(7) + "\n" + "Leadership: " + this.profileRankings.get(8));
		return sb.toString();
	}

	public void setHashedID(String hid) {
		this.hashedID = hid;
	}

	public void setCompanyName(String compName) {
		this.companyName = compName;
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

	public String getHashedID() {
		return this.hashedID;
	}

	public String getCompanyName() {
		return this.companyName;
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
		Employer emp = new Employer();
		emp.updateRankings();
		System.out.println(emp.getHashedID() + "\n" + emp.getCompanyName() + "\n" + emp.getUsername()
				+ "\n" + emp.getFirstName() + " " + emp.getLastName() + "\n" + emp.getDOB().toString());
		System.out
				.println(emp.getDBInfo().toString() + "\n" + emp.getProfileRankings().toString() + "\n");
		emp = new Employer("Xp2s5v8y/A?D(G+K");
		System.out.println("Before: " + emp.hashedID + " " + emp.companyName + " " + emp.username + " "
				+ emp.fname + " " + emp.lname + " " + emp.profileRankings);
		emp.updateEmployer();
		System.out.println(
				"After: " + emp.hashedID + " " + emp.companyName + " " + emp.username + " " + emp.fname
						+ " " + emp.lname + " " + emp.dob + " " + emp.getProfileRankings().toString() + "\n");
		emp.setHashedID("9^-*l#PWxi6}j,w");
		emp.setCompanyName("Reddit");
		emp.setUsername("CEO");
		emp.setFname("Steve");
		emp.setLname("Huffman");
		Calendar cal = Calendar.getInstance();
		cal.set(1983, 10, 12);
		emp.setDOB(cal);
		String[] rankings = {"20", "21", "22", "23", "24", "25", "26", "27", "28"};
		emp.createEmployer();
		emp.uploadJobListing("C:/Users/Jeremy/Desktop/reddit.pdf");
		emp.updateProfile(rankings);
		emp.retrieveJobListing();
		System.out.println("\n" + emp.toString() + "\n");
		JSONArray test = emp.browseApplicants();
		System.out.println("Looping Thru Applicant Result Set Now!");
		for (int i = 0; i < test.length(); i++) {
			System.out.println(test.get(i).toString());
		}
	}

}