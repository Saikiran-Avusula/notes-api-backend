package com.notesapi.service;


import com.notesapi.dto.AuthResponse;
import com.notesapi.dto.LoginRequest;
import com.notesapi.dto.RegisterRequest;
import com.notesapi.model.User;
import com.notesapi.repository.UserRepository;
import com.notesapi.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



//We are done:
//
//register(): Checks email doesn't exist, hashes password with BCrypt, saves user, returns JWT
//login(): Authenticates credentials, generates JWT if valid
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse register(RegisterRequest registerRequestUser){

//        check if email already exists
        if(userRepository.existsByEmail(registerRequestUser.getEmail())){
            throw new RuntimeException("Email already exists..!");
        };

//        Create new user
        User user = new User();
        user.setName(registerRequestUser.getName());
        user.setEmail(registerRequestUser.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestUser.getPassword()));

        User savedNewUser = userRepository.save(user);

        log.info("saved user id: {}", savedNewUser.getId());
        log.info("saved user Name: {}", savedNewUser.getName());
        log.info("saved user Email: {}", savedNewUser.getEmail());

//        generate token
        String generatedToken = jwtUtil.generateToken(savedNewUser.getEmail());
        log.info("generated Token: {}", generatedToken);

        AuthResponse authResponse = new AuthResponse(generatedToken, savedNewUser.getId(), savedNewUser.getName(), savedNewUser.getEmail());
        log.info("Auth response: {}", authResponse);

        return authResponse;
    }

    public AuthResponse login(LoginRequest loginRequest){

        log.info("logged in user");

//        Authentication user
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

//        Get user from database
            User userFromDB = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));


//        Generate JWT token
            String loginGeneratedToken = jwtUtil.generateToken(userFromDB.getEmail());


            AuthResponse loginAuthResponseLogin = new AuthResponse(loginGeneratedToken, userFromDB.getId(), userFromDB.getName(), userFromDB.getEmail());
            log.info("Login auth response: {}", loginAuthResponseLogin);
            return loginAuthResponseLogin;
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }



}
