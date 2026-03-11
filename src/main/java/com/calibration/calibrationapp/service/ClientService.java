package com.calibration.calibrationapp.service;

import com.calibration.calibrationapp.entity.Client;
import com.calibration.calibrationapp.entity.User;
import com.calibration.calibrationapp.repository.ClientRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
    }

    // ← Crée le user automatiquement avec ROLE_CLIENT
    public Client save(Client client) {
        if (client.getUser() != null) {
            User user = client.getUser();
            user.setRole("ROLE_CLIENT");
            // Si vous utilisez BCrypt décommentez la ligne suivante :
            // user.setPassword(passwordEncoder.encode(user.getPassword()));
            client.setUser(user);
        }
        return clientRepository.save(client);
    }

    // ← Update ne touche que le nom
    public Client update(Long id, Client updatedClient) {
        Client existing = getById(id);
        existing.setName(updatedClient.getName());
        return clientRepository.save(existing);
    }

    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé");
        }
        clientRepository.deleteById(id);
    }
}