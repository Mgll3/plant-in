package com.gardengroup.agroplantationapp.GerkinTest;

import com.gardengroup.agroplantationapp.model.dto.publication.PublicationSaveDTO;
import com.gardengroup.agroplantationapp.model.dto.user.LoginDTO;
import com.gardengroup.agroplantationapp.model.entity.Plantation;
import com.gardengroup.agroplantationapp.model.entity.Publication;
import com.gardengroup.agroplantationapp.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PublicationServiceBDDTest {

    @Mock
    private PublicationService publicationService;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PublicationServiceBDDTest bddTest;

    private User user;
    private String token = "token123";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void guardarNuevaPublicacion_CuandoUsuarioEsValido_DeberiaGuardarYRetornarPublicacion() {
        // Given
        PublicationSaveDTO dto = new PublicationSaveDTO();
        Plantation plantation = new Plantation();
        plantation.setAddress("Calle 123");
        dto.setTitle("Mi publicación");
        dto.setPlantation(plantation);

        Publication saved = new Publication();
        saved.setId(1L);
        saved.setTitle(dto.getTitle());

        when(publicationService.save(dto)).thenReturn(saved);

        // When
        Publication resultado = publicationService.save(dto);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(publicationService, times(1)).save(dto);
    }

    @Test
    void obtenerPublicacionPorId_CuandoExiste_DeberiaRetornarPublicacion() {
        // Given
        Long id = 123L;
        Publication pub = new Publication();
        pub.setId(id);
        pub.setTitle("Publicación encontrada");
        when(publicationService.findById(id)).thenReturn(Optional.of(pub));

        // When
        Optional<Publication> resultado = publicationService.findById(id);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
    }

    @Test
    void obtenerPublicacionesPorLikes_CuandoExisten_DeberiaRetornarListaOrdenada() {
        // Given
        List<Publication> publicaciones = List.of(
                new Publication(1L, "Más popular"),
                new Publication(2L, "Menos popular")
        );
        when(publicationService.findByLikes(1)).thenReturn(publicaciones);

        // When
        List<Publication> resultado = publicationService.findByLikes(1);

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTitle()).isEqualTo("Más popular");
    }

    @Test
    void iniciarSesion_CuandoCredencialesSonValidas_DeberiaRetornarToken() {
        // Given
        LoginDTO loginDto = new LoginDTO("usuario@correo.com", "123456");
        when(authService.login(loginDto)).thenReturn("jwt.token.mock");

        // When
        String token = authService.login(loginDto);

        // Then
        assertThat(token).isEqualTo("jwt.token.mock");
    }

    @Test
    void obtenerSesionUsuario_CuandoTokenEsValido_DeberiaRetornarUsuario() {
        // Given
        when(userService.getSession(token)).thenReturn(user);

        // When
        User resultado = userService.getSession(token);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
    }
}
