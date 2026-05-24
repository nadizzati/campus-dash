package com.caslab.controller;

import com.caslab.dto.Dto.*;
import com.caslab.service.ItemShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** REST Controller untuk item shop.
 *  Base URL: /api/items */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemShopController {

    private final ItemShopService itemService;

    // GET /api/items - Semua item tersedia
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemShopResponse>>> getAvailable() {
        return ResponseEntity.ok(ApiResponse.ok("OK", itemService.getAvailableItems()));
    }

    // GET /api/items/all - Semua item termasuk unavailable (admin)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ItemShopResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("OK", itemService.getAllItems()));
    }

    // POST /api/items - Tambah item baru (admin)
    @PostMapping
    public ResponseEntity<ApiResponse<ItemShopResponse>> create(@RequestBody ItemShopResponse req) {
        return ResponseEntity.ok(ApiResponse.ok("Item ditambahkan.", itemService.createItem(req)));
    }

    // PUT /api/items/{idItem} - Update item (admin)
    @PutMapping("/{idItem}")
    public ResponseEntity<ApiResponse<ItemShopResponse>> update(
            @PathVariable Long idItem, @RequestBody ItemShopResponse req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Item diupdate.", itemService.updateItem(idItem, req)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/items/{idItem} - Hapus item (admin)
    @DeleteMapping("/{idItem}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long idItem) {
        itemService.deleteItem(idItem);
        return ResponseEntity.ok(ApiResponse.ok("Item dihapus.", null));
    }

    // POST /api/items/buy - Beli item
    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<String>> buy(@RequestBody BuyItemRequest req) {
        return ResponseEntity.ok(itemService.buyItem(req));
    }

    // GET /api/items/student/{idStudent} - Item milik mahasiswa
    @GetMapping("/student/{idStudent}")
    public ResponseEntity<ApiResponse<List<ItemShopResponse>>> studentItems(@PathVariable Long idStudent) {
        return ResponseEntity.ok(ApiResponse.ok("OK", itemService.getStudentItems(idStudent)));
    }
}
