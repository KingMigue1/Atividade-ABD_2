package com.example.contabancaria.service;

import com.example.contabancaria.model.ContaBancaria;
import com.example.contabancaria.repository.ContaBancariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContaBancariaService {
    
    @Autowired
    private ContaBancariaRepository repository;
    
    public List<ContaBancaria> findAll() {
        return repository.findAll();
    }
    
    public Optional<ContaBancaria> findById(Long id) {
        return repository.findById(id);
    }
    
    public ContaBancaria save(ContaBancaria conta) {
        return repository.save(conta);
    }
    
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Transactional
    public ContaBancaria deposita(Long id, Float valor) {
        ContaBancaria conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        
        conta.deposita(valor);
        return repository.save(conta);
    }
    
    @Transactional
    public ContaBancaria retirada(Long id, Float valor) {
        ContaBancaria conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        
        conta.retirada(valor);
        return repository.save(conta);
    }
} 