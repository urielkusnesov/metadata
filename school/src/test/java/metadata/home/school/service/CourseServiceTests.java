package metadata.home.school.service;

import metadata.home.school.model.Course;
import metadata.home.school.model.Student;
import metadata.home.school.repository.CourseRepository;
import metadata.home.school.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CourseServiceTests {
    StudentRepository studentRepository = mock(StudentRepository.class);
    CourseRepository courseRepository = mock(CourseRepository.class);
    CourseService service = new CourseService(courseRepository, studentRepository);

    @Test
    void findByIdGetResult() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");
        when(courseRepository.findById(1)).thenReturn(java.util.Optional.of(course));
        var result = service.findById(1);

        assertEquals(result.get().getId(), 1);
        assertEquals(result.get().getName(), "Course1");
    }

    @Test
    void findByIdNoResult() {
        var course = new Course();
        course.setId(1);
        course.setName("Course1");
        when(courseRepository.findById(1)).thenReturn(null);
        var result = service.findById(1);

        assertEquals(result, null);
    }

    @Test
    void findAll() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        var course2 = new Course();
        course2.setId(2);
        course2.setName("Course2");

        when(courseRepository.findAll()).thenReturn(new ArrayList<>(){{add(course1); add(course2);}});
        var result = service.findAll();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getId(), 1);
        assertEquals(result.get(1).getId(), 2);
    }

    @Test
    void findAllEmpty() {
        when(courseRepository.findAll()).thenReturn(new ArrayList<>());
        var result = service.findAll();

        assertEquals(result.size(), 0);
    }

    @Test
    void findWithNoStudents() {
        var student = new Student();
        student.setId(1);
        student.setSchoolId("A");
        student.setName("StudentA");

        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");
        course1.setStudents(new HashSet<>(){{add(student);}});

        var course2 = new Course();
        course2.setId(2);
        course2.setName("Course2");

        when(courseRepository.findByStudentsIsEmpty()).thenReturn(new ArrayList<>(){{add(course2);}});
        var result = service.findWithNoStudents();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 2);
    }

    @Test
    void saveSuccess() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseRepository.findByName("Course1")).thenReturn(null);
        when(courseRepository.save(any())).thenReturn(course1);
        var result = service.save(course1);

        assertEquals(((Course)result.getResult()).getId(), 1);
        assertEquals(result.getMessage(), "");
    }

    @Test
    void saveErrorExistingCourse() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        when(courseRepository.findByName("Course1")).thenReturn(course1);
        var result = service.save(course1);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A course with name: Course1 already exists");
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

        when(courseRepository.findByName("Course1")).thenReturn(null);
        when(studentRepository.findBySchoolId(any())).thenReturn(students.iterator().next());
        var result = service.save(course1);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A course cannot have to more than 50 students");
    }

    @Test
    void updateSuccess() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        var course2 = new Course();
        course2.setId(1);
        course2.setName("Course1 updated");

        when(courseRepository.findById(1)).thenReturn(java.util.Optional.of(course1));
        when(courseRepository.findByName("Course1")).thenReturn(course1);
        when(courseRepository.save(any())).thenReturn(course2);
        var result = service.update(1, course2);

        assertEquals(((Course)result.getResult()).getId(), 1);
        assertEquals(((Course)result.getResult()).getName(), "Course1 updated");
        assertEquals(result.getMessage(), "");
    }

    @Test
    void upateErrorExistingCourse() {
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        var course2 = new Course();
        course2.setId(1);
        course2.setName("new name");

        var course3 = new Course();
        course3.setId(2);
        course3.setName("new name");

        when(courseRepository.findById(1)).thenReturn(java.util.Optional.of(course1));
        when(courseRepository.findByName("new name")).thenReturn(course3);
        var result = service.update(1, course2);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A course with name: new name already exists");
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

        var course2 = new Course();
        course2.setId(1);
        course2.setName("Course1");
        course2.setStudents(students);

        when(courseRepository.findById(1)).thenReturn(java.util.Optional.of(course1));
        when(courseRepository.findByName("Course1")).thenReturn(course1);
        when(studentRepository.findBySchoolId(any())).thenReturn(students.iterator().next());
        var result = service.update(1, course2);

        assertEquals(result.getResult(), null);
        assertEquals(result.getMessage(), "A course cannot have to more than 50 students");
    }

    @Test
    void deleteTest(){
        var course1 = new Course();
        course1.setId(1);
        course1.setName("Course1");

        assertDoesNotThrow(() -> service.delete(course1));
    }
}
