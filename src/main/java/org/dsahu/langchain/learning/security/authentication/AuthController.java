package org.dsahu.langchain.learning.security.authentication;

import org.dsahu.langchain.learning.employee.dto.LoginRequest;
import org.dsahu.langchain.learning.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        System.out.println("🔥 AuthController.login() CALLED");
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );
        return jwtService.generateToken(
                authentication.getName()
        );
    }

    @PostMapping("/bcryptHash")
    public String getBCryptHash(@RequestParam String token) {
        System.out.println("🔥 AuthController.getBCryptHash() CALLED");
        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();
        return encoder.encode(token);
    }
}