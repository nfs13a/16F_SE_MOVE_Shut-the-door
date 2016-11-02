Feature: Move a course instance from day+time to another

	#Scenario: CS120 exists
		#Given CSV "big"
		#Then course "CS120" exists

	Scenario: CS120 exists
		Given database courses
		Then course "CS120" exists

	Scenario: CS120 has CRN "11041"
		Given database courses
		And course "CS120" exists
		Then CRN "11041" exists for course "CS120"

	Scenario: CS120 with CRN "11041" is offered in Spring
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then CRN "11041" of course "CS120" is offered semester "201510"

	Scenario: CS120 with CRN "11041" is offered MWF from 10-10:50 in fall of 2015
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		And CRN "11041" of course "CS120" is offered semester "201510"
		Then CRN "11041" of course "CS120" is offered "MWF" from "1000" to "1050"

	Scenario: CS120 with CRN "11041" is offered in MBB room 314
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then CRN "11041" for course "CS120" is taught in building "MBB" in room "314"

	Scenario: CS120 with CRN "11041" has a limit of 30 students
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then CRN "11041" for course "CS120" has student limit 30

	Scenario: student 000234506 is taking CS120 with CRN "11041"
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		Then student "000234506" is enrolled in CRN "11041" for course "CS120"

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
		Then instructor "Aaron Robison" teaches course "CHEM101" with CRN "11165"
		#why did we format this test like this?
		And instructor "Aaron Robison" teaches classes "11165,11172,11173" in "201510"

	Scenario: Get instructor for BGRK441 during 201610
		Given database courses
		Then the instructor for code "BGRK441" and crn "10003" during semester "201610" is "Trevor Thompson"

	Scenario: student 000234506 free times
		Given database courses
		And student "000234506" exists
		Then student "000234506" is not free semester "201510" on days "MWF" from "1000" to "1200"
		But student "000234506" is free semester "201510" on days "MWF" from "1100" to "1130"
		But student "000234506" is free semester "201510" on days "MWF" from "1100" to "1130"

	Scenario: Trevor Thompson free times
		Given database courses
		And instructor "Trevor Thompson" exists
		Then instructor "Trevor Thompson" is not free semester "201510" on days "MWF" from "0930" to "1050"
		But instructor "Trevor Thompson" is free semester "201510" on days "TR" from "0800" to "0920"

	Scenario: the max size of MBB 314 is 30
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		And CRN "11041" of course "CS120" is offered semester "201510"
		And CRN "11041" for course "CS120" is taught in building "MBB" in room "314"
		Then the max seats in "MBB" room "314" is 30
		#Then CRN "11041" of course "CS120" has seats 30

	Scenario: 11041 CS120 can fit in many rooms
		Given database courses
		And course "CS120" exists
		And CRN "11041" exists for course "CS120"
		And CRN "11041" for course "CS120" is taught in building "MBB" in room "314"
		Then CRN "11041" for course "CS120" can fit in rooms "115,116,117,118,201,215,216,217,218,301,314,315,316,318,"

	Scenario: MBB 314 is free certain times
		Given database courses
		Then building "MBB" room "314" is free during semester "201510" on days "MWF" from "0800" to "0850"
		Then building "MBB" room "314" is free "0800-0850,1200-1250,1300-1350,1500-1550,1600-1650," during semester "201510" on days "MWF" for 50 minutes

	Scenario: students (list) can attend course at time in room
		Given database courses
		And building "MBB" room "314" is free during semester "201510" on days "MWF" from "0800" to "0850"
		And building "MBB" room "314" is free "0800-0850,1200-1250,1300-1350,1500-1550,1600-1650," during semester "201510" on days "MWF" for 50 minutes
		Then instructor "John Homer" is free semester "201510" on days "MWF" from "0800" to "0850"
		And students "000035206,000053912,000092980,000203213,000228985,000234506,000268963,000273509,000298206,000334136,000500936,000555650,000557186,000639144,000693048,000745155,000866953,000871940,000975831," can attend CRN "11041" for course "CS120" in semester "201510" on days "MWF" from "0800" to "0850"
		And students "000153341,000158129,000439071,000564317,000651983,000962704," cannot attend CRN "11041" for course "CS120" in semester "201510" on days "MWF" from "0800" to "0850"

	Scenario: the best option for moving 11041 CS120
		Given database courses
		And CRN "11041" for course "CS120" can fit in rooms "115,116,117,118,201,215,216,217,218,301,314,315,316,318,"
		Then the best option for CRN "11041" for course "CS120" is room "115" on days "MWF" from "1200" to "1250" by weight 38