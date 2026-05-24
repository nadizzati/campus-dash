package com.caslab.repository;

import com.caslab.entity.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repo untuk operasi database StudentAccount.

@Repository
public interface StudentAccountRepository extends JpaRepository<StudentAccount, Long> {

    Optional<StudentAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    // Leaderboard berdasarkan total koin terkumpul
    @Query("SELECT s FROM StudentAccount s ORDER BY s.totalKoinTerkumpul DESC")
    List<StudentAccount> findTopByOrderByTotalKoin();

    // Update total koin setelah sesi game selesai
    @Modifying
    @Query("UPDATE StudentAccount s SET s.totalKoinTerkumpul = s.totalKoinTerkumpul + :tambahan WHERE s.idStudent = :id")
    int tambahKoin(@Param("id") Long idStudent, @Param("tambahan") int tambahan);
}
