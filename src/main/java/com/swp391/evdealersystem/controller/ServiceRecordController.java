package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ServiceRecordRequest;
import com.swp391.evdealersystem.dto.request.ServiceRecordUpdateRequest;
import com.swp391.evdealersystem.dto.response.ServiceRecordResponse;
import com.swp391.evdealersystem.entity.ServiceRecord;
import com.swp391.evdealersystem.service.ServiceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*; import org.springframework.data.web.PageableDefault;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/service-records") @RequiredArgsConstructor
public class ServiceRecordController {
    private final ServiceRecordService service;

    @GetMapping
    public Page<ServiceRecordResponse> list(@PageableDefault(size=20, sort="createdAt", direction=Sort.Direction.DESC) Pageable p){
        return service.list(p);
    }

    @GetMapping("/{id}") public ServiceRecordResponse get(@PathVariable Long id){ return service.get(id); }

    @GetMapping("/by-customer/{customerId}")
    public Page<ServiceRecordResponse> byCustomer(@PathVariable Long customerId, @PageableDefault(size=20) Pageable p){
        return service.byCustomer(customerId, p);
    }

    @GetMapping("/by-service/{itemId}")
    public Page<ServiceRecordResponse> byService(@PathVariable Long itemId, @PageableDefault(size=20) Pageable p){
        return service.byServiceItem(itemId, p);
    }

    @GetMapping("/by-user/{userId}")
    public Page<ServiceRecordResponse> byUser(@PathVariable Long userId, @PageableDefault(size=20) Pageable p){
        return service.byUser(userId, p);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ServiceRecordResponse create(@Valid @RequestBody ServiceRecordRequest req){ return service.create(req); }

    @PutMapping("/{id}")
    public ServiceRecordResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRecordUpdateRequest req){
        return service.update(id, req);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){ service.delete(id); }
}
