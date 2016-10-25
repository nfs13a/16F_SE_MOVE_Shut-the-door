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
	    // Write code here that turns the phrase above into concrete actions
	    assertEquals(code,scm.courseExists(code));
	}

	@Given("^database courses$")
	public void databaseCourses() throws Throwable {
		scm = new StudentCourseManager();
	}

	/*@Given("^course \"([^\"]*)\" exists$")
	public void courseExists(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}*/

	@Then("^CRN \"([^\"]*)\" exists for course \"([^\"]*)\"$")
	public void crnExistsForCourse(String CRN, String code) throws Throwable {
	    assertEquals(code,scm.getCodeFromCRN(CRN));
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

	@Then("^student \"([^\"]*)\" is enrolled in CRN \"([^\"]*)\" for course \"([^\"]*)\"$")
	public void studentIsEnrolledInCRNForCourse(String banner, String CRN, String code) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    assertEquals(banner,scm.studentEnrolled(CRN,code,banner));
	}

	@Then("^student \"([^\"]*)\" exists$")
	public void studentExists(String banner) throws Throwable {
	    assert(scm.studentExists(banner));
	}

	@Then("^student \"([^\"]*)\" has classification \"([^\"]*)\" when taking CRN \"([^\"]*)\" for course \"([^\"]*)\"$")
	public void studentHasClassificationWhenTakingCRNForCourse(String banner, String classification, String CRN, String code) throws Throwable {
	    assertEquals(scm.getClassification(banner, CRN, code), classification);
	}
}