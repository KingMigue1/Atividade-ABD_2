package com.example.contabancaria;

import com.example.contabancaria.model.ContaBancaria;
import com.example.contabancaria.service.ContaBancariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ContaBancariaConcurrencyTest {

    @Autowired
    private ContaBancariaService service;

    private ContaBancaria conta;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        // Criar uma conta com saldo inicial de 1000
        conta = new ContaBancaria("João Silva", 1000.0f);
        conta = service.save(conta);
        successCount.set(0);
        failureCount.set(0);
    }

    @Test
    void testConcurrentDeposits() throws InterruptedException {
        System.out.println("=== Teste de Depósitos Concorrentes ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        
        int numThreads = 10;
        int depositsPerThread = 10;
        float depositAmount = 100.0f;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < depositsPerThread; j++) {
                    try {
                        service.deposita(conta.getId(), depositAmount);
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Depósito " + (j+1) + " realizado com sucesso");
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Falha no depósito " + (j+1) + ": " + e.getMessage());
                    }
                }
            }, executor);
            futures.add(future);
        }
        
        // Aguardar todas as threads terminarem
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        
        // Recarregar a conta do banco
        conta = service.findById(conta.getId()).orElse(null);
        
        System.out.println("Saldo final: " + conta.getSaldo());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Saldo esperado: " + (1000.0f + (numThreads * depositsPerThread * depositAmount)));
        
        // Verificar se houve perda de dados
        float expectedBalance = 1000.0f + (numThreads * depositsPerThread * depositAmount);
        assertEquals(expectedBalance, conta.getSaldo(), 0.01f);
    }

    @Test
    void testConcurrentWithdrawals() throws InterruptedException {
        System.out.println("=== Teste de Retiradas Concorrentes ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        
        int numThreads = 5;
        int withdrawalsPerThread = 5;
        float withdrawalAmount = 50.0f;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < withdrawalsPerThread; j++) {
                    try {
                        service.retirada(conta.getId(), withdrawalAmount);
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Retirada " + (j+1) + " realizada com sucesso");
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Falha na retirada " + (j+1) + ": " + e.getMessage());
                    }
                }
            }, executor);
            futures.add(future);
        }
        
        // Aguardar todas as threads terminarem
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        
        // Recarregar a conta do banco
        conta = service.findById(conta.getId()).orElse(null);
        
        System.out.println("Saldo final: " + conta.getSaldo());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Saldo esperado: " + (1000.0f - (numThreads * withdrawalsPerThread * withdrawalAmount)));
        
        // Verificar se houve perda de dados
        float expectedBalance = 1000.0f - (numThreads * withdrawalsPerThread * withdrawalAmount);
        assertEquals(expectedBalance, conta.getSaldo(), 0.01f);
    }

    @Test
    void testConcurrentMixedOperations() throws InterruptedException {
        System.out.println("=== Teste de Operações Mistas Concorrentes ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        
        int numThreads = 8;
        int operationsPerThread = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    try {
                        if (j % 2 == 0) {
                            // Depósito
                            service.deposita(conta.getId(), 100.0f);
                            System.out.println("Thread " + threadId + " - Depósito " + (j+1) + " realizado");
                        } else {
                            // Retirada
                            service.retirada(conta.getId(), 50.0f);
                            System.out.println("Thread " + threadId + " - Retirada " + (j+1) + " realizada");
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.out.println("Thread " + threadId + " - Falha na operação " + (j+1) + ": " + e.getMessage());
                    }
                }
            }, executor);
            futures.add(future);
        }
        
        // Aguardar todas as threads terminarem
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        
        // Recarregar a conta do banco
        conta = service.findById(conta.getId()).orElse(null);
        
        System.out.println("Saldo final: " + conta.getSaldo());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        
        // Verificar se o saldo final é razoável (deve ser maior que o inicial devido aos depósitos)
        assertTrue(conta.getSaldo() >= 1000.0f);
    }
} 