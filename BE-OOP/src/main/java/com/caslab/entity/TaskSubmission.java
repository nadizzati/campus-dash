package com.caslab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Entity untuk sesi game yang disubmit.

@Entity
@Table(name = "task_submissions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TaskSubmission {

    public enum StatusTugas {
        IN_PROGRESS, COMPLETED, FAILED, TIMEOUT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesi")
    private Long idSesi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private StudentAccount student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_tugas", nullable = false, length = 20)
    private StatusTugas statusTugas;

    @Column(name = "waktu_tersisa", nullable = false)
    private Integer waktuTersisa;   // detik tersisa saat selesai/gagal

    @Column(name = "koin_dikumpulkan")
    private Integer koinDikumpulkan = 0;

    @Column(name = "level")
    private Integer level = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (koinDikumpulkan == null) koinDikumpulkan = 0;
        if (level == null) level = 1;
    }
}
