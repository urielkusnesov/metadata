package metadata.home.school.controller;

import metadata.home.school.model.Course;
import metadata.home.school.model.ServiceResponse;
import metadata.home.school.model.Student;
import metadata.home.school.service.CourseService;
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
public class CourseControllerTests {
    CourseService courseService = mock(CourseService.class);
    CourseController controller = new CourseController(courseService);

    @Test
    void findByIdGetResult() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");
        when(courseService.findById(1)).thenReturn(java.util.Optional.of(course));
        var result = controller.getCourseById(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((Course)result.getBody()).getId(), 1);
        assertEquals(((Course)result.getBody()).getName(), "Course1");
    }

    @Test
    void findByIdNoResult() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");
        when(courseService.findById(1)).thenReturn(null);
        var result = controller.getCourseById(1);

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(result.getBody(), "Course with id: 1 was not found");
    }

    @Test
    void getAllTests(){
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        var course2 = new Course();
        course2.setId(2);
        course2.setName("Course2");

        when(courseService.findAll()).thenReturn(new ArrayList<>(){{add(course1); add(course2);}});
        var result = controller.getAllCourses();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(1).getId(), 2);
    }

    @Test
    void findAllEmpty() {
        when(courseService.findAll()).thenReturn(new ArrayList<>());
        var result = controller.getAllCourses();

        assertEquals(result.size(), 0);
    }

    @Test
    void getAllStudentsFromCourse(){
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");

        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");
        course1.setStudents(new HashSet<>(){{add(student);}});

        when(courseService.findById(1)).thenReturn(java.util.Optional.of(course1));
        var result = controller.getStudentsFromCourse(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((HashSet<Student>)result.getBody()).size(), 1);
        assertEquals(((HashSet<Student>)result.getBody()).iterator().next().getId(), 1);
    }

    @Test
    void getAllCoursesFromStudentEmptyTest() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.findById(1)).thenReturn(java.util.Optional.of(course1));
        var result = controller.getStudentsFromCourse(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((HashSet<Course>)result.getBody()).size(), 0);
    }

    @Test
    void getAllCoursesFromStudentNotExist(){
        var course = new Course();
        course.setId(1);
        course.setName("StudentA");
        when(courseService.findById(1)).thenReturn(null);
        var result = controller.getCourseById(1);

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(result.getBody(), "Course with id: 1 was not found");
    }

    @Test
    void saveSuccess() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.save(course1)).thenReturn(new ServiceResponse(course1, ""));
        var result = controller.createCourse(course1);

        assertEquals(result.getStatusCode(), HttpStatus.CREATED);
        assertEquals(((Course)result.getBody()).getId(), 1);
        assertEquals(((Course)result.getBody()).getName(), "Course1");
    }

    @Test
    void saveErrorExistingStudent() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.save(course1)).thenReturn(new ServiceResponse(null, "A course with name: Course1 already exists"));
        var result = controller.createCourse(course1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A course with name: Course1 already exists");
    }

    @Test
    void saveErrorMoreThanFiftyStudents() {
        var students = new HashSet<Student>();
        for(var i = 0; i < 51; i++){
            var student = new Student();
            student.setId(i);
            student.setSchoolId(Integer.toString(i));
            student.setName("Student" + i);
            students.add(student);
        }

        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");
        course1.setStudents(students);

        when(courseService.save(course1)).thenReturn(new ServiceResponse(null, "A course cannot have to more than 50 students"));
        var result = controller.createCourse(course1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A course cannot have to more than 50 students");
    }

    @Test
    void updateSuccess() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.update(1, course1)).thenReturn(new ServiceResponse(course1, ""));
        var result = controller.updateCourse(1, course1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(((Course)result.getBody()).getId(), 1);
        assertEquals(((Course)result.getBody()).getName(), "Course1");
    }

    @Test
    void upateErrorExistingStudent() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.update(1, course1)).thenReturn(new ServiceResponse(null, "A course with name: Course1 already exists"));
        var result = controller.updateCourse(1, course1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A course with name: Course1 already exists");
    }

    @Test
    void updateErrorMoreThanFiftyStudents() {
        var students = new HashSet<Student>();
        for(var i = 0; i < 51; i++){
            var student = new Student();
            student.setId(i);
            student.setSchoolId(Integer.toString(i));
            student.setName("Student" + i);
            students.add(student);
        }

        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");
        course1.setStudents(students);

        when(courseService.update(1, course1)).thenReturn(new ServiceResponse(null, "A course cannot have to more than 50 students"));
        var result = controller.updateCourse(1, course1);

        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(result.getBody(), "A course cannot have to more than 50 students");
    }

    @Test
    void deleteTest(){
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseService.findById(1)).thenReturn(java.util.Optional.of(course1));
        var result = controller.deleteCourse(1);

        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

}
