package com.ma_sante_assurance.user.service;

import com.ma_sante_assurance.common.util.IdGenerator;
import com.ma_sante_assurance.common.util.EmailNormalizer;
import com.ma_sante_assurance.common.messages.AuthMessages;
import com.ma_sante_assurance.user.dto.UserRequestDTO;
import com.ma_sante_assurance.user.dto.UserResponseDTO;
import com.ma_sante_assurance.user.entity.User;
import com.ma_sante_assurance.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO request) {
        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new IllegalArgumentException(AuthMessages.EMAIL_ALREADY_USED);
        });

        User user = User.builder()
                .id(IdGenerator.uuid())
                .fullName(request.fullName())
                .email(EmailNormalizer.normalize(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .actif(true)
                .build();

        return toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> list() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public User findEntity(String id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User introuvable"));
    }

    private UserResponseDTO toDto(User user) {
        return new UserResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getActif());
    }
}
