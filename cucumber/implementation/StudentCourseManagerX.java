package implementation;

/**
 * Lukkedoerendunandurraskewdylooshoofermoyportertooryzooysphalnabortansporthaokansakroidverjkapakkapuk
 * All Complexity calculations are cyclomatic calculations based on converting the method into a graph with E edges and N nodes.
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
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StudentCourseManagerX {

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
	
	PQsort pqs;// = new PQsort();	used to compare priorities of AlternateSessionXs to be pushed into pq
	PriorityQueue<AlternateSessionX> pq;// = new PriorityQueue<AlternateSessionX>(10, pqs);	holds all alternate sessions for a given course CRN+code;	number of students in each classification (descending, as in GR is most important) is weight
	AlternateSessionX mostStudentsAlternate;// = new AlternateSessionX("", "", "", "", "", "", "", "", "", "", new int[5], new int[5], -1, -1);	//holds the alternate session for a given course CRN+code that has the most students that can attend

	private static String password;
	
	/*PCW14a :)
	-----COMPLEXITY :D-----
	E= 6
	N= 6
	P= 1
	Complexity = 2 
	*/
	
	public StudentCourseManagerX(String CSV) { // argument must include the ".csv" extension if it is a filename
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
		
		pqs = new PQsort();
		pq = new PriorityQueue<AlternateSessionX>(10, pqs);
		mostStudentsAlternate = new AlternateSessionX("", "no available alternates", "", "", "", "", "", "", "", "", new int[5], new int[5], -1, -1);
		
		password = "";
	}
	
	/*PCW14a :)
	-----COMPLEXITY :D-----
	E = 1
	N = 2
	P = 1
	Complexity = 1
	*/

	// used when the "big" csv has already been parsed and inserted
	// will only execute the update of "use courses"
	public StudentCourseManagerX() throws SQLException {
		//established connection
		connectToDatabase();
		stmt.executeUpdate("USE COURSES;");

		// initialize all primary key vectors
		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
		
		pqs = new PQsort();
		pq = new PriorityQueue<AlternateSessionX>(10, pqs);
		mostStudentsAlternate = new AlternateSessionX("", "no available alternates", "", "", "", "", "", "", "", "", new int[5], new int[5], -1, -1);
		
		password = "";
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E = 16 
		N = 13
		P = 1
		Complexity = 5 
	*/

	// used with full tool
	// acts as no-args or 1 String arg depending on state of needToSetUp
	// could not use other constructors because a call to a constructor
	// must be the first statement in a java constructor
	public StudentCourseManagerX(String pass, String CSV) throws SQLException {
		//save MySQL password
		if (pass.equals("NONE")) {
			password = "";
		} else {
			password = pass;
		}
		
		// initialize all primary key vectors
		allBanners = new Vector<String>();
		allCIs = new Vector<String>();
		allSCTs = new Vector<String>();
		allIs = new Vector<String>();
		allICTs = new Vector<String>();
		
		//no if statement here because will check if inserts need to happen automatically instead of by asking user
		//if tool not run before:
		if (CSV.equals("1")) {
			csvPath = lol + "/implementation/cs374_anon.csv";
		} else if (CSV.equals("2")) {
			csvPath = lol + "/implementation/cs374_f16_anon.csv";
		}
		
		connectToDatabase();
		try {
			stmt.executeUpdate("USE COURSES;");
		} catch (SQLException se) {
			System.out.println(se.getMessage() + "here");
			if (se.getMessage().equals("Unknown database 'courses'")) {
				parseCRN();
			} else {
				System.out.println("Unhandled error: " + se.getMessage());
				System.exit(0);
			}
		}
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT COUNT(*) AS total FROM courseInstances;");
		if (rs.next()) {
			int totalCurrentCourses = rs.getInt("total");
			if (totalCurrentCourses == 1285 && CSV.equals("1")) {
				parseCRN();
			} else if (totalCurrentCourses == 8016 && CSV.equals("2")) {
				parseCRN();
			}
		}
		
		pqs = new PQsort();
		pq = new PriorityQueue<AlternateSessionX>(10, pqs);
		mostStudentsAlternate = new AlternateSessionX("", "no available alternates", "", "", "", "", "", "", "", "", new int[5], new int[5], -1, -1);
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1 
	*/
	
	//establish database connection
	private static void connectToDatabase() throws SQLException {
		//Reeves' port for MySQL is 3307 instead of 3306 (default)
		//"localhost/" becomes "localhost:####/"
		final String DB_URL = "jdbc:mysql://localhost/";

		// Database credentials
		final String USER = "root";
		//final String PASS = "";	now passed in 2 args constructor

		try {
			Class.forName("com.mysql.jdbc.Driver");
			//creates Connection object from database path (?) and credentials
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, password);
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to get mysql driver: " + e);
		} catch (SQLException e) {
			System.err.println("Unable to connect to server: " + e);
		}
		//initializes Statement class object
		stmt = conn.createStatement();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 14
		N= 9
		P= 1
		Complexity = 7  
	*/
	
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
				 * Course_Number: 100-799? 									| 42 
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
					String[] stuff = StudentCourseManagerX.newSplit(line);
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
				
				insertKnownRooms();

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
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 0
		N= 1
		P= 1
		Complexity = 1 
	*/
	
	private static void createDatabases(String file) throws FileNotFoundException, IOException, SQLException {
		//established connection
		connectToDatabase();
		
		//create a ScriptRunner and uses it to create a new database
		ScriptRunner runner = new ScriptRunner(conn, false, false);
		runner.runScript(new BufferedReader(new FileReader(file)));
	}
	
	private static void insertKnownRooms() throws SQLException {
		//MBB floors 1-3
		/*for (int i = 1; i <= 3; i++) {
			String sqlRoomData = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/MBBfloor1.csv' INTO TABLE student "
					+ "FIELDS TERMINATED BY ','" + " LINES TERMINATED BY '\n';";
			System.out.println(sqlRoomData);
			stmt.executeUpdate(sqlRoomData);
		}*/
		
		for (int i = 1; i <= 3; i++) {
			try {
				// sets up parsing data
				BufferedReader br = null;
				String line = "";
				String cvsSplitBy = ",";

				try {
					br = new BufferedReader(new FileReader(lol + "/MBBfloor" + i + ".csv"));
					while ((line = br.readLine()) != null) {
						String[] stuff = line.split(",");
						stmt = conn.createStatement();
						stmt.execute("INSERT INTO knownroom VALUES ('" + stuff[0] + "','" + stuff[1] + "'," + stuff[2]
								+ ");");
					}
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
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	private static void insertStudent(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn + "' INTO TABLE student "
				+ "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1 
	*/
	private static void insertCourseInstance(String csvIn) throws SQLException {
		String sqlCourseInstance = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE courseInstances " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlCourseInstance);

		stmt.executeUpdate(sqlCourseInstance);
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	private static void insertStudentCourseTaken(String csvIn) throws SQLException {
		String sqlSCT = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE studentCoursesTaken " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlSCT);

		stmt.executeUpdate(sqlSCT);
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	private static void insertInstructor(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE instructor " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	private static void insertICT(String csvIn) throws SQLException {
		String sqlStudent = "LOAD DATA LOCAL INFILE '" + lol.replace("\\", "/") + "/" + csvIn
				+ "' INTO TABLE InstructorCoursesTaught " + "FIELDS TERMINATED BY ','" + "LINES TERMINATED BY '\n';";

		System.out.println(sqlStudent);

		stmt.executeUpdate(sqlStudent);
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//if students table contains an entry with a banner
	public boolean studentExists(String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM student WHERE banner = '" + banner + "';");
		rs.next();
		return rs.getInt("total") == 1;
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//returns the instructor who teaches crn and code
	public String getInstructor(String crn, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT name FROM instructorCoursesTaught WHERE CRN = '" + crn + "' and code = '" + code + "';");
		rs.next();
		return rs.getString("name");
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
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
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
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
		// return rs.getString("instructor").substring(1, rs.getString("instructor").length() - 1);
		return result;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//ensure a course CRN+code exists
	public boolean codeExistsForCRN(String crn, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM courseInstances WHERE CRN = '" + crn + "' AND code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//ensure a course code is offered
	public boolean courseExists(String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM courseInstances WHERE code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the semester of a course CRN+code
	public String getSemester(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT semester FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("semester");
		return "no semester for CRN " + CRN + " and code " + code;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the days a course CRN+code is taught
	public String getDays(String CRN, String code) throws SQLException {
		ResultSet rs = stmt
				.executeQuery("SELECT days FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("days");
		return "no days for CRN " + CRN + " and code " + code;
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the time course CRN+code starts
	public String getStartTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT startTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("startTime");
		return "no startTime for CRN " + CRN + " and code " + code;
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the time course CRN+code ends
	public String getEndTime(String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT endTime FROM courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("endTime");
		return "no endTime for CRN " + CRN + " and code " + code;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the building course CRN+code is taught in
	public String getBuilding(String CRN) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"select building from courseInstances where CRN = '" + CRN + "';");
		if (rs.next()) {
			String temp = rs.getString("building");
			rs.close();
			return temp;
		}
		rs.close();
		return "no building for CRN " + CRN;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the room course CRN+code is taught in
	public String getRoom(String CRN, String code) throws SQLException {
		ResultSet rs = stmt
				.executeQuery("select room from courseInstances where CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("room");
		return "no room for CRN " + CRN + " and code " + code;
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//get the max number of students course CRN+code allows to enroll
	public int getMaxStudents(String CRN) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"select maxStudents from courseInstances where CRN = '" + CRN + "';");
		if (rs.next()) {
			int temp = rs.getInt("maxStudents");
			rs.close();
			return temp;
		}
		rs.close();
		System.out.println("no max students data for CRN " + CRN);
		return -1;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//ensure student is enrolled in course CRN+code
	public boolean studentEnrolled(String crn, String code, String banner) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM studentCoursesTaken where banner = '" + banner
				+ "' and CRN = '" + crn + "' and code = '" + code + "';");
		rs.next();
		return rs.getInt("total") > 0;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//gets classification (FR, SO, JR, SR, GR, SU) of student when taking a course CRN+code, which exists exclusively in a unique semester
	public String getClassification(String banner, String CRN, String code) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT classification FROM studentCoursesTaken where banner = '" + banner
				+ "' and CRN = '" + CRN + "' and code = '" + code + "';");
		//System.out.println("SELECT classification FROM studentCoursesTaken where banner = '" + banner + "' and CRN = '" + CRN + "' and code = '" + code + "';");
		if (rs.next())
			return rs.getString("classification");
		return "no classification for given data";
	}

	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//gets classification (FR, SO, JR, SR, GR, SU) of student during a semester
	public String getClassification(String banner, String semester) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				"SELECT s.banner, s.CRN, s.code, s.classification, c.semester FROM studentCoursesTaken AS s INNER JOIN courseInstances AS c ON s.banner = '"
						+ banner + "' AND s.CRN = c.CRN AND s.code = c.code AND c.semester = '" + semester + "';");
		
		//System.out.println("SELECT s.banner, s.CRN, s.code, s.classification, c.semester FROM studentCoursesTaken AS s INNER JOIN courseInstances AS c ON s.banner = '" + banner + "' AND s.CRN = c.CRN AND s.code = c.code AND c.semester = '" + semester + "';");
		
		if (rs.next())
			return rs.getString("classification");
		return "no classification for given data";
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//ensure an instructor exists
	public boolean instructorExists(String name) throws SQLException {
		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM instructor WHERE name = '" + name + "';");
		rs.next();
		return rs.getInt("total") == 1;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 14
		N= 10
		P= 1
		Complexity = 6
	*/
	//checks if a student is free in a semester on some days (any permutation of 'MTWRF') during a range of time
	public boolean studentIsFree(String banner, String semester, String days, String start, String end)
			throws SQLException {
		
		//System.out.println("data: \nbanner: " + banner + "\nsemester: " + semester + "\ndays: " + days + "\nstart: " + start + "\nend: " + end);
		
		String query = "select ci.startTime, ci.endTime from studentCoursesTaken sct inner join courseInstances ci on sct.CRN = ci.CRN and sct.code = ci.code and sct.banner = '"
				+ banner + "' and ci.semester = '" + semester + "' and (";
		
		//days LIKE '%*day-char*%' searches for any entry that contains *day-char* in the days column 
		for (char c : days.toCharArray()) {
			query += "ci.days LIKE '%" + c + "%' OR ";
		}

		//loop adds an extra OR and a space, so remove it here
		query = query.substring(0, query.lastIndexOf(" OR"));

		//order by startTime to make comparisons easy to follow
		query += ") order by ci.startTime;";

		//System.out.println(query);
		
		ResultSet rs = stmt.executeQuery(query);
		//loop through all times that the students has classes during the semester
		while (rs.next()) {
			
			String qStart = rs.getString("ci.startTime");
			String qEnd = rs.getString("ci.endTime");
			
			if (qStart.equals("") || qEnd.equals("")) {
				//System.out.println("Bad times: '" + qStart + "'-'" + qEnd + "'");
				continue;
			}
			
			int sT = Integer.parseInt(qStart); // start time
			int eT = Integer.parseInt(qEnd); // end time
			int asT = Integer.parseInt(start); // argument start time	--	the start time of the block during which we want to know if the student is free
			int aeT = Integer.parseInt(end); // argument end time --	the end time of the block during which we want to know if the student is free
			
			/* 
			 * if the start time of the test (argument) block is after the end time of a class 
			 * or if the start time of the test (argument) block is before the start time of a class, 
			 * then there is not necessarily a conflict
			 * therefore if this is not true, then there must be a conflict, so report it
			 */
			if (!(aeT < sT || asT > eT)) {
				return false;
			}
		}
		// System.out.println("No courses for banner "+banner+" in semester " + semester);
		return true;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :(-----
		E= 10
		N= 8
		P= 1
		Complexity = 4
	*/
	//ensures that an instructor is free in a time period on certain days during a semester
	public boolean instructorIsFree(String name, String semester, String days, String start, String end)
			throws SQLException {
		//works the same as studentIsFree() above, but for an instructor
		String query = "select ci.startTime, ci.endTime from instructorCoursesTaught Ict inner join courseInstances ci on ict.CRN = ci.CRN and ict.code = ci.code and ict.name = '"
				+ name + "' and ci.semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "ci.days LIKE '%" + c + "%' OR ";
		}

		query = query.substring(0, query.lastIndexOf(" OR"));

		query += ") order by ci.startTime;";

		//System.out.println(query);
		
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			String sTstr = rs.getString("ci.startTime"); // start
																		// time
			String eTstr = rs.getString("ci.endTime"); // end
																	// time
			
			if (!sTstr.equals("") && !eTstr.equals("")) {
				int sT = Integer.parseInt(sTstr);
				int eT = Integer.parseInt(eTstr);
			int asT = Integer.parseInt(start); // argument start time
			int aeT = Integer.parseInt(end); // argument end time
			if (!(aeT < sT || asT > eT))
				return false;
			}
		}
		// System.out.println("No courses for name " + name + " in semester " + semester);
		return true;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//gets the max number of seats in a room in a building
	//estimates this data from the largest maxStudents value of a class in that room
	public int getMaxSeats(String building, String room) throws SQLException {
		Statement stmtNew = conn.createStatement();
		ResultSet rsNew = stmtNew.executeQuery("SELECT seats FROM knownroom WHERE building = '" + building + "' AND room = '" + room + "';");
		
		if (rsNew.next()) {
			int seats = rsNew.getInt("seats");
			//System.out.println("Seats of room " + room + ": " + seats);
			return seats;
		} else {

			// gets all maxStudents values from courses in that building and
			// room values are ordered from greatest to least
			ResultSet rs = stmt.executeQuery("SELECT maxStudents FROM courseInstances WHERE " + "building = '"
					+ building + "' AND room = '" + room + "' order by maxStudents desc;");
			// returns the first value if it exists
			// works because of the ordering of the query
			if (rs.next()) {
				int temp = rs.getInt("maxStudents");
				rs.close();
				return temp;
			}
			rs.close();
		}
		rsNew.close();
		System.out.println("no valid data for building " + building + " and room " + room);
		return -1;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 7
		N= 6
		P= 1
		Complexity = 3
	*/
	//retrieves all CRN's for a given code
	//used in command line ui to provide prompts
	public String getAllCRNs(String code) throws SQLException {
		ResultSet rs = stmt
				.executeQuery("SELECT CRN FROM courseInstances WHERE code = '"+ code + "';");
		String all = "";
		while (rs.next()) {
			all += rs.getString("CRN") + ", ";
		}

		if (all.equals(""))
			return "No found CRN's for code '" + code + "'";
		//formatting to please user
		return all.substring(0, all.length() - 2);
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 7
		N= 6
		P= 1
		Complexity = 3
	*/
	//gets all rooms that can fit a course CRN+code
	public String getAllCandidateRooms(String CRN) throws SQLException {
		//for 11041 CS120 the equivalent query would be:
		//SELECT distinct(room) as dRoom FROM courseInstances WHERE building = 'MBB' AND maxStudents >= 30 ORDER BY dRoom;
		
		String allRooms = "";	//will store all the rooms the course can fit in, comma separated
		String building = getBuilding(CRN);	//gets the building of the course
		
		//gets all rooms from the building of the course
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT distinct(room) AS dRoom FROM courseInstances WHERE"
				+ " building = '" + building + "' order by dRoom;");
		while (rs.next()) {	//loop through all rooms
			String nextRoom = rs.getString("dRoom");
			if (getMaxSeats(building, nextRoom) >= getMaxStudents(CRN)) {	//if the max seats of the room is at least the max students of the course, append the room to allRooms
				allRooms += nextRoom + ",";
			}
		}
		//System.out.println("no valid data for building " + building + " and room " + room);
		return allRooms;
		
		
		//select distinct(room) as dRoom from courseInstances where building = (select building from courseInstances where CRN = '11041' and code = 'CS120') order by dRoom;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 10
		N= 9
		P= 1
		Complexity = 3
	*/
	//determines if a room is free during a semester on certain days from one time until another
	public boolean roomIsFree(String building, String room, String semester, String days, String start, String end) throws SQLException {
		//works just like studentIsFree, but for rooms
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
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 46
		N= 33
		P= 1
		Complexity = 15
	*/
	//gets all the times that a room is free during a semester on certain days
	public String getAllOpenTimes(String building, String room, String semester, String days) throws SQLException {	//extra argument int timelength if not just doing MWF and TR classes with normal time constraints
		int timeLength = days.equals("MWF") ? 50 : 80;	//MWF classes are 50 minutes, TR classes are 80 minutes, and all others are not considered currently
		String allTimes = "";	//all times that the room is available; will be returned unless error occurs
		//badTimes is Linked for ordering
		Map<String, String> badTimes = new LinkedHashMap<String, String>();	//start, end of classes in building+room on any of days in semster
		
		//will get all data for courses in building+room on any of the days
		String query = "SELECT CRN, code, startTime, endTime, days FROM courseInstances WHERE building = '" + building + "' AND room = '" + room + "' and semester = '" + semester + "' and (";
		for (char c : days.toCharArray()) {
			query += "days LIKE '%" + c + "%' OR ";
		}
		query = query.substring(0, query.lastIndexOf(" OR ")) + ") order by startTime;";
		
		//second statement needed because of methods that executeQuery 's
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery(query);
		//insert all times into badTimes
		while (rs.next()) {
			badTimes.put(getStartTime(rs.getString("CRN"), rs.getString("code")), getEndTime(rs.getString("CRN"), rs.getString("code")));
		}
		
		//only dealing with 50 and 80 minute classes right now		
		DateFormat df = new SimpleDateFormat("HHmm");
		Date dateobj1 = new Date(0, 0, 0, 8, 0);	//first start time is always at 8
		Date dateobj2;	//for end time
		if (timeLength == 50) {
			dateobj2 = new Date(0, 0, 0, 8, 50);	//end time of 0850 for MWF classes
		} else if (timeLength == 80) {
			dateobj2 = new Date(0, 0, 0, 9, 20);	//end time of 0920 for TR classes
		} else {
			return "Invalid length of class session given current constraints";
		}
		
		
		while (dateobj1.getHours() < 17 && dateobj2.getHours() <= 17) {
			boolean conflict = false;	//whether or not there is a conflict (only known if true inside loop, so must be stored
			String timeS = df.format(dateobj1);	//potential start time accumulator
			String timeE = df.format(dateobj2);	//associated end time accumulator
			
			//rooms cannot be free for a class during chapel, so prevents checking
			int numS = Integer.parseInt(timeS);
			int numE = Integer.parseInt(timeS);
			//if start time is at or after 1100 and before 1200
			//or if end time is at or after 1100 and before 1200
			//or if start time is before 1100 and end time is at or after 1200
			if (numS >= 1100 && numS < 1200 || numE >= 1100 && numE < 1200 || numS < 1100 && numE > 1159) {
				//increment date counter based on time of class and continue
				if (timeLength == 50) {
					dateobj1.setHours(dateobj1.getHours() + 1);
					dateobj2.setHours(dateobj2.getHours() + 1);
				} else {
					dateobj1.setMinutes(dateobj1.getMinutes() + 30);
					dateobj1.setHours(dateobj1.getHours() + 1);
					dateobj2.setMinutes(dateobj2.getMinutes() + 30);
					dateobj2.setHours(dateobj2.getHours() + 1);
				}
				continue;
			}
			
			//iterate through bad times for class
			//if any of them intersect with potential timeslot, conflict = true
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
				
				//if there is not already a conflict
				//there cannot be a conflict only if the potential start time is after the session's end time
				//or if the potential end time is after the session's start time
				conflict = conflict || !(aeT < sT || asT > eT);
				//it.remove(); // avoids a ConcurrentModificationException
			}
			
			//if there is no conflict, add potential time slot start+end to String 
			if (!conflict) {
				allTimes += timeS + "-" + timeE + ",";
			}
			
			//increment date counter based on time of class and continue
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
		return allTimes;
		//select CRN, code, startTime, endTime, days from courseInstances where building = 'MBB' and room = '314' and semester = '201510' and (days LIKE '%M%' or days LIKE '%W%' or days LIKE '%F%') order by startTime;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//gets all students that can attend a course CRN+code during time start+end on days during semester
	public String studentsCanAttend(String CRN, String semester, String days, String start, String end) throws SQLException {
		String allStudents = "";
		
		//get all students in course CRN+code
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT banner FROM studentCoursesTaken WHERE CRN = '" + CRN
				+ "';");
		//loop through all students in course CRN+code
		while (rs.next()) {
			String temp = rs.getString("banner");
			//add student if they are free in specified time
			allStudents += (studentIsFree(temp, semester, days, start, end) ? temp + "," : "");
		}
		rs.close();
		
		return allStudents;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 5
		N= 5
		P= 1
		Complexity = 2
	*/
	//gets all students that canNOT attend a course CRN+code during time start+end on days during semester
	public String studentsCannotAttend(String CRN, String semester, String days, String start, String end) throws SQLException {
		//same as above, looking for cannot attend
		
		String allStudents = "";
		
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT banner FROM studentCoursesTaken WHERE CRN = '" + CRN
				+ "';");
		while (rs.next()) {
			String temp = rs.getString("banner");
			allStudents += (studentIsFree(temp, semester, days, start, end) ? "" : temp + ",");
		}
		rs.close();
		
		return allStudents;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 21
		N= 16
		P= 1
		Complexity = 7
	*/
	//adds all AlternateSessionXs to data member pq for course CRN+code taught by instructor in building+room on days during semester
	private void pushToPQ(String CRN, String code, String semester, String days, String building, String room, String instructor) throws SQLException {
		String times[] = getAllOpenTimes(building, room, semester, days).split(",");	//all "start-end" times that building+room is free on days during semester
		//loop through all times
		for (String t : times) {
			//all "start-end" should be exactly 9 characters long, so continue of not true for t
			if (t.length() != 9) {
				//System.out.println("time: '" + t + "'");
				continue;
			}
			
			//store start and end time as String 's
			String start = t.substring(0, t.indexOf("-"));
			String end = t.substring(t.indexOf("-") + 1);
			//only a valid AlternateSessionX if instructor is free to teach then
			if (instructorIsFree(instructor, semester, days, start, end)) {
				//get all students that can and cannot attend this session
				String bannersCan = studentsCanAttend(CRN, semester, days, start, end);
				String bannersCannot = studentsCannotAttend(CRN, semester, days, start, end);
				int stus[] = {0,0,0,0,0};	//number of GR, SR, JR, SO, FR that can attend
				int totalStusCan = 0;	//number of students that can attend
				//loop through all students that can attend
				for (String bc : bannersCan.split(",")) {
					if (bc.equals("")) {
						//System.out.println("caught empty");
						continue;
					}
					Statement stmt3 = conn.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT classification FROM studentCoursesTaken WHERE banner = '" + bc + "' and CRN = '" + CRN + "';");
					rs.next();
					//System.out.println("SELECT classification FROM studentCoursesTaken WHERE banner = '" + bc + "' and CRN = '" + CRN + "';");
					stus[convertClassification(rs.getString("classification")) - 1]++;
					totalStusCan++;
				}
				
				int stusNo[] = {0,0,0,0,0};	//number of GR, SR, JR, SO, FR that cannot attend
				int totalStusCannot = 0;	//number of students that cannot attend
				//loop through all students that cannot attend
				for (String bct : bannersCannot.split(",")) {
					if (!bct.equals("")) {
						Statement stmt3 = conn.createStatement();
						ResultSet rs = stmt.executeQuery("SELECT classification FROM studentCoursesTaken WHERE banner = '" + bct + "' and CRN = '" + CRN + "';");
						rs.next();
						stusNo[convertClassification(rs.getString("classification")) - 1]++;
						totalStusCannot++;
					}
				}
				
				//create and store an AlternateSessionX with collected data
				AlternateSessionX tempSession = new AlternateSessionX(CRN, code, building, room, start, end, days, semester, bannersCan, bannersCannot, stus, stusNo, totalStusCan, totalStusCannot);
				
				//push a new AlternateSessionX with collected data to pq
				pq.add(tempSession);
				
				if (tempSession.getNumCan() > mostStudentsAlternate.getNumCan()) {
					//AlternateSessionX with the most students that can attend is the new one if its getNumCan is greater than the current max
					mostStudentsAlternate = tempSession;
				}
			}
		}
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 18
		N= 14
		P= 1
		Complexity = 6
	*/
	//set all AlternateSessionXs
	public boolean setAlternates(String CRN) throws SQLException {
		//get data for course CRN+code and the course's instructor
		Statement stmt2 = conn.createStatement();
		ResultSet rs = stmt2.executeQuery("SELECT * FROM courseInstances ci inner join instructorCoursesTaught ict on ci.CRN = '" + CRN + "' and ci.CRN = ict.CRN and ci.code = ict.code;");
		if (rs.next()) {
			String semester = rs.getString("ci.semester");	//semester of course CRN+code
			String building = rs.getString("ci.building");	//building of course CRN+code
			String instructor = rs.getString("ict.name");	//instructor of course CRN+code
			String code = rs.getString("ci.code");
			
			//must have a semester
			if (semester.equals("")) {
				System.out.println("There is no semester for CRN " + CRN);
				return false;
			}
			
			//must have a building
			if (building.equals("")) {
				System.out.println("There is no building for CRN " + CRN);
				return false;
			}
			
			//must have an instructor
			if (instructor.equals("")) {
				System.out.println("There is no instructor for CRN " + CRN);
				return false;
			}
			
			//must have an instructor
			if (code.equals("")) {
				System.out.println("There is no code for CRN " + CRN);
				return false;
			}
			
			//get all rooms in building
			String room[] = getAllCandidateRooms(CRN).split(",");
			
			//loop through rooms in building
			for (String r : room) {
				if (!r.equals("")) {
					//test for times on MWF
					pushToPQ(CRN, code, semester, "MWF", building, r, instructor);
					//test for times on TR
					pushToPQ(CRN, code, semester, "TR", building, r, instructor);
				}
			}
			return true;
		}
		System.out.println("No found courses for CRN " + CRN);
		return false;
		
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//return room of best AlternateSessionX
	public String getBestRoom() {
		return pq.peek().getRoom();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//return days of best AlternateSessionX
	public String getBestDays() {
		return pq.peek().getDays();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//return time of best AlternateSessionX
	public String getBestTime() {
		return pq.peek().getStart() + "-" + pq.peek().getEnd();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 24
		N= 19
		P= 1
		Complexity = 7
	*/
	//print best 4 AlternateSessionX based on number of students per classification
	public void getTopFour() throws SQLException {
		if (mostStudentsAlternate.getCode().equals("no available alternates")) {
			return;
		} else {
		
		//will call all getBest____ and then poll
		
		int stop = pq.size();	//get # of found AlternateSessionX 's
		System.out.println("\nFor course:");
		System.out.println("   CRN: " + mostStudentsAlternate.getCRN());
		System.out.println("   code: " + mostStudentsAlternate.getCode());
		if (stop >= 4)
			System.out.println("Best 4 options based on number of grad students, then seniors, then juniors, etc.");
		else
			System.out.println("\nBest " + stop + " option(s) based on number of grad students, then seniors, then juniors, etc.");
		System.out.println("---------------------------------------------------------------------------------");
		//only printing top 4 if 4 or more exist
		for (int i = 0; i < 4 && i < stop; i++) {
			System.out.println(i + 1 + ":");
			AlternateSessionX temp = pq.peek();
			System.out.println("Building: " + temp.getBuilding());
			System.out.println("Room: " + temp.getRoom());
			System.out.println("Start: " + temp.getStart());
			System.out.println("End: " + temp.getEnd());
			System.out.println("Days: " + temp.getDays());
			
			//need to reformat students for classification
			System.out.println("Banners that can attend:");
			int j = 0;
			for (String can : temp.getCan().split(",")) {
				if (!can.equals("")) {
					//System.out.println("banner: " + can + "     semester: " + temp.getSemester());
					System.out.print("   |" + can + " : " + getClassification(can, temp.getSemester()) + "|    ");
					j++;
					if (j % 10 == 0)
						System.out.println("");
				}
			}
			
			if (j == 0) System.out.println("   |None|");
			else if (j % 10 > 0) System.out.println("");
			System.out.println(temp.classCount());
			System.out.println("   Number of students that cannot attend: " + temp.getNumCannot());
				
			System.out.println("Banners that cannot attend:");
			
			j = 0;
			for (String cannot : temp.getCannot().split(",")) {
				if (!cannot.equals("")) {
					//System.out.println("banner: " + can + "     semester: " + temp.getSemester());
					System.out.print("   |" + cannot + " : " + getClassification(cannot, temp.getSemester()) + "|    ");
					j++;
					if (j % 10 == 0)
						System.out.println("");
				}
			}
			
			if (j == 0) System.out.println("   |None|");
			else if (j % 10 > 0) System.out.println("");
			
			//System.out.println("Weight: " + temp.getWeight());
			System.out.println(temp.classCountNo());
			System.out.println("   Number of students that cannot attend: " + temp.getNumCannot());
			System.out.println("");
			pq.poll();
		}
		}
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getMostStudentsRoom() {
		return mostStudentsAlternate.getRoom();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getMostStudentsDays() {
		return mostStudentsAlternate.getDays();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getMostStudentsTime() {
		return mostStudentsAlternate.getStart() + "-" + mostStudentsAlternate.getEnd();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getMostStudentsCount() {
		return mostStudentsAlternate.getNumCan();
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 17
		N= 12
		P= 1
		Complexity = 7
	*/
	public void getMostStudentsAlternate() throws SQLException {
		
		if (mostStudentsAlternate.getCode().equals("no available alternates")) {
			System.out.println(mostStudentsAlternate.getCode());
		} else {
		
		//print all data about mostStudentsAlternate
		System.out.println("\nOption with most students: ");
		System.out.println("--------------------------");
		System.out.println("Building: " + mostStudentsAlternate.getBuilding());
		System.out.println("Room: " + mostStudentsAlternate.getRoom());
		System.out.println("Start: " + mostStudentsAlternate.getStart());
		System.out.println("End: " + mostStudentsAlternate.getEnd());
		System.out.println("Days: " + mostStudentsAlternate.getDays());		
		
		System.out.println("Banners that can attend:");
		int j = 0;
		for (String can : mostStudentsAlternate.getCan().split(",")) {
			if (!can.equals("")) {
				//System.out.println("banner: " + can + "     semester: " + temp.getSemester());
				System.out.print("   |" + can + " : " + getClassification(can, mostStudentsAlternate.getSemester()) + "|    ");
				j++;
				if (j % 10 == 0)
					System.out.println("");
			}
		}
		
		if (j == 0) System.out.println("   |None|");
		else if (j % 10 > 0) System.out.println("");
		System.out.println(mostStudentsAlternate.classCount());
		System.out.println("   Number of students that can attend: " + mostStudentsAlternate.getNumCan());
		
		System.out.println("Banners that cannot attend:");
		
		j = 0;
		for (String cannot : mostStudentsAlternate.getCannot().split(",")) {
			if (!cannot.equals("")) {
				//System.out.println("banner: " + can + "     semester: " + temp.getSemester());
				System.out.print("   |" + cannot + " : " + getClassification(cannot, mostStudentsAlternate.getSemester()) + "|    ");
				j++;
				if (j % 10 == 0)
					System.out.println("");
			}
		}
		
		if (j == 0) System.out.println("   |None|");
		else if (j % 10 > 0) System.out.println("");
		
		System.out.println(mostStudentsAlternate.classCountNo());
		System.out.println("   Number of students that cannot attend: " + mostStudentsAlternate.getNumCannot());
		}
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 24
		N= 19
		P= 1
		Complexity = 7
	*/
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

			// adds temp into newStrings and increments 
			newStrings[i++] = temp;
		}
		return newStrings;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 12
		N= 9
		P= 1
		Complexity = 5
	*/
	//classification is equal to number of years
	//SU is currently treated as FR equivalent
	public static int convertClassification(String c) {
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
		//System.err.println("Warning: '" + c + "' is an unexpected classification.");
		return 1;
	}
}

//class that store data for another time and location that a course can be offered
//semester, building, CRN, and code do not change and are stored
class AlternateSessionX {
	
	String CRN;	//does not change
	String code;	//does not change
	String building;	//does not change
	String room;
	String start;
	String end;
	String days;	//MWF or TR
	String semester; //does not change
	String bannersCan;
	String bannersCannot;
	int numPerClassification[];	//number of students that can attend by classification [FR, SO, JR, SR, GR]
	int numPerClassificationNo[];	//number of students that cannot attend by classification [FR, SO, JR, SR, GR]
	int totalStudentsCan;
	int totalStudentsCannot;
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//constructor that sets all data members
	public AlternateSessionX(String CRN, String code, String building, String room, String start, String end, String days, String semester, String bannersCan, String bannersCannot, int stuCounts[], int stusNoCounts[], int totalStudentsCan, int totalStudentsCannot) {
		this.CRN = CRN;
		this.code = code;
		this.building = building;
		this.room = room;
		this.start = start;
		this.end = end;
		this.days = days;
		this.semester = semester;
		this.bannersCan = bannersCan;
		this.bannersCannot = bannersCannot;
		numPerClassification = stuCounts;
		numPerClassificationNo = stusNoCounts;
		this.totalStudentsCan = totalStudentsCan; 
		this.totalStudentsCannot = totalStudentsCannot;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getCRN() {
		return CRN;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getCode() {
		return code;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	//getters for all data members
	public String getBuilding() {
		return building;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getRoom() {
		return room;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getStart() { 
		return start;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getEnd() {
		return end;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getDays() {
		return days;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getSemester() {
		return semester;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getCan() {
		return bannersCan;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String getCannot() {
		return bannersCannot;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public String classCount() {
		return "   Graduate students: " + numPerClassification[4] +"\n   Seniors: "+ numPerClassification[3] + "\n   Juniors: "+ numPerClassification[2] + "\n   Sophomores: "+ numPerClassification[1] + "\n   Freshmen: "+ numPerClassification[0];
	}
	
	public String classCountNo() {
		/*System.out.println("Graduate students: " + numPerClassificationNo[4]);
		System.out.println("\nSeniors: "+ numPerClassificationNo[3]);
		System.out.println("\nJuniors: "+ numPerClassificationNo[2]);
		System.out.println("\nSophomores: "+ numPerClassificationNo[1]);
		System.out.println("\nFreshmen: "+ numPerClassificationNo[0]);*/
		
		return "   Graduate students: " + numPerClassificationNo[4] +"\n   Seniors: "+ numPerClassificationNo[3] + "\n   Juniors: "+ numPerClassificationNo[2] + "\n   Sophomores: "+ numPerClassificationNo[1] + "\n   Freshmen: "+ numPerClassificationNo[0];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumGrad() {
		return numPerClassification[4];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumSenior() {
		return numPerClassification[3];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumJunior() {
		return numPerClassification[2];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumSophomore() {
		return numPerClassification[1];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumFreshman() {
		return numPerClassification[0];
	}
	
	
	//qqq
	public int getNumGradNo() {
		return numPerClassificationNo[4];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumSeniorNo() {
		return numPerClassificationNo[3];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumJuniorNo() {
		return numPerClassificationNo[2];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumSophomoreNo() {
		return numPerClassificationNo[1];
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumFreshmanNo() {
		return numPerClassificationNo[0];
	}
	
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumCan() {
		return totalStudentsCan;
	}
	
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 1
		N= 2
		P= 1
		Complexity = 1
	*/
	public int getNumCannot() {
		return totalStudentsCannot;
	}
}

//defines priority of pq (data member of StudentCourseManagerX)
class PQsort implements Comparator<AlternateSessionX> {
	 
	/*PCW14a :)
		-----COMPLEXITY :D-----
		E= 12
		N= 8
		P= 1
		Complexity = 6
	*/
	public int compare(AlternateSessionX one, AlternateSessionX two) {
		//grads most important
		if (two.getNumGrad() - one.getNumGrad() != 0)
			return two.getNumGrad() - one.getNumGrad();
		//then seniors most important
		else if (two.getNumSenior() - one.getNumSenior() != 0)
			return two.getNumSenior() - one.getNumSenior();
		//then juniors most important
		else if (two.getNumJunior() - one.getNumJunior() != 0)
			return two.getNumJunior() - one.getNumJunior();
		//then sophomores most important
		else if (two.getNumSophomore() - one.getNumSophomore() != 0)
			return two.getNumSophomore() - one.getNumSophomore();
		//else freshmen most important
		return two.getNumFreshman() - one.getNumFreshman();
	}
}