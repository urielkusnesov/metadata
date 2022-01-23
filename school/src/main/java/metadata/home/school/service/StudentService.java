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
public class StudentService {

    private StudentRepository studentRepository;
    private CourseRepository courseRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository){
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Integer id){
        return studentRepository.findById(id);
    }

    public List<Student> findWithNoCourse(){
        return studentRepository.findByCoursesIsEmpty();
    }

    public ServiceResponse save(Student student){
        if(studentRepository.findBySchoolId(student.getSchoolId()) != null){
            return new ServiceResponse(null, "A student with school id: " + student.getSchoolId() + " already exists");
        }

        if(student.getCourses().size() > 5){
            return new ServiceResponse(null, "A student cannot register to more than 5 courses");
        }

        Set<Course> courses = new HashSet<>();
        for (var course: student.getCourses()) {
            var newCourse = courseRepository.findByName(course.getName());
            if(newCourse != null){
                courses.add(newCourse);
            }
        }
        student.setCourses(courses);

        return new ServiceResponse(studentRepository.save(student), "");
    }

    public ServiceResponse update(Integer id, Student student){
        Student currentStudent = studentRepository.findById(id).get();
        var existingStudent = studentRepository.findBySchoolId(student.getSchoolId());
        if(existingStudent != null && existingStudent.getId() != currentStudent.getId()){
            return new ServiceResponse(null, "A student with school id: " + student.getSchoolId() + " already exists");
        }

        if(student.getCourses().size() > 5){
            return new ServiceResponse(null, "A student cannot register to more than 5 courses");
        }

        currentStudent.setName(student.getName());
        currentStudent.setSchoolId(student.getSchoolId());
        currentStudent.setCourses(new HashSet<>());
        for (var course: student.getCourses()) {
            addCourse(currentStudent, course);
        }

        return new ServiceResponse(studentRepository.save(currentStudent), "");
    }

    public void delete(Student student){
        studentRepository.delete(student);
    }

    public ServiceResponse register(Integer id, List<Course> courses){
        var currentStudent = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        if(currentStudent.getCourses().size() >= 5){
            return new ServiceResponse(null, "A student cannot register to more than 5 courses");
        }
        for (var course: courses) {
            if(!currentStudent.getCourses().stream().anyMatch(x -> x.getName().equals(course.getName()))){
                var added = addCourse(currentStudent, course);
                if(!added){
                    return new ServiceResponse(null, "Cannot find course: " + course.getName());
                }
            }else{
                return new ServiceResponse(null, "Student was already registered to course: " + course.getName());
            }
        }

        return new ServiceResponse(studentRepository.save(currentStudent), "");
    }

    private boolean addCourse(Student student, Course course){
        var newCourse = courseRepository.findByName(course.getName());
        if(newCourse != null){
            student.getCourses().add(newCourse);
            return true;
        }
        return false;
    }
}
