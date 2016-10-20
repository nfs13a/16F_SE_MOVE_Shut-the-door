Feature: Move a course instance from day+time to another

	Scenario: CS120 exists
		Given CSV "big"
		Then course "CS120" exists

	Scenario: CS120 has CRN 11041
		Given using CSV "big"
		And course "CS120" exists
		Then CRN 11041 exists for course "CS120"

	Scenario: CS120 with CRN 11041 is offered in Spring
		Given using CSV "big"
		And course "CS120" exists
		And CRN 11041 exists for course "CS120"
		Then CRN 11041 of course "CS120" is offered semester 1510

	Scenario: CS120 with CRN 11041 is offered MWF from 10-10:50
		Given using CSV "big"
		And course "CS120" exists
		And CRN 11041 exists for course "CS120"
		And CRN 11041 of course "CS120" is offered semester 1510
		Then CRN 11041 of course "CS120" is offered "MWF" from "10:00-10:50"

	Scenario: student 000234506 is taking CS120 with CRN 11041
		Given using CSV "big"
		And course "CS120" exists
		And CRN 11041 exists for course "CS120"
		Then student "000234506" is enrolled in CRN 11041 for course "CS120"

	Scenario: student 000234506 exists
		Given using CSV "big"
		Then student "000234506" exists

	Scenario: student 000234506 is a sophomore
		Given using CSV "big"
		And student "000234506" exists
		Then student "000234506" has classification "SO"