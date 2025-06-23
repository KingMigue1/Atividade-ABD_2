package com.example.contabancaria.service;

import com.example.contabancaria.model.ContaBancariaVersionada;
import com.example.contabancaria.repository.ContaBancariaVersionadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContaBancariaVersionadaService {
    
    @Autowired
    private ContaBancariaVersionadaRepository repository;
    
    public List<ContaBancariaVersionada> findAll() {
        return repository.findAll();
    }
    
    public Optional<ContaBancariaVersionada> findById(Long id) {
        return repository.findById(id);
    }
    
    public ContaBancariaVersionada save(ContaBancariaVersionada conta) {
        return repository.save(conta);
    }
    
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    @Transactional
    public ContaBancariaVersionada deposita(Long id, Float valor) {
        ContaBancariaVersionada conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        
        conta.deposita(valor);
        return repository.save(conta);
    }
    
    @Transactional
    public ContaBancariaVersionada retirada(Long id, Float valor) {
        ContaBancariaVersionada conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        
        conta.retirada(valor);
        return repository.save(conta);
    }
} 