package metadata.home.school.service;

import metadata.home.school.exception.ResourceNotFoundException;
import metadata.home.school.model.Course;
import metadata.home.school.model.ServiceResponse;
import metadata.home.school.model.Student;
import metadata.home.school.repository.CourseRepository;
import metadata.home.school.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    private CourseRepository courseRepository;
    private StudentRepository studentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository){
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public List<Course> findAll(){
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Integer id){
        return courseRepository.findById(id);
    }

    public List<Course> findWithNoStudents(){
        return courseRepository.findByStudentsIsEmpty();
    }

    public ServiceResponse save(Course course){
        if(courseRepository.findByName(course.getName()) != null){
            return new ServiceResponse(null, "A course with name: " + course.getName() + " already exists");
        }

        if(course.getStudents().size() > 50){
            return new ServiceResponse(null, "A course cannot have to more than 50 students");
        }

        Set<Student> students = new HashSet<>();
        for (var student: course.getStudents()) {
            var newStudent = studentRepository.findBySchoolId(student.getSchoolId());
            if(newStudent != null){
                students.add(newStudent);
            }
        }
        course.setStudents(students);

        return new ServiceResponse(courseRepository.save(course), "");
    }

    public ServiceResponse update(Integer id, Course course){
        Course currentCourse = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        var existingCourse = courseRepository.findByName(course.getName());
        if(existingCourse != null && existingCourse.getId() != currentCourse.getId()){
            return new ServiceResponse(null, "A course with name: " + course.getName() + " already exists");
        }

        if(course.getStudents().size() > 50){
            return new ServiceResponse(null, "A course cannot have to more than 50 students");
        }

        currentCourse.setName(course.getName());
        currentCourse.setStudents(new HashSet<>());
        for (var student: course.getStudents()) {
            addStudent(currentCourse, student);
        }

        return new ServiceResponse(courseRepository.save(currentCourse), "");
    }

    public void delete(Course course){
        courseRepository.delete(course);
    }

    public boolean addStudent(Course course, Student student){
        var newStudent = studentRepository.findBySchoolId(student.getSchoolId());
        if(newStudent != null){
            course.getStudents().add(newStudent);
            return true;
        }
        return false;
    }
}
