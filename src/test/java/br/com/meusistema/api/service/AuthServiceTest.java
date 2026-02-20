package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.LoginRequestDTO;
import br.com.meusistema.api.dtos.LoginResponseDTO;
import br.com.meusistema.api.dtos.RegisterRequestDTO;
import br.com.meusistema.api.dtos.UsuarioResponseDTO;
import br.com.meusistema.api.enums.Role;
import br.com.meusistema.api.model.Usuario;
import br.com.meusistema.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;
    private Usuario usuario;
    private RegisterRequestDTO registerRequestDTO;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setup() {
        Long id = 1L;

        registerRequestDTO = new RegisterRequestDTO(
                "usuarioteste",
                "4321",
                "usuariotesteadmin@gmaail.com",
                Role.USER);

        loginRequestDTO = new LoginRequestDTO("usuarioteste", "4321");

        usuario = Usuario.builder()
                .id(id)
                .username("usuarioteste")
                .email("usuariotesteadmin@gmaail.com")
                .password("encoded4321")
                .role(Role.USER)
                .build();

    }

    @Test
    void deveResgistrarUsuarioComSucesso() {
        when(passwordEncoder.encode("4321")).thenReturn("encoded4321");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("fake-jwt-token");

        LoginResponseDTO response = authService.register(registerRequestDTO);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());

        ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(userCaptor.capture());
        assertEquals("encoded4321", userCaptor.getValue().getPassword());


        verify(passwordEncoder).encode("4321");
        verify(usuarioRepository).save(userCaptor.capture());
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void deveLogarComSucesso() {
        when(usuarioRepository.findByUsername(loginRequestDTO.username())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any())).thenReturn("fake-jwt-token");

        LoginResponseDTO response = authService.login(loginRequestDTO);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertEquals(loginRequestDTO.username(), authCaptor.getValue().getPrincipal());
        assertEquals(loginRequestDTO.password(), authCaptor.getValue().getCredentials());

        verify(usuarioRepository).findByUsername(loginRequestDTO.username());
        verify(jwtService).generateToken(any(UserDetails.class));

    }

    @Test
    void deveLancarExcecaoQuandoCredenciaisDoLoginForemInvalidas() {

        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));


        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO),
                "Deve lançar BadCredentialsException quando as credenciais forem inválidas.");

        verify(usuarioRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirNoLogin() {
        when(usuarioRepository.findByUsername("usuarioteste")).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequestDTO));
        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(usuarioRepository).findByUsername("usuarioteste");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void deveRetornarUsuarioLogadoComSucesso() {
        when(usuarioRepository.findByUsername("usuarioteste")).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = authService.getUsuarioLogado("usuarioteste");

        assertNotNull(response);
        assertEquals("usuarioteste", response.username());

        //Tem que corrigir o UsuarioResponseDTO que está retornando o password

        assertEquals("usuariotesteadmin@gmaail.com", response.email());

        assertEquals(Role.USER, response.role());

        verify(usuarioRepository).findByUsername("usuarioteste");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoTentarRetornarUsuarioLogado() {
        when(usuarioRepository.findByUsername("usuarioteste")).thenReturn(Optional.empty());

        UsernameNotFoundException exception =
        assertThrows(UsernameNotFoundException.class, () -> authService.getUsuarioLogado("usuarioteste"));
        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(usuarioRepository).findByUsername("usuarioteste");

    }

}
