package com.micro.account.controller;

import com.micro.account.dto.CreateDepartmentRequest;
import com.micro.account.dto.CreateLaboratoryRequest;
import com.micro.account.dto.DepartmentResponse;
import com.micro.account.dto.LaboratoryResponse;
import com.micro.account.entity.Department;
import com.micro.account.entity.Laboratory;
import com.micro.account.repository.DepartmentRepository;
import com.micro.account.repository.LaboratoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StructuresController {

    private final DepartmentRepository departmentRepository;
    private final LaboratoryRepository laboratoryRepository;

    // --- Public (authentifie) ---
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> listDepartments() {
        List<DepartmentResponse> res = departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse(d.getId(), d.getName(), d.getDescription()))
                .toList();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/laboratories")
    public ResponseEntity<List<LaboratoryResponse>> listLaboratories() {
        List<LaboratoryResponse> res = laboratoryRepository.findAll().stream()
                .map(l -> new LaboratoryResponse(l.getId(), l.getName(), l.getDescription()))
                .toList();
        return ResponseEntity.ok(res);
    }

    // --- Admin only (creation / maj) ---
    @PostMapping("/admin/departments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest req) {
        if (departmentRepository.existsByNameIgnoreCase(req.name())) {
            throw new IllegalStateException("Departement existe deja.");
        }
        Department entity = Department.builder()
                .name(req.name())
                .description(req.description())
                .build();
        Department saved = departmentRepository.save(entity);
        return ResponseEntity.ok(new DepartmentResponse(saved.getId(), saved.getName(), saved.getDescription()));
    }

    @PutMapping("/admin/departments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable UUID id, @Valid @RequestBody CreateDepartmentRequest req) {
        Department dep = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Departement introuvable."));
        dep.setName(req.name());
        dep.setDescription(req.description());
        Department saved = departmentRepository.save(dep);
        return ResponseEntity.ok(new DepartmentResponse(saved.getId(), saved.getName(), saved.getDescription()));
    }

    @PostMapping("/admin/laboratories")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
    public ResponseEntity<LaboratoryResponse> createLaboratory(@Valid @RequestBody CreateLaboratoryRequest req) {
        if (laboratoryRepository.existsByNameIgnoreCase(req.name())) {
            throw new IllegalStateException("Laboratoire existe deja.");
        }
        Laboratory entity = Laboratory.builder()
                .name(req.name())
                .description(req.description())
                .build();
        Laboratory saved = laboratoryRepository.save(entity);
        return ResponseEntity.ok(new LaboratoryResponse(saved.getId(), saved.getName(), saved.getDescription()));
    }

    @PutMapping("/admin/laboratories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERUSER')")
    public ResponseEntity<LaboratoryResponse> updateLaboratory(@PathVariable UUID id, @Valid @RequestBody CreateLaboratoryRequest req) {
        Laboratory lab = laboratoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Laboratoire introuvable."));
        lab.setName(req.name());
        lab.setDescription(req.description());
        Laboratory saved = laboratoryRepository.save(lab);
        return ResponseEntity.ok(new LaboratoryResponse(saved.getId(), saved.getName(), saved.getDescription()));
    }
}
