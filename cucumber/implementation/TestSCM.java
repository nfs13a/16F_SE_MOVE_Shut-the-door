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
		String pass;		//mysql password
		String fileChoice;	//which file to use
		Scanner input = new Scanner(System.in);
		
		System.out.print("What is your MySQL password (enter \"NONE\" if there is not one): ");
		pass = input.next();
		
		System.out.println("Which data would you like to use?\n1 for cs374_anon\n2 for cs374_f16_anon");
		fileChoice = input.next();
		while (!fileChoice.equals("1") && !fileChoice.equals("2")) {
			System.out.println("Invalid input");
			System.out.println("Which data would you like to use?\n1 for cs374_anon\n2 for cs374_f16_anon");
			fileChoice = input.next();
		}
		
		
		String cont = "y";

		while (!cont.equalsIgnoreCase("n")) {
			StudentCourseManager scm = new StudentCourseManager(pass, fileChoice);	//this constructor parses if needed
			
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
			while (!cont.equalsIgnoreCase("y") && !cont.equalsIgnoreCase("n")) {
				System.out.println("Invalid input");
				System.out.println("Do you want to check another course (y/n): ");
				cont = input.next();
			}
		}
	}
}