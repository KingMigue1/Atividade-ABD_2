package com.example.contabancaria;

import com.example.contabancaria.model.ContaBancariaVersionada;
import com.example.contabancaria.service.ContaBancariaVersionadaService;
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
public class ContaBancariaVersionadaConcurrencyTest {

    @Autowired
    private ContaBancariaVersionadaService service;

    private ContaBancariaVersionada conta;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger optimisticLockCount = new AtomicInteger(0);

    @BeforeEach
    void setUp() {
        // Criar uma conta com saldo inicial de 1000
        conta = new ContaBancariaVersionada("Maria Santos", 1000.0f);
        conta = service.save(conta);
        successCount.set(0);
        failureCount.set(0);
        optimisticLockCount.set(0);
    }

    @Test
    void testConcurrentDepositsWithVersioning() throws InterruptedException {
        System.out.println("=== Teste de Depósitos Concorrentes com Versionamento ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        System.out.println("Versão inicial: " + conta.getDataMovimento());
        
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
                        if (e.getMessage().contains("version") || e.getMessage().contains("optimistic")) {
                            optimisticLockCount.incrementAndGet();
                            System.out.println("Thread " + threadId + " - Conflito de versão otimista no depósito " + (j+1));
                        } else {
                            System.out.println("Thread " + threadId + " - Falha no depósito " + (j+1) + ": " + e.getMessage());
                        }
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
        System.out.println("Versão final: " + conta.getDataMovimento());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Conflitos de versão otimista: " + optimisticLockCount.get());
        System.out.println("Saldo esperado: " + (1000.0f + (numThreads * depositsPerThread * depositAmount)));
        
        // Verificar se houve perda de dados
        float expectedBalance = 1000.0f + (numThreads * depositsPerThread * depositAmount);
        assertEquals(expectedBalance, conta.getSaldo(), 0.01f);
    }

    @Test
    void testConcurrentWithdrawalsWithVersioning() throws InterruptedException {
        System.out.println("=== Teste de Retiradas Concorrentes com Versionamento ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        System.out.println("Versão inicial: " + conta.getDataMovimento());
        
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
                        if (e.getMessage().contains("version") || e.getMessage().contains("optimistic")) {
                            optimisticLockCount.incrementAndGet();
                            System.out.println("Thread " + threadId + " - Conflito de versão otimista na retirada " + (j+1));
                        } else {
                            System.out.println("Thread " + threadId + " - Falha na retirada " + (j+1) + ": " + e.getMessage());
                        }
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
        System.out.println("Versão final: " + conta.getDataMovimento());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Conflitos de versão otimista: " + optimisticLockCount.get());
        System.out.println("Saldo esperado: " + (1000.0f - (numThreads * withdrawalsPerThread * withdrawalAmount)));
        
        // Verificar se houve perda de dados
        float expectedBalance = 1000.0f - (numThreads * withdrawalsPerThread * withdrawalAmount);
        assertEquals(expectedBalance, conta.getSaldo(), 0.01f);
    }

    @Test
    void testConcurrentMixedOperationsWithVersioning() throws InterruptedException {
        System.out.println("=== Teste de Operações Mistas Concorrentes com Versionamento ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        System.out.println("Versão inicial: " + conta.getDataMovimento());
        
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
                        if (e.getMessage().contains("version") || e.getMessage().contains("optimistic")) {
                            optimisticLockCount.incrementAndGet();
                            System.out.println("Thread " + threadId + " - Conflito de versão otimista na operação " + (j+1));
                        } else {
                            System.out.println("Thread " + threadId + " - Falha na operação " + (j+1) + ": " + e.getMessage());
                        }
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
        System.out.println("Versão final: " + conta.getDataMovimento());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Conflitos de versão otimista: " + optimisticLockCount.get());
        
        // Verificar se o saldo final é razoável (deve ser maior que o inicial devido aos depósitos)
        assertTrue(conta.getSaldo() >= 1000.0f);
    }

    @Test
    void testHighConcurrencyStressTest() throws InterruptedException {
        System.out.println("=== Teste de Estresse com Alta Concorrência ===");
        System.out.println("Saldo inicial: " + conta.getSaldo());
        
        int numThreads = 20;
        int operationsPerThread = 20;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    try {
                        if (j % 3 == 0) {
                            // Depósito
                            service.deposita(conta.getId(), 25.0f);
                        } else if (j % 3 == 1) {
                            // Retirada
                            service.retirada(conta.getId(), 10.0f);
                        } else {
                            // Depósito menor
                            service.deposita(conta.getId(), 5.0f);
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        if (e.getMessage().contains("version") || e.getMessage().contains("optimistic")) {
                            optimisticLockCount.incrementAndGet();
                        }
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
        System.out.println("Versão final: " + conta.getDataMovimento());
        System.out.println("Operações bem-sucedidas: " + successCount.get());
        System.out.println("Operações com falha: " + failureCount.get());
        System.out.println("Conflitos de versão otimista: " + optimisticLockCount.get());
        
        // Verificar se o saldo final é razoável
        assertTrue(conta.getSaldo() >= 1000.0f);
    }
} 