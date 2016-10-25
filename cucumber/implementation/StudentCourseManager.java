package implementation;

/**
 * Lukkedoerendunandurraskewdylooshoofermoyportertooryzooysphalnabortansporthaokansakroidverjkapakkapuk
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StudentCourseManager {

	private String csvPath;
	private static Connection conn = null;
	private static Statement stmt = null;
	private static String lol = new File("").getAbsolutePath();
	private static Vector<String> allBanners;
	private static Vector<String> allCIs;
	private static Vector<String> allSCTs;
	private static Vector<String> allIs;
	private static Vector<String> allICTs;

	public StudentCourseManager(String CSV) {
		if (CSV.equals("big")) {

			// csvPath = "C:/Users/CPU8/Google Drive/Software
			// Engineering/Project 1 Local backups/cs374_anon.csv";
			/*
			 * try { Runtime.getRuntime().exec(lol.replace("\\", "/") + "
			 * implementation/help.bat");
			 * 
			 * } catch (IOException ie) { Thread.sleep }
			 */

			csvPath = lol + "/implementation/cs374_anon.csv";
		} else
			csvPath = lol + "/implementation/" + CSV;

		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
	}

	public StudentCourseManager() throws SQLException {
		final String DB_URL = "jdbc:mysql://localhost/";

		// Database credentials
		final String USER = "root";
		final String PASS = ""; // insert your password here

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to get mysql driver: " + e);
		} catch (SQLException e) {
			System.err.println("Unable to connect to server: " + e);
		}
		stmt = conn.createStatement();
		stmt.executeUpdate("USE COURSES;");

		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
	}

	public void parseCRN() {
		try {
			// default is: String csvFile = "C:/Users/CPU8/Google Drive/Software
			// Engineering/workspace/Beginnings/src/CSVParsing/cs374_anon.csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			// for the csv
			try {
				br = new BufferedReader(new FileReader(csvPath));
				int i = 1;
				FileWriter studentCSV = new FileWriter(lol + "/studentTable.csv");
				FileWriter ciCSV = new FileWriter(lol + "/ciTable.csv");
				FileWriter sctCSV = new FileWriter(lol + "/sctTable.csv");
				FileWriter instructorCSV = new FileWriter(lol + "/instructorTable.csv");
				FileWriter ictCSV = new FileWriter(lol + "/ictTable.csv");

				/*
				 * Fields we care about:
				 * 
				 * Column | Number in stuff array | CRN: 5 digit number | 35
				 * Subject_Code: * 2-4 string all caps | 40 Course_Number:
				 * 100-599? | 42 Instructor_Name: literally "Last, First" | 50
				 * Grade_Code: only A-D, F, W, and maybe WF | 55 Banner_ID: 9
				 * digit number but usually 3-4 leading 0's | 56 First_Name | 57
				 * Last_Name | 58 Middle_Name | 59 Prefix | 60 Class_Code: FR,
				 * SO, JR, SR, GR, SU | 33 Billing_Hours: hours the course is
				 * worth | 47 Credit_Hours: 0 if not passed, Billing_Hours if is
				 * | 48
				 */

				while ((line = br.readLine()) != null) {
					String[] stuff = StudentCourseManager.newSplit(line);
					if (i == 1) {
						// System.out.println("Creating database...");
						createDatabases(lol + "/implementation/CoursesSetup.sql");
					} else {
						stmt = conn.createStatement();
						if (!(stuff[40].isEmpty() || stuff[42].isEmpty() || stuff[35].isEmpty() || stuff[56].isEmpty()
								|| stuff[50].isEmpty())) {
							stuff[50] = stuff[50].substring(1, stuff[50].length() - 1);

							if (!allBanners.contains(stuff[56])) {
								String writeStudent = "";

								/*
								 * for (int l = 56; l < 61; l++) { writeStudent
								 * += stuff[l] + ","; }
								 */

								writeStudent += stuff[56] + "\n";

								allBanners.add(stuff[56]);
								//System.out.println("student: " + writeStudent);
								studentCSV.append(writeStudent);
							}
							if (!allCIs.contains(stuff[35] + stuff[40] + stuff[42])) {
								String writeCI = "";
								// System.out.println(stuff[50].substring(0,
								// stuff[50].indexOf(",")));
								writeCI += stuff[35] + "," + stuff[40] + stuff[42] + ",";

								// 5 day columns
								for (int j = 72; j < 77; j++)
									writeCI += stuff[j];

								writeCI += "," + stuff[66] + "," + stuff[67] + "," + stuff[1] + "," + stuff[68] + ","
										+ stuff[70] + "," + stuff[128] + "\n";
								allCIs.add(stuff[35] + stuff[40] + stuff[42]); // CRN
																				// +
																				// code
								// System.out.println("course instance: " +
								// writeCI);
								ciCSV.append(writeCI);
							}
							if (!allSCTs.contains(stuff[35] + stuff[40] + stuff[42] + stuff[56])) {
								String writeSCT = "";
								writeSCT += stuff[56] + "," + stuff[35] + "," + stuff[40] + stuff[42]
										+ "," + stuff[33]									
										+ "\n";

								allSCTs.add(stuff[35] + stuff[40] + stuff[42] + stuff[56]);
								//System.out.println("student + course: " + writeSCT);
								sctCSV.append(writeSCT);
							}

							if (!allIs.contains(stuff[50])) {
								String writeI = "";
								// writeI += stuff[50] + "\n";
								if (stuff[50].contains(",")) {
									writeI += stuff[50].substring(stuff[50].indexOf(",") + 2) + " "
											+ stuff[50].substring(0, stuff[50].indexOf(",")) + "\n";
								} else {
									writeI += stuff[50] + "\n";
								}
								// maybe add list to a vector for use later?
								allIs.add(stuff[50]);
								// System.out.println("teacher: " + writeI);
								instructorCSV.append(writeI);
							}

							if (!allICTs.contains(stuff[50] + stuff[35] + stuff[40] + stuff[42])) {
								String writeICT = "";

								if (stuff[50].contains(",")) {
									writeICT += stuff[50].substring(stuff[50].indexOf(",") + 2) + " "
											+ stuff[50].substring(0, stuff[50].indexOf(",")) + "," + stuff[35] + ","
											+ stuff[40] + stuff[42] + "\n";
								} else {
									writeICT += stuff[50] + "," + stuff[35] + "," + stuff[40] + stuff[42] + "\n";
								}

								allIs.add(stuff[50] + stuff[35] + stuff[40] + stuff[42]);
								// System.out.println("teacher + course: " +
								// writeICT);
								ictCSV.append(writeICT);
							}
						}
					}
					// if (i % 10000 == 0) System.out.println(i);
					i++;
				}
				studentCSV.close();
				ciCSV.close();
				sctCSV.close();
				instructorCSV.close();
				ictCSV.close();

				insertStudent("studentTable.csv");
				insertInstructor("instructorTable.csv");
				insertCourseInstance("ciTable.csv");
				insertStudentCourseTaken("sctTable.csv");
				insertICT("ictTable.csv");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}

	private static void createDatabases(String file) throws FileNotFoundException, IOException, SQLException {
		final String DB_URL = "jdbc:mysql://localhost/";

		// Database credentials
		final String USER = "root";
		final String PASS = ""; // insert your password here
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to get mysql driver: " + e);
		} catch (SQLException e) {
			System.err.println("Unable to connect to server: " + e);
		}
		ScriptRunner runner = new ScriptRunner(conn, false, false);
		runner.runScript(new BufferedReader(new FileReader(file)));
	}

	private static void insertStudent(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn + "' INTO TABLE student "
				+ "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}

	public boolean studentExists(String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM student WHERE banner = '" + banner + "';");
		rs.next();
		// System.out.println("num: " + rs.getInt("total"));
		return rs.getInt("total") == 1;
	}

	//rendered obsolete by student classification changing over different semesters
	public String studentClass(String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT classification FROM student WHERE banner = '" + banner + "';");
		rs.next();
		return rs.getString("classification");
	}

	private static void insertCourseInstance(String csvIn) throws SQLException {
		String sqlCourseInstance = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE courseInstances " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlCourseInstance);

		stmt.executeUpdate(sqlCourseInstance);
	}

	private static void insertStudentCourseTaken(String csvIn) throws SQLException {
		String sqlSCT = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE studentCoursesTaken " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlSCT);

		stmt.executeUpdate(sqlSCT);
	}

	private static void insertInstructor(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE instructor " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}

	private static void insertICT(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE InstructorCoursesTaught " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}
	
	public String getInstructor(String crn) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT instructor FROM courseInstances WHERE CRN = '" + crn + "';");
		rs.next();
		// return rs.getString("instructor").substring(1,
		// rs.getString("instructor").length() - 1);
		return rs.getString("instructor");
	}

	public String getCodeFromCRN(String crn) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT code FROM courseInstances WHERE CRN = '" + crn + "';");
		rs.next();
		return rs.getString("code");
	}

	public String courseExists(String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT * FROM courseInstances WHERE code = '" + code + "';");
		rs.next();
		return rs.getString("code");
	}
	
	public String getSemester(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT semester FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("semester");
		return "no semester for CRN " + CRN + " and code " + code;
	}
	
	public String getDays(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT days FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("days");
		return "no days for CRN " + CRN + " and code " + code;
	}
	
	public String getStartTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT startTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("startTime");
		return "no startTime for CRN " + CRN + " and code " + code;
	}
	
	public String getEndTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT endTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("endTime");
		return "no endTime for CRN " + CRN + " and code " + code;
	}
	
	public String studentEnrolled(String crn, String code, String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT banner FROM studentCoursesTaken where banner = '"+ banner +"' and CRN = '" + crn + "' and code = '" + code + "';");
		rs.next();
		return rs.getString("banner");
	}
	
	public String getClassification(String banner, String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT classification FROM studentCoursesTaken where banner = '" + banner + "' and CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("classification");
		return "no classification for given data";
	}

	private static String[] newSplit(String str) {
		// guaranteed to have 147 columns; any more are a mistake and/or not
		// meaningful
		String newStrings[] = new String[135]; // array of all fields to be
												// filled and returned
		int i = 0; // counter for number of fields inserted into the array; used
					// for counting and indexing into newStrings
		while (i < 135) {
			String temp; // will be filled and inserted into the ith index of
							// newStrings
			/*
			 * as seen near the end of this loop, we are removing from str (the
			 * String argument) whatever we insert into newStrings (the String
			 * array to be returned). str should always have a comma until the
			 * last field is reached. the else is necessary because an error is
			 * thrown creating a substring from 0 to -1, which happened here if
			 * the index of a comma could not be found
			 */
			if (str.contains(","))
				temp = str.substring(0, str.indexOf(',', 0));
			else
				temp = str;
			// if temp contains a quote, it may be the name of the instructor.
			// This field is surrounded by double quotes and, more importantly,
			// has a comma between the first and last name
			if (temp.contains("\"")) {
				// to find out if the field is the instructor, we search for a
				// comma. It is the instructor if a comma is found before
				// another quote
				int j = 1;
				while (true) {
					if (str.charAt(j) == '\"') {
						// since a quote is found before a comma, this must not
						// be the instructor field, so we do not have to worry
						// about splitting the data. break to resume reading
						// fields.
						break;
					} else if (str.charAt(j) == ',') {
						/*
						 * since a comma is found before a quote, this must be
						 * the instructor field. However, since we parse on
						 * commas, temp only contains the last name of the
						 * instructor and a following space (" "). This code
						 * gets the last name of the professor and the ending
						 * quote of the field and saves it to temp.
						 */
						String another = str.substring(str.indexOf(','), str.length());
						temp = str.substring(0, str.indexOf(',')) + another.substring(0, another.indexOf("\"") + 1);
						break;
					}
					j++;
				}
			}

			// removes the new insert plus the comma which succeeds it from str
			// so that we can find the first instance of a comma in str in the
			// next iteration easily
			if (!temp.equals(str))
				str = str.substring(temp.length() + 1, str.length());

			// adds temp into newStrings and increments i
			/*
			 * System.out.println("old temp: " + temp); if (temp.charAt(0) ==
			 * '\"' && temp.charAt(temp.length() - 1) == '\"' && temp.contains("
			 * ,")) temp = temp.replaceAll("\"", "");
			 * System.out.println("new temp: " + temp);
			 */
			newStrings[i++] = temp;
		}
		return newStrings;
	}

	private int convertClassification(String c) {
		if (c.equals("FR"))
			return 1;
		else if (c.equals("SO"))
			return 2;
		else if (c.equals("JR"))
			return 3;
		else if (c.equals("SR"))
			return 4;
		else if (c.equals("GR"))
			return 5;
		return 0;
	}
}