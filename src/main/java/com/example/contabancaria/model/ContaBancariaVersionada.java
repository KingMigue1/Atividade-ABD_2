package com.example.contabancaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas_bancarias_versionadas")
public class ContaBancariaVersionada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome do cliente é obrigatório")
    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;
    
    @NotNull(message = "Saldo é obrigatório")
    @Column(nullable = false)
    private Float saldo;
    
    @Version
    @Column(name = "data_movimento", nullable = false)
    private LocalDateTime dataMovimento;
    
    // Construtores
    public ContaBancariaVersionada() {
        this.dataMovimento = LocalDateTime.now();
    }
    
    public ContaBancariaVersionada(String nomeCliente, Float saldo) {
        this.nomeCliente = nomeCliente;
        this.saldo = saldo;
        this.dataMovimento = LocalDateTime.now();
    }
    
    // Métodos de negócio
    public void deposita(Float valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser maior que zero");
        }
        this.saldo += valor;
        this.dataMovimento = LocalDateTime.now();
    }
    
    public void retirada(Float valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor da retirada deve ser maior que zero");
        }
        if (valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para a retirada");
        }
        this.saldo -= valor;
        this.dataMovimento = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomeCliente() {
        return nomeCliente;
    }
    
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
    
    public Float getSaldo() {
        return saldo;
    }
    
    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }
    
    public LocalDateTime getDataMovimento() {
        return dataMovimento;
    }
    
    public void setDataMovimento(LocalDateTime dataMovimento) {
        this.dataMovimento = dataMovimento;
    }
    
    @Override
    public String toString() {
        return "ContaBancariaVersionada{" +
                "id=" + id +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", saldo=" + saldo +
                ", dataMovimento=" + dataMovimento +
                '}';
    }
} 