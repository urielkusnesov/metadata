package metadata.home.school.repository;

import metadata.home.school.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByName(String name);
    List<Course> findByStudentsIsEmpty();
}
