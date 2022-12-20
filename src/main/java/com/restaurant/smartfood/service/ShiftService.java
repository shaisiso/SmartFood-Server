package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.EmployeeRole;
import com.restaurant.smartfood.entities.Shift;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.ShiftRepository;
import com.restaurant.smartfood.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShiftService {


    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;
    private final WebSocketService webSocketService;
    @Value("${timezone.name}")
    private String timezone;

    public Shift startShift(Shift newShift) {
        if (newShift.getShiftEntrance() == null)
            newShift.setShiftEntrance(LocalDateTime.now(ZoneId.of(timezone)));
        var employee = employeeRepository.findByPhoneNumber(newShift.getEmployee().getPhoneNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There is no employee with phone number: " + newShift.getEmployee().getPhoneNumber()));
        validateFirstShiftStart(employee);
        newShift.setEmployee(employee);
        if (isManagers(employee)) {
            newShift.setIsApproved(true);
            return shiftRepository.save(newShift);
        } else { // notify shift manager
            var shift = shiftRepository.save(newShift);
            webSocketService.notifyShiftsChange(shift);
            return shift;
        }
    }

    private boolean isManagers(Employee employee) {
        return List.of(EmployeeRole.MANAGER, EmployeeRole.SHIFT_MANAGER, EmployeeRole.DELIVERY_MANAGER, EmployeeRole.KITCHEN_MANAGER, EmployeeRole.BAR_MANAGER)
                .contains(employee.getRole());
    }

    private void validateFirstShiftStart(Employee employee) {
        var dateStart = LocalDate.now(ZoneId.of(timezone)).atStartOfDay();
        var dateEnd = LocalDate.now(ZoneId.of(timezone)).atTime(23, 59);
        shiftRepository.findByEmployeePhoneNumberAndShiftEntranceIsBetween(employee.getPhoneNumber(), dateStart, dateEnd)
                .stream().filter(shift -> shift.getShiftExit() == null)
                .findFirst()
                .ifPresent(shift -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You already started a shift");
                });
    }

    public Shift exitShift(Shift shift) {
        var shiftId = shift.getShiftID() != null ? shift.getShiftID() : -1;
        var shiftFound = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift was not found"));
        if (shiftFound.getShiftExit() == null)
            shiftFound.setShiftExit(LocalDateTime.now(ZoneId.of(timezone)));
        if (!isManagers(shift.getEmployee())) {
            var updatedShift = shiftRepository.save(shiftFound);
            webSocketService.notifyShiftsChange(updatedShift);
            return updatedShift;
        }
        return shiftRepository.save(shiftFound);
    }

    public Shift updateShift(Shift shift) {
        shiftRepository.findById(shift.getShiftID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested shift was not found"));
        if (!isManagers(shift.getEmployee())) {
            var updatedShift = shiftRepository.save(shift);
            webSocketService.notifyShiftsChange(updatedShift);
            return updatedShift;
        }
        return shiftRepository.save(shift);
    }

    public void deleteShift(Long shiftId) {
        shiftRepository.findById(shiftId).ifPresentOrElse(s -> {
            shiftRepository.delete(s);
            webSocketService.notifyShiftsChange(s);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested shift was not found");
        });
    }

    public List<Shift> getShiftsByEmployeeAndDates(String phoneNumber, String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            return shiftRepository.findByEmployeePhoneNumberAndShiftEntranceIsBetween(phoneNumber, start.atStartOfDay(), end.atTime(23, 59));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<Shift> getAllShiftsToApprove() {
        return shiftRepository.findByIsApproved(false);
    }
}
