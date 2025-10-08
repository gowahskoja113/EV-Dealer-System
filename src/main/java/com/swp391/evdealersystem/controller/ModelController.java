package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ModelRequest;
import com.swp391.evdealersystem.dto.response.ModelResponse;
import com.swp391.evdealersystem.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ModelResponse> create(@RequestBody ModelRequest req) {
        return ResponseEntity.ok(modelService.create(req));
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<ModelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getById(id));
    }

    @GetMapping("/code/{modelCode}")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<ModelResponse> getByCode(@PathVariable String modelCode) {
        return ResponseEntity.ok(modelService.getByCode(modelCode));
    }

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<Page<ModelResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(modelService.list(pageable));
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ModelResponse> update(@PathVariable Long id,
                                                @RequestBody ModelRequest req) {
        return ResponseEntity.ok(modelService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        modelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}