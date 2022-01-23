package metadata.home.school.repository;

import metadata.home.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findBySchoolId(String name);
    List<Student> findByCoursesIsEmpty();
}
