package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.exception.UnprocessableEntityException;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TableReservationServiceTest {
    @Autowired
    private TableReservationService tableReservationService;
    @MockBean
    private TableReservationRepository tableReservationRepository;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(Optional.empty()).when(tableReservationRepository).findById(2L);
    }

    @Test
    void addNewReservationPastDate() {
        var reservation = TableReservation.builder()
                .date(LocalDate.now().minusDays(1))
                .hour(LocalTime.now())
                .numberOfDiners(3)
                .build();
        var expectedException = UnprocessableEntityException.class;
        assertThrows(expectedException, () -> tableReservationService.addTableReservation(reservation));
    }

    @Test
    void updateReservationNotFound() {
        var reservation = TableReservation.builder()
                .reservationId(2L)
                .date(LocalDate.now().plusDays(2))
                .hour(LocalTime.now())
                .numberOfDiners(3)
                .build();
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException, () -> tableReservationService.updateTableReservation(reservation));
    }
    @Test
    void getReservationsByDates() {
        var startDate ="22-13-2022";
        var endDate ="31-02-2023";

        var expectedException = BadRequestException.class;
        assertThrows(expectedException,()->tableReservationService.getTableReservationsByDates(startDate,endDate));
    }
}