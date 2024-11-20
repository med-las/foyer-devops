package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.MethodOrderer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.repository.ReservationRepository;
import tn.esprit.tpfoyer.service.ReservationServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Use this for test ordering
@ExtendWith(MockitoExtension.class)
public class Test {

    @Mock
    ReservationRepository reservationRepository;

    @InjectMocks
    ReservationServiceImpl reservationService;

    Reservation reservation1;
    Reservation reservation2;

    @BeforeEach
    void setUp() {
        reservation1 = new Reservation("1", new Date(), true, null);
        reservation2 = new Reservation("2", new Date(), false, null);
    }

    @org.junit.jupiter.api.Test
    @Order(1)
    public void testRetrieveAllReservations() {
        // Arrange
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(reservation1, reservation2));

        // Act
        List<Reservation> reservations = reservationService.retrieveAllReservations();

        // Assert
        assertNotNull(reservations);
        assertEquals(2, reservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @org.junit.jupiter.api.Test
    @Order(2)
    public void testRetrieveReservation() {
        // Arrange
        String reservationId = "1";
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation1));

        // Act
        Reservation foundReservation = reservationService.retrieveReservation(reservationId);

        // Assert
        assertNotNull(foundReservation);
        assertEquals(reservation1.getIdReservation(), foundReservation.getIdReservation());
        verify(reservationRepository, times(1)).findById(reservationId);
    }

    @org.junit.jupiter.api.Test
    @Order(3)
    public void testAddReservation() {
        // Arrange
        when(reservationRepository.save(reservation1)).thenReturn(reservation1);

        // Act
        Reservation savedReservation = reservationService.addReservation(reservation1);

        // Assert
        assertNotNull(savedReservation);
        assertEquals(reservation1.getIdReservation(), savedReservation.getIdReservation());
        verify(reservationRepository, times(1)).save(reservation1);
    }

    @org.junit.jupiter.api.Test
    @Order(4)
    public void testModifyReservation() {
        // Arrange
        reservation1.setEstValide(false);
        when(reservationRepository.save(reservation1)).thenReturn(reservation1);

        // Act
        Reservation modifiedReservation = reservationService.modifyReservation(reservation1);

        // Assert
        assertNotNull(modifiedReservation);
        assertFalse(modifiedReservation.isEstValide());
        verify(reservationRepository, times(1)).save(reservation1);
    }

    @org.junit.jupiter.api.Test
    @Order(5)
    public void testRemoveReservation() {
        // Arrange
        String reservationId = "1";
        doNothing().when(reservationRepository).deleteById(reservationId);

        // Act
        reservationService.removeReservation(reservationId);

        // Assert
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }

    @org.junit.jupiter.api.Test
    @Order(6)
    public void testFindReservationsByDateAndStatus() {
        // Arrange
        Date testDate = new Date();
        when(reservationRepository.findAllByAnneeUniversitaireBeforeAndEstValide(testDate, true))
                .thenReturn(Arrays.asList(reservation1));

        // Act
        List<Reservation> reservations = reservationService.trouverResSelonDateEtStatus(testDate, true);

        // Assert
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        verify(reservationRepository, times(1))
                .findAllByAnneeUniversitaireBeforeAndEstValide(testDate, true);
    }
}
