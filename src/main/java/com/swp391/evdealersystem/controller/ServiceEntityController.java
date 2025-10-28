package com.swp391.evdealersystem.controller;


import com.swp391.evdealersystem.dto.request.ServiceRequest;
import com.swp391.evdealersystem.dto.response.ServiceResponse;
import com.swp391.evdealersystem.service.ServiceEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/service-items")
public class ServiceEntityController {

    private final ServiceEntityService service;

    @GetMapping
    public Page<ServiceResponse> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable p) {
        return service.list(p);
    }

    @GetMapping("/{id}")
    public ServiceResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse create(@Valid @RequestBody ServiceRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

