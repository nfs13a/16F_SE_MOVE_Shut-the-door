Feature: Move a course instance from day+time to another

	#Scenario: CS120 exists
		#Given CSV "big"
		#Then course "CS120" exists

	Scenario: CS120 has CRN "11041"
		Given database courses
		And course "CS120" exists
		Then CRN "11041" exists for course "CS120"

	Scenario: CS120 with CRN "11041" is offered in Spring
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then CRN "11041" of course "CS120" is offered semester "201510"

	Scenario: CS120 with CRN "11041" is offered MWF from 10-10:50
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		And CRN "11041" of course "CS120" is offered semester "201510"
		Then CRN "11041" of course "CS120" is offered "MWF" from "1000" to "1050"

	Scenario: student 000234506 is taking CS120 with CRN "11041"
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then student "000234506" is enrolled in CRN "11041" for course "CS120"

	Scenario: student 000234506 exists
		Given database courses
		Then student "000234506" exists

	Scenario: student 000234506 is a sophomore
		Given database courses
		And student "000234506" exists
		Then student "000234506" has classification "SO" when taking CRN "11041" for course "CS120"
		And student "000234506" has classification "SO" in semester "201510"

	Scenario: instructor Aaron Robison teaches a few courses
		Given database courses
		Then instructor "Aaron Robison" teaches classes "11165,11172,11173" in "201510"

	Scenario: Get instructor for BGRK441 during 201610
		Given database courses
		Then the instructor for code "BGRK441" and crn "10003" during semester "201610" is "Trevor Thompson"
