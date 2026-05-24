package com.caslab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// Entity untuk item yang tersedia di toko dalam game.
@Entity
@Table(name = "item_shop")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemShop {

    public enum EfekItem {
        SPEED_BOOST, TIME_BONUS, SHIELD, COIN_MAGNET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    @Column(name = "nama_item", nullable = false, length = 100)
    private String namaItem;

    @Column(name = "deskripsi")
    private String deskripsi;

    @Column(name = "harga_koin", nullable = false)
    private Integer hargaKoin;

    @Enumerated(EnumType.STRING)
    @Column(name = "efek_item", length = 50)
    private EfekItem efekItem;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentItem> studentItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isAvailable == null) isAvailable = true;
    }
}
