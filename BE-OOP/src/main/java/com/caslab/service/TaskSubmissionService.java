package com.caslab.service;

import com.caslab.dto.Dto.*;
import com.caslab.entity.StudentAccount;
import com.caslab.entity.TaskSubmission;
import com.caslab.entity.TaskSubmission.StatusTugas;
import com.caslab.repository.TaskSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Service untuk manajemen sesi game (task submissions).

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSubmissionService {

    private final TaskSubmissionRepository taskRepo;
    private final StudentAccountService studentService;

    // Mulai sesi game baru (status IN_PROGRESS)
    @Transactional
    public TaskSubmissionResponse startSession(Long idStudent) {
        StudentAccount student = studentService.findOrThrow(idStudent);

        TaskSubmission sesi = TaskSubmission.builder()
                .student(student)
                .statusTugas(StatusTugas.IN_PROGRESS)
                .waktuTersisa(300) // 5 menit default
                .koinDikumpulkan(0)
                .level(1)
                .build();

        TaskSubmission saved = taskRepo.save(sesi);
        log.info("Sesi game dimulai: idSesi={}, student={}", saved.getIdSesi(), student.getUsername());
        return toResponse(saved);
    }

    // Submit hasil akhir game (COMPLETED / FAILED / TIMEOUT)
    @Transactional
    public TaskSubmissionResponse submitResult(GameResultRequest req) {
        StudentAccount student = studentService.findOrThrow(req.getIdStudent());

        // Cari sesi IN_PROGRESS yang paling baru
        TaskSubmission sesi = taskRepo
                .findTopByStudentIdStudentAndStatusTugasOrderByCreatedAtDesc(
                        req.getIdStudent(), StatusTugas.IN_PROGRESS)
                .orElseGet(() -> TaskSubmission.builder()
                        .student(student)
                        .koinDikumpulkan(0)
                        .level(1)
                        .build());

        sesi.setStatusTugas(req.getStatusTugas());
        sesi.setWaktuTersisa(req.getWaktuTersisa());
        sesi.setKoinDikumpulkan(req.getKoinDikumpulkan());
        sesi.setLevel(req.getLevel());
        sesi.setFinishedAt(LocalDateTime.now());

        TaskSubmission saved = taskRepo.save(sesi);

        // Jika COMPLETED, tambahkan koin ke total akun mahasiswa
        if (req.getStatusTugas() == StatusTugas.COMPLETED && req.getKoinDikumpulkan() > 0) {
            studentService.tambahKoin(req.getIdStudent(), req.getKoinDikumpulkan());
            log.info("Koin ditambahkan: student={}, koin={}", student.getUsername(), req.getKoinDikumpulkan());
        }

        log.info("Sesi game selesai: idSesi={}, status={}", saved.getIdSesi(), saved.getStatusTugas());
        return toResponse(saved);
    }

    // Riwayat sesi game seorang mahasiswa
    @Transactional(readOnly = true)
    public List<TaskSubmissionResponse> getHistory(Long idStudent) {
        studentService.findOrThrow(idStudent);
        return taskRepo.findByStudentIdStudentOrderByCreatedAtDesc(idStudent)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Detail satu sesi berdasarkan ID
    @Transactional(readOnly = true)
    public TaskSubmissionResponse getById(Long idSesi) {
        TaskSubmission sesi = taskRepo.findById(idSesi)
                .orElseThrow(() -> new IllegalArgumentException("Sesi id=" + idSesi + " tidak ditemukan."));
        return toResponse(sesi);
    }

    // Semua sesi game (admin)
    @Transactional(readOnly = true)
    public List<TaskSubmissionResponse> getAll() {
        return taskRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Helper
    private TaskSubmissionResponse toResponse(TaskSubmission t) {
        return TaskSubmissionResponse.builder()
                .idSesi(t.getIdSesi())
                .idStudent(t.getStudent() != null ? t.getStudent().getIdStudent() : null)
                .username(t.getStudent() != null ? t.getStudent().getUsername() : null)
                .statusTugas(t.getStatusTugas())
                .waktuTersisa(t.getWaktuTersisa())
                .koinDikumpulkan(t.getKoinDikumpulkan())
                .level(t.getLevel())
                .createdAt(t.getCreatedAt())
                .finishedAt(t.getFinishedAt())
                .build();
    }
}
