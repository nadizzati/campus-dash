package com.caslab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Entity junction untuk relasi many-to-many antara student dan item yang dibeli

@Entity
@Table(name = "student_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_student", "id_item"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private StudentAccount student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private ItemShop item;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @PrePersist
    protected void onCreate() {
        purchasedAt = LocalDateTime.now();
    }
}
