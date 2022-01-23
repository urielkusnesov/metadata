package metadata.home.school.controller;

import metadata.home.school.exception.ResourceNotFoundException;
import metadata.home.school.model.Course;
import metadata.home.school.model.ServiceResponse;
import metadata.home.school.model.Student;
import metadata.home.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    private StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable(value = "id") Integer id) {
        var student = studentService.findById(id);
        if (student != null){
            return new ResponseEntity<>(student.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/students/{id}/courses")
    public ResponseEntity<?> getCoursesFromStudent(@PathVariable(value = "id") Integer id) {
        var student = studentService.findById(id);
        if (student != null){
            return new ResponseEntity<>(student.get().getCourses(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/students/noCourses")
    public List<Student> getStudentsWithNoCourse() {
        return studentService.findWithNoCourse();
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        var savedStudent = studentService.save(student);

        if(savedStudent.getResult() != null){
            return new ResponseEntity<>(savedStudent.getResult(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(savedStudent.getMessage(), HttpStatus.CONFLICT);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable(value = "id") Integer id, @RequestBody Student student) {
        ServiceResponse updatedStudent;
        try{
            updatedStudent = studentService.update(id, student);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
        }

        if(updatedStudent.getResult() != null){
            return new ResponseEntity<>(updatedStudent.getResult(), HttpStatus.OK);
        }
        return new ResponseEntity<>(updatedStudent.getMessage(), HttpStatus.CONFLICT);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable(value = "id") Integer id) {
        var student = studentService.findById(id);
        if(student.get() != null){
            studentService.delete(student.get());
            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/students/{id}/register")
    public ResponseEntity<?> register(@PathVariable(value = "id") Integer id, @RequestBody List<Course> courses) {
        try{
            var updatedStudent = studentService.register(id, courses);

            if(updatedStudent.getResult() != null){
                return new ResponseEntity<>(updatedStudent.getResult(), HttpStatus.OK);
            }
            return new ResponseEntity<>(updatedStudent.getMessage(), HttpStatus.CONFLICT);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
        }
    }
}
