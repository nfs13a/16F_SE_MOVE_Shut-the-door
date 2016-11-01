package implementation;

/**
 * Lukkedoerendunandurraskewdylooshoofermoyportertooryzooysphalnabortansporthaokansakroidverjkapakkapuk
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StudentCourseManager {

	// stores the local path to the CSV from the absolute path
	private String csvPath;
	// gets the absolute path to properly access files on any system
	private static String lol = new File("").getAbsolutePath();

	// for building statements
	private static Connection conn = null;
	// for executing sql
	private static Statement stmt = null;

	// ___________ kept it singular out of fear of great shame
	// /
	// v
	// keeps track of which primary key has been entered for each of the 5
	// tables
	private static Vector<String> allBanners; // student
	private static Vector<String> allCIs; // courseInstances
	private static Vector<String> allSCTs; // studentCoursesTaken
	private static Vector<String> allIs; // instructor
	private static Vector<String> allICTs; // instructorCoursesTaught

	public StudentCourseManager(String CSV) { // argument must include the
												// ".csv" extension if it is a
												// filename
		if (CSV.equals("big")) // use the csv given by Dr. Reeves
			csvPath = lol + "/implementation/cs374_anon.csv";
		else // use the test csv filename entered
			csvPath = lol + "/implementation/" + CSV;

		// initialize all primary key vectors
		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
	}
	
	private static void connectToDatabase() throws SQLException {
		final String DB_URL = "jdbc:mysql://localhost/";

		// Database credentials
		final String USER = "root";
		final String PASS = ""; // password is set to "NO", as in no password,
								// on test systems.
								// should add input to final tool to prompt user
								// for password

		try {
			Class.forName("com.mysql.jdbc.Driver");
			//creates Connection object from database path (?) and credentials
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to get mysql driver: " + e);
		} catch (SQLException e) {
			System.err.println("Unable to connect to server: " + e);
		}
		//initializes Statement class object
		stmt = conn.createStatement();
	}

	// used when the "big" csv has already been parsed and inserted
	// will only execute the update of "use courses"
	public StudentCourseManager() throws SQLException {
		//established connection
		connectToDatabase();
		stmt.executeUpdate("USE COURSES;");

		// initialize all primary key vectors
		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
	}

	public void parseCRN() {
		try {
			// sets up parsing data
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			try {
				br = new BufferedReader(new FileReader(csvPath));
				
				//create a new CSV per table in the database
				FileWriter studentCSV = new FileWriter(lol + "/studentTable.csv");
				FileWriter ciCSV = new FileWriter(lol + "/ciTable.csv");
				FileWriter sctCSV = new FileWriter(lol + "/sctTable.csv");
				FileWriter instructorCSV = new FileWriter(lol + "/instructorTable.csv");
				FileWriter ictCSV = new FileWriter(lol + "/ictTable.csv");

				/*
				 * Fields we care about:
				 * 
				 * Column 													| Number in stuff array 
				 * CRN: 5 digit number 										| 35
				 * Subject_Code: * 2-4 string all caps 						| 40 
				 * Course_Number: 100-599? 									| 42 
				 * Instructor_Name: literally "Last, First" 				| 50
				 * Grade_Code: only A-D, F, W, and maybe WF 				| 55 
				 * Banner_ID: 9 digit number but usually 3-4 leading 0's 	| 56 
				 * First_Name 												| 57
				 * Last_Name 												| 58 
				 * Middle_Name 												| 59 
				 * Prefix 													| 60 
				 * Class_Code: FR, SO, JR, SR, GR, SU 						| 33 
				 * Billing_Hours: hours the course is worth 				| 47 
				 * Credit_Hours: 0 if not passed, Billing_Hours if is 		| 48
				 */
				
				createDatabases(lol + "/implementation/CoursesSetup.sql");
				while ((line = br.readLine()) != null) {
					String[] stuff = StudentCourseManager.newSplit(line);
					stmt = conn.createStatement();

					// only parse the line if none of the components of the tables' primary key (<- singular, ugh) is/are empty
					if (!(stuff[40].isEmpty() || stuff[42].isEmpty() || stuff[35].isEmpty() || stuff[56].isEmpty()
							|| stuff[50].isEmpty())) {
						stuff[50] = stuff[50].substring(1, stuff[50].length() - 1);

						//write entry to students if new
						if (!allBanners.contains(stuff[56])) { // student only represented by a banner
							String writeStudent = "";

							writeStudent += stuff[56] + "\n";

							//add primary key to Vector
							allBanners.add(stuff[56]);
							//add new line to appropriate csv
							studentCSV.append(writeStudent);
						}
						//write entry to courseInstances if new
						if (!allCIs.contains(stuff[35] + stuff[40] + stuff[42])) {
							String writeCI = "";
							// CRN, code alpha (e.g. CS), code numeric (e.g. 120)
							writeCI += stuff[35] + "," + stuff[40] + stuff[42] + ",";

							// 5 day columns, M|T|W|R|F
							for (int j = 72; j < 77; j++)
								writeCI += stuff[j];

							// start time, end time, semester, building, room, max students
							writeCI += "," + stuff[66] + "," + stuff[67] + "," + stuff[1] + "," + stuff[68] + ","
									+ stuff[70] + "," + stuff[113 + 26] + "\n";
							
							//add primary key to Vector
							allCIs.add(stuff[35] + stuff[40] + stuff[42]); // CRN + code
							//add new line to appropriate csv
							ciCSV.append(writeCI);
						}
						//write entry to studentCoursesTaken if new
						if (!allSCTs.contains(stuff[35] + stuff[40] + stuff[42] + stuff[56])) {
							String writeSCT = "";
							writeSCT += stuff[56] + "," + stuff[35] + "," + stuff[40] + stuff[42] + "," + stuff[33]
									+ "\n";

							//add primary key to Vector
							allSCTs.add(stuff[35] + stuff[40] + stuff[42] + stuff[56]);
							//add new line to appropriate csv
							sctCSV.append(writeSCT);
						}
						//write entry to instructor if new
						if (!allIs.contains(stuff[50])) {
							String writeI = "";
							// writeI += stuff[50] + "\n";
							if (stuff[50].contains(",")) {
								writeI += stuff[50].substring(stuff[50].indexOf(",") + 2) + " "
										+ stuff[50].substring(0, stuff[50].indexOf(",")) + "\n";
							} else {
								writeI += stuff[50] + "\n";
							}
							
							//add primary key to Vector
							allIs.add(stuff[50]);
							//add new line to appropriate csv
							instructorCSV.append(writeI);
						}
						//write entry to instructorCoursesTaught if new
						if (!allICTs.contains(stuff[50] + stuff[35] + stuff[40] + stuff[42])) {
							String writeICT = "";

							if (stuff[50].contains(",")) {
								writeICT += stuff[50].substring(stuff[50].indexOf(",") + 2) + " "
										+ stuff[50].substring(0, stuff[50].indexOf(",")) + "," + stuff[35] + ","
										+ stuff[40] + stuff[42] + "\n";
							} else {
								writeICT += stuff[50] + "," + stuff[35] + "," + stuff[40] + stuff[42] + "\n";
							}
							
							//add primary key to Vector
							allIs.add(stuff[50] + stuff[35] + stuff[40] + stuff[42]);
							//add new line to appropriate csv
							ictCSV.append(writeICT);
						}
					}
				}
				
				//close the filewriters that write to the table csv's
				studentCSV.close();
				ciCSV.close();
				sctCSV.close();
				instructorCSV.close();
				ictCSV.close();

				//unique methods calls that load data from table csv's into tables
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
		//established connection
		connectToDatabase();
		
		//create a ScriptRunner and uses it to create a new database
		ScriptRunner runner = new ScriptRunner(conn, false, false);
		runner.runScript(new BufferedReader(new FileReader(file)));
	}

	private static void insertStudent(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn + "' INTO TABLE student "
				+ "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
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

	//if students table contains an entry with a banner
	public boolean studentExists(String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM student WHERE banner = '" + banner + "';");
		rs.next();
		// System.out.println("num: " + rs.getInt("total"));
		return rs.getInt("total") == 1;
	}

	//returns the instructor who teaches crn and code
	public String getInstructor(String crn, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT name FROM instructorCoursesTaught WHERE CRN = '" + crn + "' and code = '" + code + "';");
		rs.next();
		return rs.getString("name");
	}

	//returns all CRN's taught by instructorName in semester
	public String getCoursesTaughtByInstructorDuringSemester(String instructorName, String semester) throws SQLException {
		String result = "";
		ResultSet rs = stmt.executeQuery(
				"select ci.CRN from instructor t inner join instructorcoursestaught ict on (t.name = ict.name) inner join courseInstances ci on (ict.CRN = ci.CRN) where t.name = '"
						+ instructorName + "' and ci.semester = '" + semester + "';");
		rs.next();
		result += rs.getString("CRN");
		while (rs.next()) {
			result += "," + rs.getString("CRN");
		}
		return result;
	}

	//returns the instructor that teaches a course with crn and code during a semester
	//CRN and code are unique per semester for our data.
	//this method is necessary to ensure that semesters are correct for CRN+code+instructor tuples
	public String getInstructorForClassDuringSemester(String crn, String code, String semester) throws SQLException {
		String result = "";
		ResultSet rs = stmt.executeQuery(
				"select ict.name, ci.CRN, ict.code, ci.semester from instructorcoursestaught ict inner join courseInstances ci on (ict.CRN = ci.CRN) where ict.CRN = '"
						+ crn + "' and ict.code = '" + code + "' and ci.semester = '" + semester + "';");
		rs.next();
		result = rs.getString("name");
		// return rs.getString("instructor").substring(1,
		// rs.getString("instructor").length() - 1);
		return result;
	}

	//ensure a course CRN+code exists
	public boolean codeExistsForCRN(String crn, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM courseInstances WHERE CRN = '" + crn + "' AND code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}

	//ensure a course code is offered
	public boolean courseExists(String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM courseInstances WHERE code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}

	//get the semester of a course CRN+code
	public String getSemester(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT semester FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("semester");
		return "no semester for CRN " + CRN + " and code " + code;
	}

	//get the days a course CRN+code is taught
	public String getDays(String CRN, String code) throws SQLException {
		ResultSet rs = stmt
				.executeQuery("SELECT days FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("days");
		return "no days for CRN " + CRN + " and code " + code;
	}

	//get the time course CRN+code starts
	public String getStartTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT startTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("startTime");
		return "no startTime for CRN " + CRN + " and code " + code;
	}

	//get the time course CRN+code ends
	public String getEndTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT endTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("endTime");
		return "no endTime for CRN " + CRN + " and code " + code;
	}

	//get the building course CRN+code is taught in
	public String getBuilding(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"select building from courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next()) {
			String temp = rs.getString("building");
			rs.close();
			return temp;
		}
		rs.close();
		return "no building for CRN " + CRN + " and code " + code;
	}

	//get the room course CRN+code is taught in
	public String getRoom(String CRN, String code) throws SQLException {
		ResultSet rs = stmt
				.executeQuery("select room from courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("room");
		return "no room for CRN " + CRN + " and code " + code;
	}

	//get the max number of students course CRN+code allows to enroll
	public int getMaxStudents(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"select maxStudents from courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next()) {
			int temp = rs.getInt("maxStudents");
			rs.close();
			return temp;
		}
		rs.close();
		System.out.println("no max students data for CRN " + CRN + " and code " + code);
		return -1;
	}

	//ensure student is enrolled in course CRN+code
	public boolean studentEnrolled(String crn, String code, String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM studentCoursesTaken where banner = '" + banner
				+ "' and CRN = '" + crn + "' and code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}

	public String getClassification(String banner, String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT classification FROM studentCoursesTaken where banner = '" + banner
				+ "' and CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("classification");
		return "no classification for given data";
	}

	public String getClassification(String banner, String semester) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT s.banner, s.CRN, s.code, s.classification, c.semester FROM studentCoursesTaken AS s INNER JOIN courseInstances AS c ON s.banner = '"
						+ banner + "' AND s.CRN = c.CRN AND s.code = c.code AND c.semester = '" + semester + "';");
		if (rs.next())
			return rs.getString("classification");
		return "no classification for given data";
	}

	public boolean instructorExists(String name) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM instructor WHERE name = '" + name + "';");
		rs.next();
		// System.out.println("num: " + rs.getInt("total"));
		return rs.getInt("total") == 1;
	}

	public boolean studentIsFree(String banner, String semester, String days, String start, String end)
			throws SQLException {
		String query = "select ci.startTime, ci.endTime from studentCoursesTaken sct inner join courseInstances ci on sct.CRN = ci.CRN and sct.code = ci.code and sct.banner = '"
				+ banner + "' and ci.semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "ci.days LIKE '%" + c + "%' OR ";
		}

		query = query.substring(0, query.lastIndexOf(" OR"));

		query += ") order by ci.startTime;";

		// System.out.println("days query: " + query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sT = Integer.parseInt(rs.getString("ci.startTime")); // start
																		// time
			int eT = Integer.parseInt(rs.getString("ci.endTime")); // end time
			int asT = Integer.parseInt(start); // argument start time
			int aeT = Integer.parseInt(end); // argument end time
			// System.out.println(sT + " and " + eT + " vs " + asT + " and " +
			// aeT);
			if (!(aeT < sT || asT > eT)) {
				return false;
			}
			// return true;
		}
		// System.out.println("No courses for banner "+banner+" in semester
		// "+semester);return true;
		return true;
	}

	public boolean instructorIsFree(String name, String semester, String days, String start, String end)
			throws SQLException {
		String query = "select ci.startTime, ci.endTime from instructorCoursesTaught Ict inner join courseInstances ci on ict.CRN = ci.CRN and ict.code = ci.code and ict.name = '"
				+ name + "' and ci.semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "ci.days LIKE '%" + c + "%' OR ";
		}

		query = query.substring(0, query.lastIndexOf(" OR"));

		query += ") order by ci.startTime;";

		// System.out.println("days query: " + query);

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sT = Integer.parseInt(rs.getString("ci.startTime")); // start
																		// time
			int eT = Integer.parseInt(rs.getString("ci.endTime")); // end
																	// time
			int asT = Integer.parseInt(start); // argument start time
			int aeT = Integer.parseInt(end); // argument end time
			if (!(aeT < sT || asT > eT))
				return false;
		}
		// System.out.println("No courses for name " + name + " in semester " +
		// semester);
		return true;
	}

	/*
	 * public int getMaxSeats(String CRN, String code) throws SQLException {
	 * ResultSet rs =
	 * stmt.executeQuery("SELECT maxStudents FROM courseInstances WHERE " +
	 * "building = (SELECT building FROM courseInstances WHERE CRN = '" + CRN +
	 * "' and code = '" + code + "')" +
	 * " AND room = (SELECT room FROM courseInstances WHERE CRN = '" + CRN +
	 * "' and code = '" + code + "') " +
	 * "AND semester <= (SELECT semester FROM courseInstances WHERE CRN = '" +
	 * CRN + "' and code = '" + code + "') " + "order by maxStudents desc;"); if
	 * (rs.next()) return rs.getInt("maxStudents");
	 * System.out.println("no valid data for CRN " + CRN + " and code " + code);
	 * return -1; }
	 */

	public int getMaxSeats(String building, String room) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT maxStudents FROM courseInstances WHERE " + "building = '" + building
				+ "' AND room = '" + room + "' order by maxStudents desc;");
		if (rs.next()) {
			int temp = rs.getInt("maxStudents");
			rs.close();
			return temp;
		}
		rs.close();
		System.out.println("no valid data for building " + building + " and room " + room);
		return -1;
	}
	
	public String getAllCandidateRooms(String CRN, String code) throws SQLException {
		//for 11041 CS120 the equivalent query would be:
		//SELECT distinct(room) as dRoom FROM courseInstances WHERE building = 'MBB' AND maxStudents >= 30 ORDER BY dRoom;
		
		String allRooms = "";
		String building = getBuilding(CRN, code);
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT distinct(room) AS dRoom FROM courseInstances WHERE"
				+ " building = '" + building + "' order by dRoom;");
		while (rs.next()) {
			String nextRoom = rs.getString("dRoom");
			if (getMaxSeats(building, nextRoom) >= getMaxStudents(CRN, code)) {
				allRooms += nextRoom + ",";
			}
		}
		//System.out.println("no valid data for building " + building + " and room " + room);
		return allRooms;
		
		
		//select distinct(room) as dRoom from courseInstances where building = (select building from courseInstances where CRN = '11041' and code = 'CS120') order by dRoom;
	}
	
	public boolean roomIsFree(String building, String room, String semester, String days, String start, String end) throws SQLException {
		String query = "SELECT CRN, code, startTime, endTime, days FROM courseInstances WHERE building = '" + building + "' AND room = '" + room + "' and semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "days LIKE '%" + c + "%' OR ";
		}
		query = query.substring(0, query.lastIndexOf(" OR ")) + ") order by startTime;";

		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			int sT = Integer.parseInt(rs.getString("startTime")); // start time
			int eT = Integer.parseInt(rs.getString("endTime")); // end time
			int asT = Integer.parseInt(start); // argument start time
			int aeT = Integer.parseInt(end); // argument end time
			if (!(aeT < sT || asT > eT))
				return false;
		}
		return true;
	}
	
	public String getAllOpenTimes(String building, String room, String semester, String days, int timeLength) throws SQLException {
		String allTimes = "";
		Map<String, String> badTimes = new LinkedHashMap<String, String>();
		
		String query = "SELECT CRN, code, startTime, endTime, days FROM courseInstances WHERE building = '" + building + "' AND room = '" + room + "' and semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "days LIKE '%" + c + "%' OR ";
		}
		query = query.substring(0, query.lastIndexOf(" OR ")) + ") order by startTime;";
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery(query);
		while (rs.next()) {
			badTimes.put(getStartTime(rs.getString("CRN"), rs.getString("code")), getEndTime(rs.getString("CRN"), rs.getString("code")));
		}
		
		//only dealing with 50 and 80 minute classes right now		
		DateFormat df = new SimpleDateFormat("HHmm");
		Date dateobj1 = new Date(0, 0, 0, 8, 0);
		Date dateobj2;
		if (timeLength == 50) {
			dateobj2 = new Date(0, 0, 0, 8, 50);
		} else if (timeLength == 90) {
			dateobj2 = new Date(0, 0, 0, 9, 20);
		} else {
			return "Invalid length of class session given current constraints";
		}
		
		
		//System.out.println(df.format(dateobj1));
		while (dateobj1.getHours() < 17 && dateobj2.getHours() <= 17) {
			boolean conflict = false;
			String timeS = df.format(dateobj1);
			String timeE = df.format(dateobj2);
			
			Iterator it = badTimes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				//System.out.println(pair.getKey() + " = " + pair.getValue());
				int sT = Integer.parseInt((String) pair.getKey()); // start time
				int eT = Integer.parseInt((String) pair.getValue()); // end time
				int asT = Integer.parseInt(timeS); // argument start time
				int aeT = Integer.parseInt(timeE); // argument end time
				
				//System.out.println("course: " + sT + "-" + eT);
				//System.out.println("sample: " + asT + "-" + aeT);
				
				conflict = conflict || !(aeT < sT || asT > eT);
				//it.remove(); // avoids a ConcurrentModificationException
			}
			
			if (!conflict) {
				allTimes += timeS + "-" + timeE + ",";
			}
			
			//System.out.println(df.format(dateobj1));
			if (timeLength == 50) {
				dateobj1.setHours(dateobj1.getHours() + 1);
				dateobj2.setHours(dateobj2.getHours() + 1);
			} else {
				dateobj1.setMinutes(dateobj1.getMinutes() + 30);
				dateobj1.setHours(dateobj1.getHours() + 1);
				dateobj2.setMinutes(dateobj2.getMinutes() + 30);
				dateobj2.setHours(dateobj2.getHours() + 1);
			}
		}
		/*while (t.getHours() < 17) {
			System.out.println(t.getTime());
			if (timeLength == 50) {
				t.setHours(t.getHours() + 1);
			} else {
				t.setMinutes(t.getMinutes() + 30);
				t.setHours(t.getHours() + 1);
			}
		}*/
		
		/*boolean conflict = false;
		Iterator it = badTimes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}*/
		return allTimes;
		//select CRN, code, startTime, endTime, days from courseInstances where building = 'MBB' and room = '314' and semester = '201510' and (days LIKE '%M%' or days LIKE '%W%' or days LIKE '%F%') order by startTime;
	}

	public String studentsCanAttend(String CRN, String code, String semester, String days, String start, String end) throws SQLException {
		String allStudents = "";
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT banner FROM studentCoursesTaken WHERE CRN = '" + CRN
				+ "' AND code = '" + code + "';");
		while (rs.next()) {
			String temp = rs.getString("banner");
			allStudents += (studentIsFree(temp, semester, days, start, end) ? temp + "," : "");
		}
		rs.close();
		
		return allStudents;
	}
	
	public String studentsCannotAttend(String CRN, String code, String semester, String days, String start, String end) throws SQLException {
		String allStudents = "";
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT banner FROM studentCoursesTaken WHERE CRN = '" + CRN
				+ "' AND code = '" + code + "';");
		while (rs.next()) {
			String temp = rs.getString("banner");
			allStudents += (studentIsFree(temp, semester, days, start, end) ? "" : temp + ",");
		}
		rs.close();
		
		return allStudents;
	}
	
	private static String[] newSplit(String str) {
		// guaranteed to have 147 columns; any more are a mistake and/or not
		// meaningful
		String newStrings[] = new String[140]; // array of all fields to be
												// filled and returned
		int i = 0; // counter for number of fields inserted into the array; used
					// for counting and indexing into newStrings
		while (i < 140) {
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