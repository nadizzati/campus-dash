package com.caslab.service;

import com.caslab.dto.Dto.*;
import com.caslab.entity.ItemShop;
import com.caslab.entity.StudentAccount;
import com.caslab.entity.StudentItem;
import com.caslab.repository.ItemShopRepository;
import com.caslab.repository.StudentItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service untuk manajemen item shop.
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemShopService {

    private final ItemShopRepository itemRepo;
    private final StudentItemRepository studentItemRepo;
    private final StudentAccountService studentService;

    // Ambil semua item yang tersedia
    @Transactional(readOnly = true)
    public List<ItemShopResponse> getAvailableItems() {
        return itemRepo.findByIsAvailableTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // Ambil semua item (termasuk tidak tersedia) - admin
    @Transactional(readOnly = true)
    public List<ItemShopResponse> getAllItems() {
        return itemRepo.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // Tambah item baru
    @Transactional
    public ItemShopResponse createItem(ItemShopResponse req) {
        ItemShop item = ItemShop.builder()
                .namaItem(req.getNamaItem())
                .deskripsi(req.getDeskripsi())
                .hargaKoin(req.getHargaKoin())
                .efekItem(req.getEfekItem() != null ?
                        ItemShop.EfekItem.valueOf(req.getEfekItem()) : null)
                .isAvailable(true)
                .build();
        return toResponse(itemRepo.save(item));
    }

    // Update item
    @Transactional
    public ItemShopResponse updateItem(Long idItem, ItemShopResponse req) {
        ItemShop item = itemRepo.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Item id=" + idItem + " tidak ditemukan."));
        item.setNamaItem(req.getNamaItem());
        item.setDeskripsi(req.getDeskripsi());
        item.setHargaKoin(req.getHargaKoin());
        item.setIsAvailable(req.getIsAvailable());
        return toResponse(itemRepo.save(item));
    }

    // Hapus item
    @Transactional
    public void deleteItem(Long idItem) {
        itemRepo.deleteById(idItem);
    }

    // Mahasiswa membeli item dengan koin
    @Transactional
    public ApiResponse<String> buyItem(BuyItemRequest req) {
        if (studentItemRepo.existsByStudentIdStudentAndItemIdItem(req.getIdStudent(), req.getIdItem())) {
            return ApiResponse.error("Item sudah dimiliki.");
        }

        StudentAccount student = studentService.findOrThrow(req.getIdStudent());
        ItemShop item = itemRepo.findById(req.getIdItem())
                .orElseThrow(() -> new IllegalArgumentException("Item tidak ditemukan."));

        if (student.getTotalKoinTerkumpul() < item.getHargaKoin()) {
            return ApiResponse.error("Koin tidak cukup. Butuh " + item.getHargaKoin() +
                    ", punya " + student.getTotalKoinTerkumpul());
        }

        // Kurangi koin mahasiswa
        studentService.tambahKoin(req.getIdStudent(), -item.getHargaKoin());

        // Catat pembelian
        StudentItem si = StudentItem.builder().student(student).item(item).build();
        studentItemRepo.save(si);

        log.info("Pembelian berhasil: student={}, item={}", student.getUsername(), item.getNamaItem());
        return ApiResponse.ok("Item berhasil dibeli: " + item.getNamaItem(), null);
    }

    // Item yang dimiliki mahasiswa
    @Transactional(readOnly = true)
    public List<ItemShopResponse> getStudentItems(Long idStudent) {
        return studentItemRepo.findByStudentIdStudent(idStudent).stream()
                .map(si -> toResponse(si.getItem()))
                .collect(Collectors.toList());
    }

    // Helper
    private ItemShopResponse toResponse(ItemShop i) {
        return ItemShopResponse.builder()
                .idItem(i.getIdItem())
                .namaItem(i.getNamaItem())
                .deskripsi(i.getDeskripsi())
                .hargaKoin(i.getHargaKoin())
                .efekItem(i.getEfekItem() != null ? i.getEfekItem().name() : null)
                .isAvailable(i.getIsAvailable())
                .build();
    }
}
