package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.*;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.repostitory.WaitingListRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class WaitingListServiceTest {
    @Autowired
    private WaitingListService waitingListService;
    @MockBean
    private WaitingListRepository waitingListRepository;

    @MockBean
    private  PersonService personService;
    @MockBean
    private MessageService messageService;
    private WaitingList waitingList;
    private Person person;
    @BeforeEach
    void setUp() {
         person = Person.builder()
                .id(1L)
                .build();
        waitingList = WaitingList.builder()
                .id(1L)
                .date(LocalDate.now())
                .hour(LocalTime.now())
                .person( person)
                .numberOfDiners(2)
                .build();
        Mockito.doReturn(waitingList).when(waitingListRepository).save(waitingList);
        Mockito.doReturn(person).when(personService).savePerson(person);
        Mockito.doReturn(Optional.empty()).when(waitingListRepository).findById(waitingList.getId());

    }
    @Test
    void addToWaitingListValid() {
        Mockito.doReturn(Optional.empty())
                .when(waitingListRepository).findByPersonIdAndDateAndHour(person.getId(), waitingList.getDate(), waitingList.getHour());
        var expectedResult = waitingList;
        var actualResult = waitingListService.addToWaitingList(waitingList);

        assert actualResult.getId().equals(expectedResult.getId());
    }
    @Test
    void addToWaitingLisTwice() {
        Mockito.doReturn(Optional.of(waitingList))
                .when(waitingListRepository).findByPersonIdAndDateAndHour(person.getId(), waitingList.getDate(), waitingList.getHour());
        var expectedException = ConflictException.class;
        assertThrows(expectedException, ()->waitingListService.addToWaitingList(waitingList));
    }
    @Test
    void updateWaitingListNotFound() {
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException, ()->waitingListService.updateWaitingList(waitingList));
    }

}