package metadata.home.school.service;

import metadata.home.school.model.Course;
import metadata.home.school.repository.CourseRepository;
import metadata.home.school.repository.StudentRepository;
import metadata.home.school.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentServiceTests {
    StudentRepository studentRepository = mock(StudentRepository.class);
    CourseRepository courseRepository = mock(CourseRepository.class);
    StudentService service = new StudentService(studentRepository, courseRepository);

    @Test
    void findByIdGetResult() {
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");
        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student));
        var result = service.findById(1);

        assertEquals(result.get().getId(), 1);
        assertEquals(result.get().getSchoolId(), "A");
        assertEquals(result.get().getName(), "StudentA");
    }

    @Test
    void findByIdNoResult() {
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");
        when(studentRepository.findById(1)).thenReturn(null);
        var result = service.findById(1);

        assertEquals(result, null);
    }

    @Test
    void findAll() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        var student2 = new Student();
        student2.setId(2);
        student2.setSchoolId("B");
        student2.setName("StudentB");

        when(studentRepository.findAll()).thenReturn(new ArrayList<>(){{add(student1); add(student2);}});
        var result = service.findAll();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(1).getId(), 2);
    }

    @Test
    void findAllEmpty() {
        when(studentRepository.findAll()).thenReturn(new ArrayList<>());
        var result = service.findAll();

        assertEquals(result.size(), 0);
    }

    @Test
    void findWithNoCourse() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(new HashSet<>(){{add(course);}});

        var student2 = new Student();
        student2.setId(2);
        student2.setSchoolId("B");
        student2.setName("StudentB");

        when(studentRepository.findByCoursesIsEmpty()).thenReturn(new ArrayList<>(){{add(student2);}});
        var result = service.findWithNoCourse();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 2);
    }

    @Test
    void saveSuccess() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentRepository.findBySchoolId("A")).thenReturn(null);
        when(studentRepository.save(any())).thenReturn(student1);
        var result = service.save(student1);

        assertEquals(((Student)result.getResult()).getId(), 1);
        assertEquals(result.getMessage(), "");
    }

    @Test
    void saveErrorExistingStudent() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentRepository.findBySchoolId("A")).thenReturn(student1);
        var result = service.save(student1);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A student with school id: A already exists");
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

        when(studentRepository.findBySchoolId("A")).thenReturn(null);
        when(courseRepository.findByName(any())).thenReturn(courses.iterator().next());
        var result = service.save(student1);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A student cannot register to more than 5 courses");
    }

    @Test
    void updateSuccess() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        var student2 = new Student();
        student2.setId(1);
        student2.setSchoolId("A");
        student2.setName("StudentA updated");

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        when(studentRepository.findBySchoolId("A")).thenReturn(student1);
        when(studentRepository.save(any())).thenReturn(student2);
        var result = service.update(1, student2);

        assertEquals(((Student)result.getResult()).getId(), 1);
        assertEquals(((Student)result.getResult()).getName(), "StudentA updated");
        assertEquals(result.getMessage(), "");
    }

    @Test
    void upateErrorExistingStudent() {
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        var student2 = new Student();
        student2.setId(1);
        student2.setSchoolId("B");
        student2.setName("StudentA updated to B");

        var student3 = new Student();
        student3.setId(2);
        student3.setSchoolId("B");
        student3.setName("StudentB");

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        when(studentRepository.findBySchoolId("B")).thenReturn(student3);
        var result = service.update(1, student2);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A student with school id: B already exists");
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

        var student2 = new Student();
        student2.setId(1);
        student2.setSchoolId("A");
        student2.setName("StudentA");
        student2.setCourses(courses);

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        when(studentRepository.findBySchoolId("A")).thenReturn(student1);
        when(courseRepository.findByName(any())).thenReturn(courses.iterator().next());
        var result = service.update(1, student2);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A student cannot register to more than 5 courses");
    }

    @Test
    void deleteTest(){
        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        assertDoesNotThrow(() -> service.delete(student1));
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

        var student2 = new Student();
        student2.setId(1);
        student2.setSchoolId("A");
        student2.setName("StudentA");
        student2.setCourses(new HashSet<>(){{add(course);}});

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        when(courseRepository.findByName("Course1")).thenReturn(course);
        when(studentRepository.save(any())).thenReturn(student2);

        var result = service.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(((Student)result.getResult()).getId(), 1);
        assertEquals(((Student)result.getResult()).getCourses().size(), 1);
        assertEquals(((Student)result.getResult()).getCourses().iterator().next().getName(), "Course1");
        assertEquals(result.getMessage(), "");
    }

    @Test
    void registerErrorMoreThanFiveCourses() {
        var courses = new HashSet<Course>();
        for(var i = 0; i < 5; i++){
            var course = new Course();
            course.setId(i);
            course.setName("Course" + i);
            courses.add(course);
        }

        var course = new Course();
        course.setId(6);
        course.setName("Course6");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(courses);

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = service.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A student cannot register to more than 5 courses");
    }

    @Test
    void registerErrorAlreadyExistingCourse() {
        var courses = new HashSet<Course>();
        for(var i = 0; i < 4; i++){
            var course = new Course();
            course.setId(i);
            course.setName("Course" + i);
            courses.add(course);
        }

        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");
        student1.setCourses(courses);

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = service.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "Student was already registered to course: Course1");
    }

    @Test
    void registerErrorCourseNotFound() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");

        var student1 = new Student();
        student1.setId(1);
        student1.setSchoolId("A");
        student1.setName("StudentA");

        when(studentRepository.findById(1)).thenReturn(java.util.Optional.of(student1));
        var result = service.register(1, new ArrayList<>(){{add(course);}});

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "Cannot find course: Course1");
    }
}
