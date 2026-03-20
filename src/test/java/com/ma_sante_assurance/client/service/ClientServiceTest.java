package com.ma_sante_assurance.client.service;

import com.ma_sante_assurance.client.dto.ClientRequestDTO;
import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.entity.Client;
import com.ma_sante_assurance.client.mapper.ClientMapper;
import com.ma_sante_assurance.client.repository.ClientRepository;
import com.ma_sante_assurance.common.audit.service.AuditService;
import com.ma_sante_assurance.common.dto.PageResponse;
import com.ma_sante_assurance.common.enums.GeneralStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private AuditService auditService;
    @Mock
    private NumeroAssuranceGenerator numeroAssuranceGenerator;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(
            clientRepository,
            clientMapper,
            auditService,
            numeroAssuranceGenerator
        );
    }

    @Test
    void create_ShouldCreateClientAndReturnDto() {
        // Arrange
        ClientRequestDTO.Create request = new ClientRequestDTO.Create(
            "user-123",
            "John",
            "Doe",
            null,
            "+221771234567",
            "Dakar",
            "1234567890",
            null,
            "Standard",
            GeneralStatus.ACTIVE,
            "agent-001"
        );

        Client savedClient = Client.builder()
            .id("client-123")
            .userId("user-123")
            .numeroAssurance("MSA-2026-0001")
            .prenom("John")
            .nom("Doe")
            .telephone("+221771234567")
            .adresse("Dakar")
            .numeroCni("1234567890")
            .statut(GeneralStatus.ACTIVE)
            .createdAt(Instant.now())
            .build();

        ClientResponseDTO expectedDto = new ClientResponseDTO(
            "client-123",
            "user-123",
            "MSA-2026-0001",
            "John",
            "Doe",
            null,
            "+221771234567",
            "Dakar",
            "1234567890",
            null,
            "Standard",
            GeneralStatus.ACTIVE,
            "agent-001",
            Instant.now()
        );

        when(numeroAssuranceGenerator.generate()).thenReturn("MSA-2026-0001");
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(clientMapper.toDto(savedClient)).thenReturn(expectedDto);

        // Act
        ClientResponseDTO result = clientService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.prenom());
        assertEquals("Doe", result.nom());
        assertEquals("MSA-2026-0001", result.numeroAssurance());
        verify(clientRepository).save(any(Client.class));
        verify(auditService).log(eq("agent-001"), eq("CLIENT_CREATE"), eq("CLIENT"), any(), any());
    }

    @Test
    void list_ShouldReturnPaginatedResults() {
        // Arrange
        Client client1 = Client.builder()
            .id("client-1")
            .prenom("Alice")
            .nom("Smith")
            .numeroAssurance("MSA-2026-0001")
            .statut(GeneralStatus.ACTIVE)
            .build();

        Client client2 = Client.builder()
            .id("client-2")
            .prenom("Bob")
            .nom("Jones")
            .numeroAssurance("MSA-2026-0002")
            .statut(GeneralStatus.ACTIVE)
            .build();

        Page<Client> page = new PageImpl<>(List.of(client1, client2), PageRequest.of(0, 20), 2);
        
        when(clientRepository.search(any(), any(), any(PageRequest.class))).thenReturn(page);
        when(clientMapper.toDto(any(Client.class)))
            .thenAnswer(invocation -> {
                Client c = invocation.getArgument(0);
                return new ClientResponseDTO(
                    c.getId(), c.getUserId(), c.getNumeroAssurance(), c.getPrenom(), c.getNom(),
                    null, c.getTelephone(), c.getAdresse(), c.getNumeroCni(),
                    c.getPhotoUrl(), c.getTypeAssurance(), c.getStatut(), c.getCreatedByAgentId(), c.getCreatedAt()
                );
            });

        // Act
        PageResponse<ClientResponseDTO> result = clientService.list(null, null, 0, 20, "createdAt", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(20, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(clientRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(jakarta.persistence.EntityNotFoundException.class, 
            () -> clientService.findById("non-existent"));
    }

    @Test
    void update_ShouldUpdateClientFields() {
        // Arrange
        Client existingClient = Client.builder()
            .id("client-123")
            .prenom("John")
            .nom("Doe")
            .telephone("+221771234567")
            .statut(GeneralStatus.ACTIVE)
            .build();

        ClientRequestDTO.Update request = new ClientRequestDTO.Update(
            "Jane", null, null, "+221779999999", null, null, null, null, GeneralStatus.INACTIF
        );

        when(clientRepository.findById("client-123")).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);
        when(clientMapper.toDto(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            return new ClientResponseDTO(
                c.getId(), c.getUserId(), c.getNumeroAssurance(), c.getPrenom(), c.getNom(),
                null, c.getTelephone(), c.getAdresse(), c.getNumeroCni(),
                c.getPhotoUrl(), c.getTypeAssurance(), c.getStatut(), c.getCreatedByAgentId(), c.getCreatedAt()
            );
        });

        // Act
        ClientResponseDTO result = clientService.update("client-123", request, "agent-001");

        // Assert
        assertNotNull(result);
        verify(clientRepository).save(any(Client.class));
    }
}
