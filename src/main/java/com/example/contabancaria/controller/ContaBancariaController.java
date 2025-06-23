package com.example.contabancaria.controller;

import com.example.contabancaria.model.ContaBancaria;
import com.example.contabancaria.service.ContaBancariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = "*")
public class ContaBancariaController {
    
    @Autowired
    private ContaBancariaService service;
    
    @GetMapping
    public List<ContaBancaria> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ContaBancaria create(@RequestBody ContaBancaria conta) {
        return service.save(conta);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContaBancaria> update(@PathVariable Long id, @RequestBody ContaBancaria conta) {
        return service.findById(id)
                .map(existingConta -> {
                    conta.setId(id);
                    return ResponseEntity.ok(service.save(conta));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/deposita")
    public ResponseEntity<ContaBancaria> deposita(@PathVariable Long id, @RequestParam Float valor) {
        try {
            ContaBancaria conta = service.deposita(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/retirada")
    public ResponseEntity<ContaBancaria> retirada(@PathVariable Long id, @RequestParam Float valor) {
        try {
            ContaBancaria conta = service.retirada(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 