package com.ma_sante_assurance.auth.service;

import com.ma_sante_assurance.agent.service.AgentService;
import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.client.dto.ClientRequestDTO;
import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.service.ClientService;
import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.common.messages.AuthMessages;
import com.ma_sante_assurance.common.util.EmailNormalizer;
import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.common.validation.PhoneNumberValidator;
import com.ma_sante_assurance.common.validation.ValidationMessages;
import com.ma_sante_assurance.notification.NotificationService;
import com.ma_sante_assurance.user.entity.User;
import com.ma_sante_assurance.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final ClientService clientService;
    private final AgentService agentService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public RegistrationService(UserRepository userRepository,
                               ClientService clientService,
                               AgentService agentService,
                               PasswordEncoder passwordEncoder,
                               NotificationService notificationService) {
        this.userRepository = userRepository;
        this.clientService = clientService;
        this.agentService = agentService;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public RegistrationResult register(AuthRequestDTO.RegisterRequest request) {
        UserRole role = request.role() == null ? UserRole.CLIENT : request.role();
        String normalizedEmail = EmailNormalizer.normalize(request.email());

        if (!PhoneNumberValidator.isValid(request.telephone())) {
            throw new IllegalArgumentException(ValidationMessages.PHONE_INVALID);
        }

        if (role == UserRole.CLIENT) {
            if (normalizedEmail != null) {
                userRepository.findByEmail(normalizedEmail).ifPresent(u -> {
                    throw new IllegalArgumentException(AuthMessages.EMAIL_ALREADY_USED);
                });
            }

            User user = User.builder()
                    .id(IdGenerator.uuid())
                    .fullName(request.fullName())
                    .email(normalizedEmail)
                    .passwordHash(passwordEncoder.encode(request.password()))
                    .role(UserRole.CLIENT)
                    .actif(true)
                    .build();

            userRepository.save(user);
            ClientResponseDTO client = clientService.createFromUser(new ClientRequestDTO.CreateFromUser(
                    user.getId(),
                    user.getFullName(),
                    request.dateNaissance(),
                    request.telephone(),
                    request.numeroCni(),
                    request.photoUrl()
            ));
            notificationService.sendNumeroAssurance(user.getFullName(), client.numeroAssurance(), normalizedEmail, request.telephone());
            return new RegistrationResult(user, client);
        }

        if (normalizedEmail == null) {
            throw new IllegalArgumentException(ValidationMessages.EMAIL_REQUIRED);
        }

        userRepository.findByEmail(normalizedEmail).ifPresent(u -> {
            throw new IllegalArgumentException(AuthMessages.EMAIL_ALREADY_USED);
        });

        User user = User.builder()
                .id(IdGenerator.uuid())
                .fullName(request.fullName())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(role)
                .actif(true)
                .build();

        userRepository.save(user);
        if (role == UserRole.AGENT) {
            agentService.createFromUser(user.getId(), user.getFullName());
        }

        return new RegistrationResult(user, null);
    }

    @Transactional(readOnly = true)
    public User findUserByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifiant requis");
        }

        String trimmed = identifier.trim();
        if (trimmed.contains("@")) {
            return userRepository.findByEmail(EmailNormalizer.normalize(trimmed))
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        }

        ClientResponseDTO client = clientService.findByNumeroAssurance(trimmed);
        if (client.userId() == null || client.userId().isBlank()) {
            throw new EntityNotFoundException("Utilisateur introuvable");
        }

        return userRepository.findById(client.userId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

}
