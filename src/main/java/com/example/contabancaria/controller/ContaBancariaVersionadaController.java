package com.example.contabancaria.controller;

import com.example.contabancaria.model.ContaBancariaVersionada;
import com.example.contabancaria.service.ContaBancariaVersionadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas-versionadas")
@CrossOrigin(origins = "*")
public class ContaBancariaVersionadaController {
    
    @Autowired
    private ContaBancariaVersionadaService service;
    
    @GetMapping
    public List<ContaBancariaVersionada> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContaBancariaVersionada> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ContaBancariaVersionada create(@RequestBody ContaBancariaVersionada conta) {
        return service.save(conta);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContaBancariaVersionada> update(@PathVariable Long id, @RequestBody ContaBancariaVersionada conta) {
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
    public ResponseEntity<ContaBancariaVersionada> deposita(@PathVariable Long id, @RequestParam Float valor) {
        try {
            ContaBancariaVersionada conta = service.deposita(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/retirada")
    public ResponseEntity<ContaBancariaVersionada> retirada(@PathVariable Long id, @RequestParam Float valor) {
        try {
            ContaBancariaVersionada conta = service.retirada(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 