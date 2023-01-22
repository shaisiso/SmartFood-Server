package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.utility.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ShiftService {


    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;
    private final WebSocketService webSocketService;
    @Value("${timezone.name}")
    private String timezone;
    @Autowired
    public ShiftService(ShiftRepository shiftRepository, EmployeeRepository employeeRepository, WebSocketService webSocketService) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
        this.webSocketService = webSocketService;
    }

    public Shift startShift(Shift newShift) {
        if (newShift.getShiftEntrance() == null)
            newShift.setShiftEntrance(LocalDateTime.now(ZoneId.of(timezone)));
        Employee employee = employeeRepository.findByPhoneNumber(newShift.getEmployee().getPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException(  "There is no employee with phone number: " + newShift.getEmployee().getPhoneNumber()));
        validateFirstShiftStart(employee);
        newShift.setEmployee(employee);
        if (isManagers(employee)) {
            newShift.setIsApproved(true);
            return shiftRepository.save(newShift);
        } else { // notify shift manager
            Shift shift = shiftRepository.save(newShift);
            webSocketService.notifyShiftsChange(shift);
            return shift;
        }
    }

    private boolean isManagers(Employee employee) {
        return Arrays.asList(EmployeeRole.MANAGER, EmployeeRole.SHIFT_MANAGER, EmployeeRole.DELIVERY_MANAGER, EmployeeRole.KITCHEN_MANAGER, EmployeeRole.BAR_MANAGER)
                .contains(employee.getRole());
    }

    private void validateFirstShiftStart(Employee employee) {
        LocalDateTime dateStart = LocalDate.now(ZoneId.of(timezone)).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.now(ZoneId.of(timezone)).atTime(23, 59);
        shiftRepository.findByEmployeePhoneNumberAndShiftEntranceIsBetween(employee.getPhoneNumber(), dateStart, dateEnd)
                .stream().filter(shift -> shift.getShiftExit() == null)
                .findFirst()
                .ifPresent(shift -> {
                    throw new ConflictException( "You already started a shift");
                });
    }

    public Shift exitShift(Shift shift) {
        Long shiftId = shift.getShiftID() != null ? shift.getShiftID() : -1;
        Shift shiftFound = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException( "Shift was not found"));
        if (shiftFound.getShiftExit() == null)
            shiftFound.setShiftExit(LocalDateTime.now(ZoneId.of(timezone)));
        if (!isManagers(shift.getEmployee())) {
            Shift updatedShift = shiftRepository.save(shiftFound);
            webSocketService.notifyShiftsChange(updatedShift);
            return updatedShift;
        }
        return shiftRepository.save(shiftFound);
    }

    public Shift updateShift(Shift shift) {
        shiftRepository.findById(shift.getShiftID())
                .orElseThrow(() -> new ResourceNotFoundException( "The requested shift was not found"));
        if (!isManagers(shift.getEmployee())) {
            Shift updatedShift = shiftRepository.save(shift);
            webSocketService.notifyShiftsChange(updatedShift);
            return updatedShift;
        }
        return shiftRepository.save(shift);
    }

    public void deleteShift(Long shiftId) {
       Optional<Shift> optionalShift = shiftRepository.findById(shiftId);
       if(optionalShift.isPresent()) {
           Shift s =optionalShift.get();
            shiftRepository.delete(s);
            webSocketService.notifyShiftsChange(s);
        }else  {
            throw new ResourceNotFoundException( "The requested shift was not found");
        }
    }

    public List<Shift> getShiftsByEmployeeAndDates(String phoneNumber, String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            return shiftRepository.findByEmployeePhoneNumberAndShiftEntranceIsBetween(phoneNumber, start.atStartOfDay(), end.atTime(23, 59));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public List<Shift> getShiftsByDates(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            return shiftRepository.findByShiftEntranceIsBetween
                    (start.atStartOfDay(), end.atTime(23, 59));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException( "The request was in bad format");
        }
    }

    public List<Shift> getAllShiftsToApprove() {
        return shiftRepository.findByIsApproved(false);
    }

    public List<Employee> findAllDeliveryGuyInShift() {
        LocalDateTime start = LocalDate.now(ZoneId.of(timezone)).atStartOfDay();
        LocalDateTime end = LocalDate.now(ZoneId.of(timezone)).atTime(23,59);

        return  shiftRepository.findByShiftEntranceBetweenAndShiftExitIsNullAndEmployeeRoleIs(start,end,EmployeeRole.DELIVERY_GUY)
                .stream()
                .map(Shift::getEmployee)
                .collect(Collectors.toList());
    }
}
