package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShiftServiceTest {
    @Autowired
    private ShiftService shiftService;
    @MockBean
    private ShiftRepository shiftRepository;
    @MockBean
    private WebSocketService webSocketService;
    @MockBean
    private EmployeeRepository employeeRepository;
    @Value("${timezone.name}")
    private String timezone;
    private Shift shift;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .phoneNumber("0500000000")
                .id(1L)
                .role(EmployeeRole.MANAGER)
                .build();
        shift = Shift.builder()
                .shiftID(1L)
                .shiftEntrance(LocalDateTime.now(ZoneId.of(timezone)))
                .employee(employee)
                .build();

        Mockito.doReturn(Optional.of(shift)).when(shiftRepository).findById(shift.getShiftID());
        Mockito.doReturn(shift).when(shiftRepository).save(shift);
        Mockito.doReturn(Optional.of(employee)).when(employeeRepository).findByPhoneNumber(employee.getPhoneNumber());
        Mockito.doNothing().when(webSocketService).notifyShiftsChange(shift);
    }

    @Test
    @DisplayName("Exit shift with valid details should set the shift exit hour to the current time")
    void exitShiftWhenDetailsValid() {
        // arrange
        var expectedResult = LocalDateTime.now(ZoneId.of(timezone)).withNano(0);
        //act
        var actualResult = shiftService.exitShift(shift).getShiftExit().withNano(0);
        // assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Exit shift with shift id that doesn't exist should throw ResourceNotFoundException")
    void exitShiftWhenShiftNotFound() {
        // arrange
        shift.setShiftID(2L);
        var expectedException = ResourceNotFoundException.class;

        // act & assert
        assertThrows(expectedException, () -> shiftService.exitShift(shift));
    }


    @Test
    @DisplayName("Start shift with valid details should set the shift start to the current time")
    void startShiftValidDetails() {
        LocalDateTime dateStart = LocalDate.now(ZoneId.of(timezone)).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.now(ZoneId.of(timezone)).atTime(23, 59);
        Mockito.doReturn(new ArrayList<Shift>())
                .when(shiftRepository).findByEmployeePhoneNumberAndShiftEntranceIsBetween(employee.getPhoneNumber(), dateStart, dateEnd);

        var expectedResult = LocalDateTime.now(ZoneId.of(timezone)).withNano(0);

        var actualResult = shiftService.startShift(shift).getShiftEntrance().withNano(0);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    @DisplayName("Start shift twice a day without exiting previous should throw ConflictException")
    void startShiftTwice() {
        LocalDateTime dateStart = LocalDate.now(ZoneId.of(timezone)).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.now(ZoneId.of(timezone)).atTime(23, 59);
        Mockito.doReturn(Arrays.asList(shift))
                .when(shiftRepository).findByEmployeePhoneNumberAndShiftEntranceIsBetween(employee.getPhoneNumber(), dateStart, dateEnd);
        var expectedException = ConflictException.class;
        assertThrows(expectedException, () -> shiftService.startShift(shift));
    }
    @Test
    @DisplayName("Start shift with employee not exist details should throw ResourceNotFoundException")
    void startShiftEmployeeNotExists() {
        Mockito.doReturn(Optional.empty()).when(employeeRepository).findByPhoneNumber(employee.getPhoneNumber());

        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException, () -> shiftService.startShift(shift));
    }


    @Test
    @DisplayName("get shift by dates in bad format should throw BadRequestException ")
    void getShiftsBadFormat() {
        var expectedException = BadRequestException.class;
        assertThrows(expectedException, () -> shiftService.getShiftsByDates("33-1-22","1/2/22"));
    }
}