package com.caslab.controller;

import com.caslab.dto.Dto.*;
import com.caslab.service.StudentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** REST Controller untuk manajemen akun mahasiswa
 *  Base URL: /api/students */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentAccountController {

    private final StudentAccountService studentService;

    // POST /api/students/register - Registrasi akun baru
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StudentResponse>> register(@RequestBody RegisterRequest req) {
        try {
            StudentResponse data = studentService.register(req);
            return ResponseEntity.ok(ApiResponse.ok("Registrasi berhasil.", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/students - Semua akun
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("OK", studentService.getAll()));
    }

    // GET /api/students/{id} - Detail satu akun
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("OK", studentService.getById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/students/{id} - Hapus akun
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return ResponseEntity.ok(ApiResponse.ok("Akun dihapus.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/students/leaderboard - Leaderboard top koin
    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> leaderboard() {
        return ResponseEntity.ok(ApiResponse.ok("OK", studentService.getLeaderboard()));
    }
}
