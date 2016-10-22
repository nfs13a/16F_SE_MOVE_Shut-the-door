package step_definitions;

import cucumber.api.java.en.*;
import cucumber.api.PendingException;
import implementation.StudentCourseManager;

public class MoveTest {
	//parses CSV, sets up database of student/course info, gives data about students/courses
	StudentCourseManager scm;
	
	@Given("^CSV \"([^\"]*)\"$")
	public void csv(String filename) throws Throwable {
	    scm = new StudentCourseManager(filename);
	    scm.parseCRN();
	}

	@Then("^course \"([^\"]*)\" exists$")
	public void courseExists(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	@Given("^using CSV \"([^\"]*)\"$")
	public void usingCSV(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	/*@Given("^course \"([^\"]*)\" exists$")
	public void courseExists(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}*/

	@Then("^CRN (\\d+) exists for course \"([^\"]*)\"$")
	public void crnExistsForCourse(int arg1, String arg2) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	/*@Given("^CRN (\\d+) exists for course \"([^\"]*)\"$")
	public void crnExistsForCourse(int arg1, String arg2) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}*/

	@Then("^CRN (\\d+) of course \"([^\"]*)\" is offered semester (\\d+)$")
	public void crnOfCourseIsOfferedSemester(int arg1, String arg2, int arg3) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	/*@Given("^CRN (\\d+) of course \"([^\"]*)\" is offered semester (\\d+)$")
	public void crnOfCourseIsOfferedSemester(int arg1, String arg2, int arg3) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}*/

	@Then("^CRN (\\d+) of course \"([^\"]*)\" is offered \"([^\"]*)\" from \"([^\"]*)\"$")
	public void crnOfCourseIsOfferedFrom(int arg1, String arg2, String arg3, String arg4) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	@Then("^student \"([^\"]*)\" is enrolled in CRN (\\d+) for course \"([^\"]*)\"$")
	public void studentIsEnrolledInCRNForCourse(String arg1, int arg2, String arg3) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	@Then("^student \"([^\"]*)\" exists$")
	public void studentExists(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}

	/*@Given("^student \"([^\"]*)\" exists$")
	public void studentExists(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}*/

	@Then("^student \"([^\"]*)\" has classification \"([^\"]*)\"$")
	public void studentHasClassification(String arg1, String arg2) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}
}