# metadata

Welcome to school application.

To run the application you only need to be on school folder and run the command:
  `docker-compose -f dev/docker-compose.yml up`
  
After the docker images have started the web server will be listening on port 8080

The following endpoints will be available:

Student attributes:
- id: database id (automatically asigned by the application)
- schoolId: school number for the user
- name: name of the student
- courses: list of courses where the user is registered

Course attributes:
- id: database id (automatically asigned by the application)
- name: name of the student
- students: list of students registered in it

- GET: /api/students -> list all students
- GET: /api/students/{id} -> get student with the corresponding id
- GET: /api/students/{id}/courses -> get the list of courses for the above mentioned student
- GET: /api/students/noCourses -> get the list of all students not registered in any course
- POST: /api/students (user in body) -> creates a new student 
- PUT: /api/students/{id} (user in body) -> updates a student
- DELETE: /api/students/{id} -> deletes a student 
- POST: /api/students/{id}/register (list of courses in body) -> register a student in the received courses

- GET: /api/courses -> list all courses
- GET: /api/courses/{id} -> get course with the corresponding id
- GET: /api/courses/{id}/students -> get the list of students for the above mentioned course
- GET: /api/courses/noStudents -> get the list of all courses with no students registered
- POST: /api/courses (course in body) -> creates a new course 
- PUT: /api/courses/{id} (course in body) -> updates a course
- DELETE: /api/courses/{id} -> deletes a courses 


