package com.example.contabancaria.config;

import com.example.contabancaria.model.ContaBancaria;
import com.example.contabancaria.model.ContaBancariaVersionada;
import com.example.contabancaria.service.ContaBancariaService;
import com.example.contabancaria.service.ContaBancariaVersionadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ContaBancariaService contaBancariaService;

    @Autowired
    private ContaBancariaVersionadaService contaBancariaVersionadaService;

    @Override
    public void run(String... args) throws Exception {
        // Criar contas de teste para ContaBancaria
        ContaBancaria conta1 = new ContaBancaria("João Silva", 1000.0f);
        ContaBancaria conta2 = new ContaBancaria("Maria Santos", 2000.0f);
        ContaBancaria conta3 = new ContaBancaria("Pedro Oliveira", 1500.0f);

        contaBancariaService.save(conta1);
        contaBancariaService.save(conta2);
        contaBancariaService.save(conta3);

        System.out.println("Contas bancárias criadas:");
        System.out.println("ID: " + conta1.getId() + " - " + conta1.getNomeCliente() + " - Saldo: " + conta1.getSaldo());
        System.out.println("ID: " + conta2.getId() + " - " + conta2.getNomeCliente() + " - Saldo: " + conta2.getSaldo());
        System.out.println("ID: " + conta3.getId() + " - " + conta3.getNomeCliente() + " - Saldo: " + conta3.getSaldo());

        // Criar contas de teste para ContaBancariaVersionada
        ContaBancariaVersionada contaV1 = new ContaBancariaVersionada("Ana Costa", 1000.0f);
        ContaBancariaVersionada contaV2 = new ContaBancariaVersionada("Carlos Lima", 2000.0f);
        ContaBancariaVersionada contaV3 = new ContaBancariaVersionada("Lucia Ferreira", 1500.0f);

        contaBancariaVersionadaService.save(contaV1);
        contaBancariaVersionadaService.save(contaV2);
        contaBancariaVersionadaService.save(contaV3);

        System.out.println("\nContas bancárias versionadas criadas:");
        System.out.println("ID: " + contaV1.getId() + " - " + contaV1.getNomeCliente() + " - Saldo: " + contaV1.getSaldo() + " - Versão: " + contaV1.getDataMovimento());
        System.out.println("ID: " + contaV2.getId() + " - " + contaV2.getNomeCliente() + " - Saldo: " + contaV2.getSaldo() + " - Versão: " + contaV2.getDataMovimento());
        System.out.println("ID: " + contaV3.getId() + " - " + contaV3.getNomeCliente() + " - Saldo: " + contaV3.getSaldo() + " - Versão: " + contaV3.getDataMovimento());

        System.out.println("\n=== APLICAÇÃO INICIADA COM SUCESSO ===");
        System.out.println("Console H2: http://localhost:8080/h2-console");
        System.out.println("JDBC URL: jdbc:h2:mem:testdb");
        System.out.println("Usuário: sa");
        System.out.println("Senha: password");
        System.out.println("\nAPIs disponíveis:");
        System.out.println("- GET /api/contas - Listar todas as contas");
        System.out.println("- GET /api/contas/{id} - Buscar conta por ID");
        System.out.println("- POST /api/contas - Criar nova conta");
        System.out.println("- PUT /api/contas/{id} - Atualizar conta");
        System.out.println("- DELETE /api/contas/{id} - Deletar conta");
        System.out.println("- POST /api/contas/{id}/deposita?valor=100 - Fazer depósito");
        System.out.println("- POST /api/contas/{id}/retirada?valor=50 - Fazer retirada");
        System.out.println("\nAPIs para contas versionadas:");
        System.out.println("- GET /api/contas-versionadas - Listar todas as contas versionadas");
        System.out.println("- GET /api/contas-versionadas/{id} - Buscar conta versionada por ID");
        System.out.println("- POST /api/contas-versionadas - Criar nova conta versionada");
        System.out.println("- PUT /api/contas-versionadas/{id} - Atualizar conta versionada");
        System.out.println("- DELETE /api/contas-versionadas/{id} - Deletar conta versionada");
        System.out.println("- POST /api/contas-versionadas/{id}/deposita?valor=100 - Fazer depósito");
        System.out.println("- POST /api/contas-versionadas/{id}/retirada?valor=50 - Fazer retirada");
    }
} 