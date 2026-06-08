package com.caslab.service;

import com.caslab.dto.Dto.*;
import com.caslab.entity.StudentAccount;
import com.caslab.repository.StudentAccountRepository;
import com.caslab.repository.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentAccountService {

    private final StudentAccountRepository studentRepo;
    private final TaskSubmissionRepository taskRepo;

    @Transactional
    public StudentResponse register(RegisterRequest req) {
        if (studentRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username '" + req.getUsername() + "' sudah terdaftar.");
        }

        StudentAccount student = StudentAccount.builder()
                .username(req.getUsername())
                .passwordHash("hashed_" + req.getPassword())
                .totalKoinTerkumpul(0)
                .build();

        StudentAccount saved = studentRepo.save(student);
        log.info("Akun baru terdaftar: {}", saved.getUsername());
        return toResponse(saved);
    }

    @Transactional
    public StudentResponse login(LoginRequest req) {
        StudentAccount account = studentRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username tidak ditemukan."));

        String expectedHash = "hashed_" + req.getPassword();
        if (!account.getPasswordHash().equals(expectedHash)) {
            throw new IllegalArgumentException("Password salah.");
        }

        log.info("Login berhasil: {}", account.getUsername());
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return studentRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard() {
        List<StudentAccount> students = studentRepo.findTopByOrderByTotalKoin();
        AtomicInteger rank = new AtomicInteger(1);

        return students.stream().map(s -> LeaderboardEntry.builder()
                .rank(rank.getAndIncrement())
                .idStudent(s.getIdStudent())
                .username(s.getUsername())
                .totalKoin(s.getTotalKoinTerkumpul())
                .totalSesiCompleted(taskRepo.countCompletedByStudent(s.getIdStudent()))
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public StudentResponse tambahKoin(Long idStudent, int tambahan) {
        findOrThrow(idStudent);
        studentRepo.tambahKoin(idStudent, tambahan);
        return toResponse(studentRepo.findById(idStudent).orElseThrow());
    }

    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        studentRepo.deleteById(id);
        log.info("Akun mahasiswa id={} dihapus.", id);
    }

    public StudentAccount findOrThrow(Long id) {
        return studentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mahasiswa id=" + id + " tidak ditemukan."));
    }

    private StudentResponse toResponse(StudentAccount s) {
        return StudentResponse.builder()
                .idStudent(s.getIdStudent())
                .username(s.getUsername())
                .totalKoinTerkumpul(s.getTotalKoinTerkumpul())
                .createdAt(s.getCreatedAt())
                .build();
    }
}