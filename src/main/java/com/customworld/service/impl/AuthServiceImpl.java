package com.customworld.service.impl;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.AuthResponse;
import com.customworld.entity.User;
import com.customworld.exception.BadRequestException;
import com.customworld.repository.UserRepository;
import com.customworld.security.JwtTokenProvider;
import com.customworld.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Début login pour email : {}", loginRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            log.info("Authentification réussie pour email : {}", loginRequest.getEmail());
        } catch (AuthenticationException ex) {
            log.error("Erreur d'authentification : {}", ex.getMessage());
            throw new BadRequestException("Email ou mot de passe incorrect", "");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé pour email : {}", loginRequest.getEmail());
                    return new BadRequestException("Email ou mot de passe incorrect", "");
                });

        log.info("Génération du token pour l'utilisateur : {}", user.getEmail());
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        log.info("Token généré : {}", token);

        return new AuthResponse(token, user.getRole().name());
    }


    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email déjà utilisé","");
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());
        user.setRole(registerRequest.getRole());
        userRepository.save(user);
    }
}