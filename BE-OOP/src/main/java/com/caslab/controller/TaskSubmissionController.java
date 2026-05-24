package com.caslab.controller;

import com.caslab.dto.Dto.*;
import com.caslab.service.TaskSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** REST Controller untuk sesi game (task submissions)
 *  Base URL: /api/sessions */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskSubmissionController {

    private final TaskSubmissionService taskService;

    // POST /api/sessions/start/{idStudent} - Mulai sesi game
    @PostMapping("/start/{idStudent}")
    public ResponseEntity<ApiResponse<TaskSubmissionResponse>> start(@PathVariable Long idStudent) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Sesi dimulai.", taskService.startSession(idStudent)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/sessions/submit - Submit hasil game
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<TaskSubmissionResponse>> submit(@RequestBody GameResultRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Hasil game disimpan.", taskService.submitResult(req)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/sessions/history/{idStudent} - Riwayat sesi mahasiswa
    @GetMapping("/history/{idStudent}")
    public ResponseEntity<ApiResponse<List<TaskSubmissionResponse>>> history(@PathVariable Long idStudent) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("OK", taskService.getHistory(idStudent)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/sessions/{idSesi} - Detail satu sesi
    @GetMapping("/{idSesi}")
    public ResponseEntity<ApiResponse<TaskSubmissionResponse>> getById(@PathVariable Long idSesi) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("OK", taskService.getById(idSesi)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/sessions - Semua sesi (admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskSubmissionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("OK", taskService.getAll()));
    }
}
