package com.caslab.repository;

import com.caslab.entity.ItemShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repo untuk operasi database ItemShop.

@Repository
public interface ItemShopRepository extends JpaRepository<ItemShop, Long> {

    List<ItemShop> findByIsAvailableTrue();

    List<ItemShop> findByHargaKoinLessThanEqual(int maxHarga);
}
