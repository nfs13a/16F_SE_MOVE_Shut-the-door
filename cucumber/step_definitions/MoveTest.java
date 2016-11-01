package step_definitions;

import cucumber.api.java.en.*;
import cucumber.api.PendingException;
import implementation.StudentCourseManager;
import static org.junit.Assert.*;

public class MoveTest {
	//parses CSV, sets up database of student/course info, gives data about students/courses
	StudentCourseManager scm;
	
	@Given("^CSV \"([^\"]*)\"$")
	public void csv(String filename) throws Throwable {
	    scm = new StudentCourseManager(filename);
	    scm.parseCRN();
	}

	@Then("^course \"([^\"]*)\" exists$")
	public void courseExists(String code) throws Throwable {
	    assertEquals(scm.courseExists(code), true);
	}

	@Given("^database courses$")
	public void databaseCourses() throws Throwable {
		scm = new StudentCourseManager();
	}

	@Then("^CRN \"([^\"]*)\" exists for course \"([^\"]*)\"$")
	public void crnExistsForCourse(String CRN, String code) throws Throwable {
	    assertEquals(scm.codeExistsForCRN(CRN, code), true);
	}

	@Then("^CRN \"([^\"]*)\" of course \"([^\"]*)\" is offered semester \"([^\"]*)\"$")
	public void crnOfCourseIsOfferedSemester(String CRN, String code, String semester) throws Throwable {
		assertEquals(scm.getSemester(CRN, code), semester);
	}

	@Then("^CRN \"([^\"]*)\" of course \"([^\"]*)\" is offered \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void crnOfCourseIsOfferedFromTo(String CRN, String code, String days, String startTime, String endTime) throws Throwable {
		assertEquals(scm.getDays(CRN, code), days);
	    assertEquals(scm.getStartTime(CRN, code), startTime);
	    assertEquals(scm.getEndTime(CRN, code), endTime);
	}
	
	@Then("^CRN \"([^\"]*)\" for course \"([^\"]*)\" is taught in building \"([^\"]*)\" in room \"([^\"]*)\"$")
	public void crnForCourseIsTaughtInBuildingInRoom(String CRN, String code, String building, String room) throws Throwable {
	    assertEquals(scm.getBuilding(CRN, code), building);
	    assertEquals(scm.getRoom(CRN, code), room);
	}
	
	@Then("^CRN \"([^\"]*)\" for course \"([^\"]*)\" has student limit (\\d+)$")
	public void crnForCourseHasStudentLimit(String CRN, String code, int max) throws Throwable {
	    assertEquals(scm.getMaxStudents(CRN, code), max);
	}

