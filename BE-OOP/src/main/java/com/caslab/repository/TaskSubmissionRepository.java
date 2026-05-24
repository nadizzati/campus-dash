package com.caslab.repository;

import com.caslab.entity.TaskSubmission;
import com.caslab.entity.TaskSubmission.StatusTugas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repo untuk operasi database TaskSubmission

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

    List<TaskSubmission> findByStudentIdStudentOrderByCreatedAtDesc(Long idStudent);

    List<TaskSubmission> findByStatusTugas(StatusTugas status);

    Optional<TaskSubmission> findTopByStudentIdStudentAndStatusTugasOrderByCreatedAtDesc(
            Long idStudent, StatusTugas status);

    // total koin dari sesi COMPLETED
    @Query("SELECT COALESCE(SUM(t.koinDikumpulkan), 0) FROM TaskSubmission t " +
           "WHERE t.student.idStudent = :id AND t.statusTugas = 'COMPLETED'")
    Integer sumKoinByStudentCompleted(@Param("id") Long idStudent);

    // Jumlah sesi sukses per mahasiswa
    @Query("SELECT COUNT(t) FROM TaskSubmission t " +
           "WHERE t.student.idStudent = :id AND t.statusTugas = 'COMPLETED'")
    long countCompletedByStudent(@Param("id") Long idStudent);
}
