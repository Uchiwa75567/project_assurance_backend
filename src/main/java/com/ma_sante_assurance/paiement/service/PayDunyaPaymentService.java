package com.ma_sante_assurance.paiement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.client.repository.ClientRepository;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Service
public class PayDunyaPaymentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;
    private final PackRepository packRepository;

    @Value("${app.paydunya.public-key}")
    private String publicKey;

    @Value("${app.paydunya.private-key}")
    private String privateKey;

    @Value("${app.paydunya.token}")
    private String token;

    @Value("${app.paydunya.api-url}")
    private String apiUrl;

    @Value("${app.paydunya.ipn-url}")
    private String ipnUrl;

    public PayDunyaPaymentService(@Qualifier("paydunyaRestTemplate") RestTemplate restTemplate,
                                  ObjectMapper objectMapper,
                                  ClientRepository clientRepository,
                                  PackRepository packRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.clientRepository = clientRepository;
        this.packRepository = packRepository;
    }

    public record CreateInvoiceResponse(
            String token,
            String paymentUrl,
            String status,
            String message
    ) {}

    public CreateInvoiceResponse createInvoice(String clientId, String packId, String souscriptionId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable: " + clientId));
        
        Pack pack = packRepository.findById(packId)
                .orElseThrow(() -> new IllegalArgumentException("Pack introuvable: " + packId));

        String amount = pack.getPrix().toString();
        String description = String.format("Souscription %s - Pack %s", souscriptionId, pack.getNom());
        String customerName = client.getPrenom() + " " + client.getNom();
        String customerPhone = client.getTelephone();

        Map<String, String> request = Map.of(
            "amount", amount,
            "description", description,
            "customer_name", customerName,
            "customer_phone", customerPhone,
            "customer_email", "client@" + client.getTelephone() + ".test",
            "reference", souscriptionId,
            "ipn_url", ipnUrl,
            "return_url", ipnUrl + "?success=true",
            "expire_days", "1"
        );

        try {
            String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/inv/api/pay/v1/invoicing/invoices/create")
                    .queryParam("public_key", publicKey)
                    .queryParam("token", token)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + privateKey);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            Map body = response.getBody();
            log.info("PayDunya createInvoice response: {}", body);

            return new CreateInvoiceResponse(
                (String) body.get("token"),
                (String) body.get("payment_url"),
                (String) body.get("status"),
                (String) body.get("message")
            );
        } catch (Exception e) {
            log.error("Erreur PayDunya createInvoice", e);
            throw new RuntimeException("PayDunya indisponible", e);
        }
    }

    public Map verifyPayment(String token) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/inv/api/pay/v1/invoicing/invoices/" + token)
                .queryParam("public_key", publicKey)
                .queryParam("token", token)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + privateKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        log.info("PayDunya verifyPayment[{}]: {}", token, response.getBody());
        return response.getBody();
    }
}
