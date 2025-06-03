package com.gardengroup.agroplantationapp.service.implementation;

import com.gardengroup.agroplantationapp.exception.UnauthorizedActionException;
import com.gardengroup.agroplantationapp.model.dto.publication.*;
import com.gardengroup.agroplantationapp.model.entity.*;
import com.gardengroup.agroplantationapp.model.repository.PublicationRepository;
import com.gardengroup.agroplantationapp.service.implementation.VoteService.VoteAndPublicationDTO;
import com.gardengroup.agroplantationapp.service.interfaces.IPublicationService;
import com.gardengroup.agroplantationapp.service.interfaces.IUserService;
import com.gardengroup.agroplantationapp.service.interfaces.IVoteService;
import com.gardengroup.agroplantationapp.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PublicationService implements IPublicationService {

    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IVoteService voteService;

    @Transactional
    public Publication savePublication(PublicationSaveDTO publicationDTO, String email) {

        Publication publication = new Publication(publicationDTO);

        User user = userService.findByEmail(email);
        publication.setAuthor(user);

        // Asignaciones de parametros default
        publication.setVisibility(false);
        publication.setScore(0);
        // Inicializo la publicacion con estado pendiente
        publication.setAuthorizationStatus(new StateRequest(1L));
        publication.setPublicationDate(LocalDateTime.now());
        publication.setPlantation(publication.getPlantation());

        return publicationRepository.save(publication);
    }

    public List<Publication> getTopPublications() {

        List<Publication> allPublications = publicationRepository.findTop6ByOrderByScoreDesc();

        if (allPublications.isEmpty()) {
            throw new DataAccessException(Constants.PS_NOT_FOUND) {
            };
        }
        // Limitar la cantidad de publicaciones devueltas a exactamente 6
        return allPublications.stream().limit(6).collect(Collectors.toList());
    }

    @Transactional
    public PublicationDTO getPublication(Long publicationId, String email) {

        User user = userService.findByEmail(email);

        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new DataAccessException(Constants.P_NOT_FOUND) {
                });

        // Devolver si el usuario ya ha votado en esta publicación
        Boolean voto = voteService.findByUserAndPublication(user.getId(), publicationId);
        PublicationDTO publicationDTO = new PublicationDTO(publication);
        publicationDTO.setUserVote(voto);

        return publicationDTO;

    }

    @Transactional
    public List<Publication> publicationsByEmail(String email) {

        final List<Publication> publications = publicationRepository.publicationsByEmail(email);

        if (!publications.isEmpty()) {
            return publications;
        }

        throw new DataAccessException(Constants.PS_NOT_FOUND) {
        };

    }

    @Transactional
    public Publication updatePublication(PublicationUpdDTO publicationUpdDTO) {

        Publication publication = new Publication(publicationUpdDTO);

        if (!publicationRepository.existsById(publication.getId())) {
            throw new DataAccessException(Constants.P_NOT_FOUND) {
            };
        }

        Publication publicationSaved = publicationRepository.findById(publication.getId()).get();
        publicationSaved.updateInfo(publication);
        return publicationRepository.save(publicationSaved);

    }

    @Transactional
    public PublicationFilterDTO getPublicationsByLike(int pag) {

        if (pag < 1) {
            throw new IllegalArgumentException(Constants.PAGE_INVALID);
        }

        int pagTop = 46;

        // Buscar si hay 3 paginaciones más adelante de la actual (1 Paginacion = 15)
        pag = (pag == 1) ? 0 : ((pag - 1) * Constants.PAGINATION_SIZE);
        List<Publication> publications = publicationRepository.publicationsBylike(pag, pagTop);

        return returnPublicationsWithPagination(publications);
    }

    @Transactional
    public PublicationFilterDTO getPublicationsByUser(int pag) {

        if (pag < 1) {
            throw new IllegalArgumentException(Constants.PAGE_INVALID);
        }

        int pagTop = 46;

        // Buscar si hay 3 paginaciones más adelante de la actual (1 Paginacion = 15)
        pag = (pag == 1) ? 0 : ((pag - 1) * Constants.PAGINATION_SIZE);
        List<Publication> publications = publicationRepository.publicationsByUser(pag, pagTop);

        return returnPublicationsWithPagination(publications);
    }

    @Transactional
    public PublicationFilterDTO getPublicationsByDate(int pag) {

        if (pag < 1) {
            throw new IllegalArgumentException(Constants.PAGE_INVALID);
        }

        int pagTop = 46;

        // Buscar si hay 3 paginaciones más adelante de la actual (1 Paginacion = 15)
        pag = (pag == 1) ? 0 : ((pag - 1) * Constants.PAGINATION_SIZE);
        List<Publication> publications = publicationRepository.publicationsByDate(pag, pagTop);

        return returnPublicationsWithPagination(publications);
    }

    @Transactional
    public PublicationFilterDTO getPublicationsByAleatory(int pag) {

        if (pag < 1) {
            throw new IllegalArgumentException(Constants.PAGE_INVALID);
        }

        int pagTop = 46;

        // Buscar si hay 3 paginaciones más adelante de la actual (1 Paginacion = 15)
        pag = (pag == 1) ? 0 : ((pag - 1) * Constants.PAGINATION_SIZE);
        List<Publication> publications = publicationRepository.publicationsByAleatory(pag, pagTop);

        return returnPublicationsWithPagination(publications);
    }

    @Transactional
    public PublicationFilterDTO getPublicationsByQuantity(int pag) {

        if (pag < 1) {
            throw new IllegalArgumentException(Constants.PAGE_INVALID);
        }

        int pagTop = 46;

        // Buscar si hay 3 paginaciones más adelante de la actual (1 Paginacion = 15)
        pag = (pag == 1) ? 0 : ((pag - 1) * Constants.PAGINATION_SIZE);
        List<Publication> publications = publicationRepository.publicationsByQuantity(pag, pagTop);

        return returnPublicationsWithPagination(publications);
    }

    // Funciones reutilizables:

    private PublicationFilterDTO returnPublicationsWithPagination(List<Publication> publications) {

        if (publications.isEmpty()) {
            throw new DataAccessException(Constants.PS_NOT_FOUND) {
            };
        }

        // Calcular número posible de paginaciones que hay en base de datos
        Double paginationDouble = (double) publications.size() / Constants.PAGINATION_SIZE;
        int pagination = (int) (Math.ceil(paginationDouble) - 1);

        if (publications.size() > Constants.PAGINATION_SIZE) {
            // Enviar solo 15 publicaciones que necesita el front
            publications = publications.subList(0, Constants.PAGINATION_SIZE);
        }

        return new PublicationFilterDTO(publications, pagination);
    }
}
