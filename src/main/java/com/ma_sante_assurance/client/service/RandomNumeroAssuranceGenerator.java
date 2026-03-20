package com.ma_sante_assurance.client.service;

import com.ma_sante_assurance.client.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomNumeroAssuranceGenerator implements NumeroAssuranceGenerator {

    private final ClientRepository clientRepository;
    private final Random random = new Random();

    public RandomNumeroAssuranceGenerator(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public String generate() {
        String numero;
        do {
            numero = "MA-" + (1000000 + random.nextInt(9000000));
        } while (clientRepository.findByNumeroAssurance(numero).isPresent());
        return numero;
    }
}
