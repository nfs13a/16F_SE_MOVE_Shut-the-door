package implementation;

import java.util.Scanner;
import java.sql.*;

public class TestSCM {
	public static void main(String[] args) throws SQLException {
		//StudentCourseManager scm = new StudentCourseManager();
		// StudentCourseManager scm = new StudentCourseManager("big");
		// scm.parseCRN();

		String crn;		//crn of course
		String code;	//code of course
		String response;	//if run or not (y/n)
		String pass;		//mysql password
		Scanner input = new Scanner(System.in);

		System.out.print("Is this your first time running this tool (y/n): ");
		response = input.next();
		while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
			System.out.println("Invalid input");
			System.out.print("Is this your first time running this tool (y/n): ");
			response = input.next();
		}
		
		System.out.print("What is your MySQL password (enter \"NONE\" if there is not one): ");
		pass = input.next();
		
		String cont = "y";

		while (!cont.equalsIgnoreCase("n")) {
			StudentCourseManager scm;
			if (response.equalsIgnoreCase("y")) {
				scm = new StudentCourseManager(pass, true);
				scm.parseCRN();
				response = "n";
			} else {
				scm = new StudentCourseManager(pass, false);
			}
			
			System.out.print("Enter a code (e.g. CS120): ");
			code = input.next();
			String CRNs = scm.getAllCRNs(code);
			if (CRNs.contains("No found CRN's")) {
				System.out.println(CRNs);
			} else {
				System.out.print("Enter a CRN (" + CRNs + "): ");
				crn = input.next();

				if (scm.setAlternates(crn, code)) {
					scm.getTopFour();
					scm.getMostStudentsAlternate();
				}
			}
			
			System.out.println("Do you want to check another (y/n): ");
			cont = input.next();
		}
	}
}