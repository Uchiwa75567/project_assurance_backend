package com.ma_sante_assurance.upload.service;

import com.ma_sante_assurance.upload.dto.UploadResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class CloudinaryUploadService {

    @Value("${app.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${app.cloudinary.folder:ma_sante_assurance}")
    private String folder;

    private final RestTemplate restTemplate = new RestTemplate();

    public UploadResponseDTO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier image requis");
        }
        if (cloudName == null || cloudName.isBlank() || apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException("Configuration Cloudinary incomplète");
        }

        long timestamp = Instant.now().getEpochSecond();
        String signature = sign(Map.of(
                "folder", folder,
                "timestamp", String.valueOf(timestamp)
        ));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartByteArrayResource(readBytes(file), file.getOriginalFilename(), file.getContentType()));
        body.add("api_key", apiKey);
        body.add("timestamp", String.valueOf(timestamp));
        body.add("folder", folder);
        body.add("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        String endpoint = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, new HttpEntity<>(body, headers), Map.class);

        Map<?, ?> payload = response.getBody();
        if (payload == null || payload.get("secure_url") == null) {
            throw new IllegalStateException("Réponse Cloudinary invalide");
        }

        String secureUrl = payload.get("secure_url").toString();
        String publicId = payload.get("public_id") == null ? null : payload.get("public_id").toString();
        return new UploadResponseDTO(secureUrl, publicId);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le fichier image", e);
        }
    }

    private String sign(Map<String, String> params) {
        Map<String, String> ordered = new TreeMap<>(params);
        StringBuilder base = new StringBuilder();
        ordered.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                if (base.length() > 0) {
                    base.append('&');
                }
                base.append(key).append('=').append(value);
            }
        });
        base.append(apiSecret);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(base.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithme SHA-1 indisponible", e);
        }
    }

    private static final class MultipartByteArrayResource extends ByteArrayResource {
        private final String filename;
        private final String contentType;

        private MultipartByteArrayResource(byte[] byteArray, String filename, String contentType) {
            super(byteArray);
            this.filename = filename == null ? "upload.jpg" : filename;
            this.contentType = contentType;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
