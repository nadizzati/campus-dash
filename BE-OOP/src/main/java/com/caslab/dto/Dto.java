package com.caslab.dto;

import com.caslab.entity.TaskSubmission.StatusTugas;
import lombok.*;
import java.time.LocalDateTime;

// DTO (Data Transfer Object) untuk request + response API Campus Dash

public class Dto {

    // Request registrasi akun baru
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        private String username;
        private String password;
    }

    // Request submit hasil sesi game
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GameResultRequest {
        private Long idStudent;
        private StatusTugas statusTugas;
        private Integer waktuTersisa;
        private Integer koinDikumpulkan;
        private Integer level;
    }

    // Request beli item dari shop
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BuyItemRequest {
        private Long idStudent;
        private Long idItem;
    }


    // Response data akun mahasiswa
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StudentResponse {
        private Long idStudent;
        private String username;
        private Integer totalKoinTerkumpul;
        private LocalDateTime createdAt;
    }

    // Response sesi game (task submission)
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TaskSubmissionResponse {
        private Long idSesi;
        private Long idStudent;
        private String username;
        private StatusTugas statusTugas;
        private Integer waktuTersisa;
        private Integer koinDikumpulkan;
        private Integer level;
        private LocalDateTime createdAt;
        private LocalDateTime finishedAt;
    }

    // Response item shop
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemShopResponse {
        private Long idItem;
        private String namaItem;
        private String deskripsi;
        private Integer hargaKoin;
        private String efekItem;
        private Boolean isAvailable;
    }

    // Response entry leaderboard
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LeaderboardEntry {
        private Integer rank;
        private Long idStudent;
        private String username;
        private Integer totalKoin;
        private Long totalSesiCompleted;
    }

    // Response generik API
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> ok(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }
    }

    // Login Request
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
