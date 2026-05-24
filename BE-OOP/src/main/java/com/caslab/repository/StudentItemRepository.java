package com.caslab.repository;

import com.caslab.entity.StudentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository untuk item yang dimiliki mahasiswa.

@Repository
public interface StudentItemRepository extends JpaRepository<StudentItem, Long> {

    List<StudentItem> findByStudentIdStudent(Long idStudent);

    boolean existsByStudentIdStudentAndItemIdItem(Long idStudent, Long idItem);
}
