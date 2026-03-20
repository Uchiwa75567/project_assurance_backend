package com.ma_sante_assurance.config;

import com.ma_sante_assurance.agent.dto.AgentRequestDTO;
import com.ma_sante_assurance.agent.service.AgentService;
import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.auth.service.AuthService;
import com.ma_sante_assurance.carte.dto.CarteRequestDTO;
import com.ma_sante_assurance.carte.service.CarteService;
import com.ma_sante_assurance.client.dto.ClientRequestDTO;
import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.service.ClientService;
import com.ma_sante_assurance.common.enums.GeneralStatus;
import com.ma_sante_assurance.common.enums.PaiementStatus;
import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.garantie.dto.GarantieRequestDTO;
import com.ma_sante_assurance.garantie.service.GarantieService;
import com.ma_sante_assurance.pack.dto.PackRequestDTO;
import com.ma_sante_assurance.pack.dto.PackResponseDTO;
import com.ma_sante_assurance.pack.entity.Pack;
import com.ma_sante_assurance.pack.repository.PackRepository;
import com.ma_sante_assurance.pack.service.PackService;
import com.ma_sante_assurance.packgarantie.dto.PackGarantieRequestDTO;
import com.ma_sante_assurance.packgarantie.service.PackGarantieService;
import com.ma_sante_assurance.paiement.dto.PaiementRequestDTO;
import com.ma_sante_assurance.paiement.dto.PaiementResponseDTO;
import com.ma_sante_assurance.paiement.service.PaiementService;
import com.ma_sante_assurance.partenaire.dto.PartenaireRequestDTO;
import com.ma_sante_assurance.partenaire.service.PartenaireService;
import com.ma_sante_assurance.souscription.dto.SouscriptionRequestDTO;
import com.ma_sante_assurance.souscription.dto.SouscriptionResponseDTO;
import com.ma_sante_assurance.souscription.service.SouscriptionService;
import com.ma_sante_assurance.user.entity.User;
import com.ma_sante_assurance.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedAll(
            AuthService authService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AgentService agentService,
            PackService packService,
            PackRepository packRepository,
            GarantieService garantieService,
            PackGarantieService packGarantieService,
            ClientService clientService,
            CarteService carteService,
            SouscriptionService souscriptionService,
            PaiementService paiementService,
            PartenaireService partenaireService
    ) {
        return args -> {
            ensureTestUser(authService, userRepository, passwordEncoder, "Admin M&A", "admin@masante.sn", "admin123", UserRole.ADMIN);
            ensureTestUser(authService, userRepository, passwordEncoder, "Agent Ibrahima", "agent1@masante.sn", "agent123", UserRole.AGENT);
            ensureTestUser(authService, userRepository, passwordEncoder, "Client Test", "client@masante.sn", "client123", UserRole.CLIENT);

            safe(() -> agentService.createOrUpdateAgent(new AgentRequestDTO("1", "MA-8218992", "Ibrahima", "Diop", "783783434", "Active")));
            safe(() -> agentService.createOrUpdateAgent(new AgentRequestDTO("2", "MA-8218993", "Aissatou", "Ndiaye", "781234567", "Active")));
            safe(() -> agentService.createOrUpdateAgent(new AgentRequestDTO("3", "MA-8218994", "Mamadou", "Fall", "701234567", "Active")));
            safe(() -> agentService.createOrUpdateAgent(new AgentRequestDTO("4", "MA-8218995", "Khady", "Ba", "761234567", "Inactif")));

            PackResponseDTO noppale = findOrCreatePack(
                    packRepository,
                    packService,
                    new PackRequestDTO("PACK-NOPPALE", "Pack Noppale Sante", "Couverture essentielle", new BigDecimal("3900"), 1, true)
            );
            PackResponseDTO kerYaram = findOrCreatePack(
                    packRepository,
                    packService,
                    new PackRequestDTO("PACK-KER-YARAM", "Pack Ker Yaram", "Couverture familiale", new BigDecimal("5900"), 1, true)
            );

            var g1 = safeReturn(() -> garantieService.create(new GarantieRequestDTO("Consultations generales", "Soins de base", new BigDecimal("50000"))));
            var g2 = safeReturn(() -> garantieService.create(new GarantieRequestDTO("Medicaments de base", "Traitements courants", new BigDecimal("30000"))));
            var g3 = safeReturn(() -> garantieService.create(new GarantieRequestDTO("Hospitalisation", "Prise en charge familiale", new BigDecimal("150000"))));

            if (g1 != null) safe(() -> packGarantieService.create(new PackGarantieRequestDTO(noppale.id(), g1.id(), new BigDecimal("50000"))));
            if (g2 != null) safe(() -> packGarantieService.create(new PackGarantieRequestDTO(noppale.id(), g2.id(), new BigDecimal("30000"))));
            if (g3 != null) safe(() -> packGarantieService.create(new PackGarantieRequestDTO(kerYaram.id(), g3.id(), new BigDecimal("150000"))));

            ClientResponseDTO client1 = clientService.create(new ClientRequestDTO.Create(
                    null, "Abdoulaye", "Diallo", LocalDate.of(1992, 2, 24), "771234567", "Dakar", "CNI1234",
                    null, "Pack Noppale Sante", GeneralStatus.ACTIVE, "1"
            ));

            SouscriptionResponseDTO s1 = souscriptionService.create(new SouscriptionRequestDTO.Create(
                    client1.id(), "1", noppale.id(), LocalDate.now(), LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1), null
            ), "system");

            safe(() -> carteService.createOrUpdate(new CarteRequestDTO(s1.id(), null, LocalDate.now(), LocalDate.now().plusYears(1), null, null)));

            PaiementResponseDTO p1 = paiementService.create(new PaiementRequestDTO.Create(
                    s1.id(), new BigDecimal("3900"), "PAY-001", "mobile-money", "TX-001", null, LocalDate.now(), LocalDate.now().plusMonths(1)
            ), "system");
            safe(() -> paiementService.update(p1.id(), new PaiementRequestDTO.Update(PaiementStatus.VALIDE, null, null, null, null, null, null), "system"));

            safe(() -> partenaireService.create(new PartenaireRequestDTO(
                    null, "Hopital Principal de Dakar", "hopital", "Avenue Nelson Mandela, Dakar",
                    "+221 33 839 50 50", 14.6708, -17.4352, true
            )));
            safe(() -> partenaireService.create(new PartenaireRequestDTO(
                    null, "CHN Fann", "hopital", "Fann Residence, Dakar",
                    "+221 33 869 18 18", 14.6924, -17.4552, true
            )));
            safe(() -> partenaireService.create(new PartenaireRequestDTO(
                    null, "Hopital Aristide Le Dantec", "hopital", "Avenue Pasteur, Dakar",
                    "+221 33 889 38 00", 14.6732, -17.4385, true
            )));
        };
    }

    private PackResponseDTO findOrCreatePack(PackRepository repo, PackService service, PackRequestDTO request) {
        Pack existing = repo.findByCode(request.code()).orElse(null);
        if (existing != null) {
            return new PackResponseDTO(
                    existing.getId(),
                    existing.getCode(),
                    existing.getNom(),
                    existing.getDescription(),
                    existing.getPrix(),
                    existing.getDuree(),
                    existing.getActif()
            );
        }
        return service.create(request);
    }

    private void safe(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ignored) {
            // idempotence seed for dev
        }
    }

    private <T> T safeReturn(java.util.concurrent.Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void ensureTestUser(AuthService authService,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                String fullName,
                                String email,
                                String rawPassword,
                                UserRole role) {
        String normalizedEmail = email.toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);

        if (user == null) {
            safe(() -> authService.register(new AuthRequestDTO.RegisterRequest(
                    fullName,
                    normalizedEmail,
                    null,
                    null,
                    null,
                    null,
                    rawPassword,
                    role
            )));
            return;
        }

        user.setFullName(fullName);
        user.setRole(role);
        user.setActif(true);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }
}
