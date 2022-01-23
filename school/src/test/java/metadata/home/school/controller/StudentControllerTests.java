package metadata.home.school.controller;

import metadata.home.school.model.Course;
import metadata.home.school.model.ServiceResponse;
import metadata.home.school.model.Student;
import metadata.home.school.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentControllerTests {
    StudentService studentService = mock(StudentService.class);
    StudentController controller = new StudentController(studentService);

    @Test
    void findByIdGetResult() {
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");
        when(studentService.findById(1)).thenReturn(java.util.Optional.of(student));
        var result = controller.getStudentById(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((Student)result.getBody()).getId(), 1);
        assertEquals(((Student)result.getBody()).getName(), "StudentA");
    }

    @Test
    void findByIdNoResult() {
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");
        when(studentService.findById(1)).thenReturn(null);
        var result = controller.getStudentById(1);

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(result.getBody(), "Student with id: 1 was not found");
    }

    @Test
    void getAllTests(){
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        var student2 = new Student();
        student2.setId(2);
        student2.setSchoolId("B");
        student2.setName("StudentB");

        when(studentService.findAll()).thenReturn(new ArrayList<>(){{add(student1); add(student2);}});
        var result = controller.getAllStudents();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(1).getId(), 2);
    }

    @Test
    void findAllEmpty() {
        when(studentService.findAll()).thenReturn(new ArrayList<>());
        var result = controller.getAllStudents();

        assertEquals(result.size(), 0);
    }

    @Test
    void getAllCoursesFromStudent(){
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(new HashSet<>(){{add(course);}});

        when(studentService.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = controller.getCoursesFromStudent(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((HashSet<Course>)result.getBody()).size(), 1);
        assertEquals(((HashSet<Course>)result.getBody()).iterator().next().getId(), 1);
    }

    @Test
    void getAllCoursesFromStudentEmptyTest() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = controller.getCoursesFromStudent(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((HashSet<Course>)result.getBody()).size(), 0);
    }

    @Test
    void getAllCoursesFromStudentNotExist(){
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");
        when(studentService.findById(1)).thenReturn(null);
        var result = controller.getStudentById(1);

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(result.getBody(), "Student with id: 1 was not found");
    }

    @Test
    void saveSuccess() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.save(student1)).thenReturn(new ServiceResponse(student1, ""));
        var result = controller.createStudent(student1);

        assertEquals(result.getStatusCode(), HttpStatus.CREATED);
        assertEquals(((Student)result.getBody()).getId(), 1);
        assertEquals(((Student)result.getBody()).getSchoolId(), "A");
    }

    @Test
    void saveErrorExistingStudent() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.save(student1)).thenReturn(new ServiceResponse(null, "A student with school id: A already exists"));
        var result = controller.createStudent(student1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A student with school id: A already exists");
    }

    @Test
    void saveErrorMoreThanFiveCourses() {
        var courses = new HashSet<Course>();
        for(var i = 0; i < 6; i++){
            var course = new Course();
            course.setId(i);
            course.setName("Course" + i);
            courses.add(course);
        }

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(courses);

        when(studentService.save(student1)).thenReturn(new ServiceResponse(null, "A student cannot register to more than 5 courses"));
        var result = controller.createStudent(student1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A student cannot register to more than 5 courses");
    }

    @Test
    void updateSuccess() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.update(1, student1)).thenReturn(new ServiceResponse(student1, ""));
        var result = controller.updateStudent(1, student1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((Student)result.getBody()).getId(), 1);
        assertEquals(((Student)result.getBody()).getSchoolId(), "A");
    }

    @Test
    void upateErrorExistingStudent() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.update(1, student1)).thenReturn(new ServiceResponse(null, "A student with school id: A already exists"));
        var result = controller.updateStudent(1, student1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A student with school id: A already exists");
    }

    @Test
    void updateErrorMoreThanFiveCourses() {
        var courses = new HashSet<Course>();
        for(var i = 0; i < 6; i++){
            var course = new Course();
            course.setId(i);
            course.setName("Course" + i);
            courses.add(course);
        }

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(courses);

        when(studentService.update(1, student1)).thenReturn(new ServiceResponse(null, "A student with school id: B already exists"));
        var result = controller.updateStudent(1, student1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A student with school id: B already exists");
    }

    @Test
    void deleteTest(){
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentService.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = controller.deleteStudent(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void RegisterSuccess(){
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(new HashSet<>(){{add(course);}});

        when(studentService.register(1, new ArrayList<>(){{add(course);}})).thenReturn(new ServiceResponse(student1, ""));
        var result = controller.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((Student)result.getBody()).getCourses().size(), 1);
        assertEquals(((Student)result.getBody()).getCourses().iterator().next().getName(), "Course1");
    }

    @Test
    void registerErrorMoreThanFiveCourses() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        when(studentService.register(1, new ArrayList<>(){{add(course);}})).thenReturn(new ServiceResponse(null, "A student cannot register to more than 5 courses"));
        var result = controller.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A student cannot register to more than 5 courses");
    }

    @Test
    void registerErrorAlreadyExistingCourse() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        when(studentService.register(1, new ArrayList<>(){{add(course);}})).thenReturn(new ServiceResponse(null, "Student was already registered to course: Course1"));
        var result = controller.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "Student was already registered to course: Course1");
    }

    @Test
    void registerErrorCourseNotFound() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        when(studentService.register(1, new ArrayList<>(){{add(course);}})).thenReturn(new ServiceResponse(null, "Cannot find course: Course1"));
        var result = controller.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "Cannot find course: Course1");
    }
}