	@Then("^student \"([^\"]*)\" is enrolled in CRN \"([^\"]*)\" for course \"([^\"]*)\"$")
	public void studentIsEnrolledInCRNForCourse(String banner, String CRN, String code) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    assertEquals(scm.studentEnrolled(CRN,code,banner), true);
	}

	@Then("^student \"([^\"]*)\" exists$")
	public void studentExists(String banner) throws Throwable {
	    assertEquals(scm.studentExists(banner), true);
	}

	@Then("^student \"([^\"]*)\" has classification \"([^\"]*)\" when taking CRN \"([^\"]*)\" for course \"([^\"]*)\"$")
	public void studentHasClassificationWhenTakingCRNForCourse(String banner, String classification, String CRN, String code) throws Throwable {
	    assertEquals(scm.getClassification(banner, CRN, code), classification);
	}
	
	@Then("^student \"([^\"]*)\" has classification \"([^\"]*)\" in semester \"([^\"]*)\"$")
	public void studentHasClassificationInSemester(String banner, String classification, String semester) throws Throwable {
		assertEquals(scm.getClassification(banner, semester), classification);
	}
	
	@Then("^instructor \"([^\"]*)\" teaches course \"([^\"]*)\" with CRN \"([^\"]*)\"$")
	public void instructorTeachesCourseWithCRN(String name, String code, String CRN) throws Throwable {
	    assertEquals(scm.getInstructor(CRN, code), name);
	}

	@Then("^instructor \"([^\"]*)\" teaches classes \"([^\"]*)\" in \"([^\"]*)\"$")
	public void instructorTeachesClassesIn(String instructor, String CRNs, String semester) throws Throwable {
	    assertEquals(scm.getCoursesTaughtByInstructorDuringSemester(instructor, semester), CRNs);
	}

	@Then("^the instructor for code \"([^\"]*)\" and crn \"([^\"]*)\" during semester \"([^\"]*)\" is \"([^\"]*)\"$")
	public void theInstructorForCodeAndCrnDuringSemesterIs(String code, String crn, String semester, String instructor) throws Throwable {
		assertEquals(scm.getInstructorForClassDuringSemester(crn, code, semester), instructor);
	}
	
	@Then("^student \"([^\"]*)\" is not free semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void studentIsNotFreeSemesterOnDaysFromTo(String banner, String semester, String days, String startTime, String endTime) throws Throwable {
		assertEquals(!scm.studentIsFree(banner, semester, days, startTime, endTime), true);
	}

	@Then("^student \"([^\"]*)\" is free semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void studentIsFreeSemesterOnDaysFromTo(String banner, String semester, String days, String startTime, String endTime) throws Throwable {
		assertEquals(scm.studentIsFree(banner, semester, days, startTime, endTime), true);
	}

	@Given("^instructor \"([^\"]*)\" exists$")
	public void instructorExists(String name) throws Throwable {
		assertEquals(scm.instructorExists(name), true);
	}

	@Then("^instructor \"([^\"]*)\" is not free semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void instructorIsNotFreeSemesterOnDaysFromTo(String banner, String semester, String days, String startTime, String endTime) throws Throwable {
		assertEquals(!scm.instructorIsFree(banner, semester, days, startTime, endTime), true);
	}
	
	@Then("^instructor \"([^\"]*)\" is free semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void instructorIsFreeSemesterOnDaysFromTo(String banner, String semester, String days, String startTime, String endTime) throws Throwable {
		assertEquals(scm.instructorIsFree(banner, semester, days, startTime, endTime), true);
	}
	
	/*@Then("^CRN \"([^\"]*)\" of course \"([^\"]*)\" has seats (\\d+)$")
	public void crnOfCourseHasSeats(String CRN, String code, int max) throws Throwable {
	    assertEquals(scm.getMaxSeats(CRN, code), max);
	}*/
	
	@Then("^the max seats in \"([^\"]*)\" room \"([^\"]*)\" is (\\d+)$")
	public void theMaxSeatsInRoomIs(String building, String room, int max) throws Throwable {
	    assertEquals(scm.getMaxSeats(building, room), max);
	}
	
	@Then("^CRN \"([^\"]*)\" for course \"([^\"]*)\" can fit in rooms \"([^\"]*)\"$")
	public void crnForCourseCanFitInRooms(String CRN, String code, String rooms) throws Throwable {
	    assertEquals(scm.getAllCandidateRooms(CRN, code), rooms);
	}
	
	/*@Then("^building \"([^\"]*)\" room \"([^\"]*)\" is free \"([^\"]*)\" during semester \"([^\"]*)\" on days \"([^\"]*)\"$")
	public void buildingRoomIsFreeDuringSemesterOnDays(String building, String room, String times, String semester, String days) throws Throwable {
		assertEquals(scm.getAllOpenTimes(building, room, semester, days), times);
	}*/
	
	@Then("^building \"([^\"]*)\" room \"([^\"]*)\" is free during semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void buildingRoomIsFreeDuringSemesterOnDaysFromTo(String building, String room, String semester, String days, String start, String end) throws Throwable {
	    assertEquals(scm.roomIsFree(building, room, semester, days, start, end), true);
	}
	
	@Then("^building \"([^\"]*)\" room \"([^\"]*)\" is free \"([^\"]*)\" during semester \"([^\"]*)\" on days \"([^\"]*)\" for (\\d+) minutes$")
	public void buildingRoomIsFreeDuringSemesterOnDaysForMinutes(String building, String room, String times, String semester, String days, int time) throws Throwable {
		assertEquals(scm.getAllOpenTimes(building, room, semester, days, time), times);
	}
	
	@Then("^students \"([^\"]*)\" can attend CRN \"([^\"]*)\" for course \"([^\"]*)\" in semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void studentsCanAttendCRNForCourseInSemesterOnDaysFromTo(String students, String CRN, String code, String semester, String days, String start, String end) throws Throwable {
	    assertEquals(scm.studentsCanAttend(CRN, code, semester, days, start, end), students);
	}

	@Then("^students \"([^\"]*)\" cannot attend CRN \"([^\"]*)\" for course \"([^\"]*)\" in semester \"([^\"]*)\" on days \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
	public void studentsCannotAttendCRNForCourseInSemesterOnDaysFromTo(String students, String CRN, String code, String semester, String days, String start, String end) throws Throwable {
		assertEquals(scm.studentsCannotAttend(CRN, code, semester, days, start, end), students);
	}
}