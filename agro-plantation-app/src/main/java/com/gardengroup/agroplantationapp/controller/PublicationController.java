package com.gardengroup.agroplantationapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gardengroup.agroplantationapp.model.dto.publication.*;
import com.gardengroup.agroplantationapp.model.entity.Publication;
import com.gardengroup.agroplantationapp.model.entity.Vote;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gardengroup.agroplantationapp.service.interfaces.IPublicationService;
import com.gardengroup.agroplantationapp.service.implementation.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/publication")
@CrossOrigin(origins = "*")
@Slf4j
public class PublicationController {
        @Autowired
        private IPublicationService publicationService;
        @Autowired
        private SecurityService securityService;

        @Operation(summary = "Guardar publicación", description = "End Point para guardar una nueva publicación en base de datos, con Token", tags = {
                        "Publication" })
        @Parameter(name = "Publication", description = "Objeto Publication que se guardará en base de datos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Publicación guardada exitosamente"),
                        @ApiResponse(responseCode = "501", description = "Error al guardar la publicación"),
                        @ApiResponse(responseCode = "401", description = "Email no autorizado para crear publicaciones"),
                        @ApiResponse(responseCode = "400", description = "Error de validación en los campos de la publicación")
        })
        @PostMapping("/save")
        public ResponseEntity<Publication> savePublication(@Valid @RequestBody PublicationSaveDTO publication,
                        HttpServletRequest request) {
                try {
                        String email = securityService.getEmail(request);
                        // Validar si existe correo
                        if (email == null) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                        }
                        Publication publicationSaved = publicationService.savePublication(publication, email);
                        return new ResponseEntity<>(publicationSaved, HttpStatus.CREATED);
                } catch (Exception e) {
                        log.error(e.getMessage());
                        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
        }

        @Operation(summary = "Obtener una publicación", description = "End Point para obtener una publicación por su id", tags = {
                        "Publication" })
        @Parameter(name = "id", description = "Id de la publicación que se desea obtener")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicación obtenida exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicación no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener la publicación")
        })
        @GetMapping("/{id}")
        public ResponseEntity<PublicationDTO> getPublication(@PathVariable Long id, HttpServletRequest request) {
                String email = securityService.getEmail(request);
                PublicationDTO publication = publicationService.getPublication(id, email);

                return new ResponseEntity<>(publication, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones por email", description = "End Point para obtener todas las publicaciones asociadas a un email de usuario", tags = {
                        "Publication" })
        @Parameter(name = "email", description = "Email del usuario que se desea obtener sus publicaciones")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/email/{email}")
        public ResponseEntity<List<Publication>> publicationsByEmail(@PathVariable String email) {
                List<Publication> publication = publicationService.publicationsByEmail(email);
                return new ResponseEntity<>(publication, HttpStatus.OK);
        }

        @Operation(summary = "Obtener las publicaciones principales", description = "Endpoint para obtener las publicaciones más populares o mejor valoradas", tags = {
                        "Publication" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Éxito al obtener las publicaciones principales", content = @Content(schema = @Schema(implementation = List.class))),
                        @ApiResponse(responseCode = "404", description = "No hay publicaciones para mostrar"),
                        @ApiResponse(responseCode = "500", description = "Error al procesar la solicitud", content = @Content(schema = @Schema(implementation = String.class)))
        })
        @GetMapping("publications/top")
        public ResponseEntity<List<Publication>> getTopPublications() {
                List<Publication> topPublications = publicationService.getTopPublications();
                return new ResponseEntity<>(topPublications, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones por Likes", description = "End Point para obtener las publicaciónes en orden por más likes, además devuelve como maximo 3, el número de paginaciónes siguientes posibles", tags = {
                        "Publication Filters" })
        @Parameter(name = "pag", description = "Numero de Paginación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/like/{pag}")
        public ResponseEntity<PublicationFilterDTO> getPublicationsByLike(@PathVariable int pag) {
                PublicationFilterDTO publications = publicationService.getPublicationsByLike(pag);
                return new ResponseEntity<>(publications, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones por Usuario", description = "End Point para obtener las publicaciónes en orden alfabetico por usuario, además devuelve como maximo 3, el número de paginaciónes siguientes posibles", tags = {
                        "Publication Filters" })
        @Parameter(name = "pag", description = "Numero de Paginación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/user/{pag}")
        public ResponseEntity<PublicationFilterDTO> getPublicationsByUser(@PathVariable int pag) {
                PublicationFilterDTO publications = publicationService.getPublicationsByUser(pag);
                return new ResponseEntity<>(publications, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones aleatoriamente", description = "End Point para obtener las publicaciónes en orden más recientes por fecha, además devuelve como maximo 3, el número de paginaciónes siguientes posibles", tags = {
                        "Publication Filters" })
        @Parameter(name = "pag", description = "Numero de Paginación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/date/{pag}")
        public ResponseEntity<PublicationFilterDTO> getPublicationsByDate(@PathVariable int pag) {
                PublicationFilterDTO publications = publicationService.getPublicationsByDate(pag);
                return new ResponseEntity<>(publications, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones aleatoriamente", description = "End Point para obtener las publicaciónes de forma aleatoria, además devuelve como maximo 3, el número de paginaciónes siguientes posibles", tags = {
                        "Publication Filters" })
        @Parameter(name = "pag", description = "Numero de Paginación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/aleatory/{pag}")
        public ResponseEntity<PublicationFilterDTO> getPublicationsByAleatory(@PathVariable int pag) {
                PublicationFilterDTO publications = publicationService.getPublicationsByAleatory(pag);
                return new ResponseEntity<>(publications, HttpStatus.OK);
        }

        @Operation(summary = "Obtener publicaciones por Usuario y cantidad", description = "End Point para obtener las publicaciónes en orden por usuario con cantidad mayor a menor, además devuelve como maximo 3, el número de paginaciónes siguientes posibles", tags = {
                        "Publication Filters" })
        @Parameter(name = "pag", description = "Numero de Paginación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Publicaciones no encontradas"),
                        @ApiResponse(responseCode = "500", description = "Error al obtener las publicaciones")
        })
        @GetMapping("/userQuantity/{pag}")
        public ResponseEntity<PublicationFilterDTO> getPublicationsByQuantity(@PathVariable int pag) {
                PublicationFilterDTO publications = publicationService.getPublicationsByQuantity(pag);
                return new ResponseEntity<>(publications, HttpStatus.OK);
        }

}
