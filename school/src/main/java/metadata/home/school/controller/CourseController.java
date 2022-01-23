package metadata.home.school.controller;

import metadata.home.school.exception.ResourceNotFoundException;
import metadata.home.school.model.Course;
import metadata.home.school.model.ServiceResponse;
import metadata.home.school.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CourseController {
    private CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService){
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseService.findAll();
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable(value = "id") Integer id) {
        var course = courseService.findById(id);
        if (course != null){
            return new ResponseEntity<>(course.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Course with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/courses/{id}/students")
    public ResponseEntity<?> getStudentsFromCourse(@PathVariable(value = "id") Integer id) {
        var cuorse = courseService.findById(id);
        if (cuorse != null){
            return new ResponseEntity<>(cuorse.get().getStudents(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Course with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/courses/noStudents")
    public List<Course> getCoursesWithNoStudents() {
        return courseService.findWithNoStudents();
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        var savedCourse = courseService.save(course);

        if(savedCourse.getResult() != null){
            return new ResponseEntity<>(savedCourse.getResult(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(savedCourse.getMessage(), HttpStatus.CONFLICT);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable(value = "id") Integer id, @RequestBody Course course) {
        ServiceResponse updatedCourse;
        try{
            updatedCourse = courseService.update(id, course);
        }catch (ResourceNotFoundException ex){
            return new ResponseEntity<>("Student with id: " + id +  " was not found", HttpStatus.NOT_FOUND);
        }

        if(updatedCourse.getResult() != null){
            return new ResponseEntity<>(updatedCourse.getResult(), HttpStatus.OK);
        }
        return new ResponseEntity<>(updatedCourse.getMessage(), HttpStatus.CONFLICT);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable(value = "id") Integer id) {
        Course course = courseService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseService.delete(course);

        return ResponseEntity.ok().build();
    }
}
