package com.restaurant.smartfood.service;

import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.TakeAwayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TakeAwayServiceTest {
    @Autowired
    private TakeAwayService takeAwayService;
    @MockBean
    private TakeAwayRepository takeAwayRepository;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(Optional.empty()).when(takeAwayRepository).findById(2L);
    }
    @Test
    void deleteTakeAwayNotFound() {
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException,()->takeAwayService.deleteTakeAway(2l));
    }

    @Test
    void getTakeAwayListByDates() {
        var startDate ="22-13-2022";
        var endDate ="31-02-2023";

        var expectedException = BadRequestException.class;
        assertThrows(expectedException,()->takeAwayService.getTakeAwayListByDates(startDate,endDate));
    }

}