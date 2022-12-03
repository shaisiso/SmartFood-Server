package com.restaurant.smartfood.service;


import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.repostitory.TableReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TableReservationService {

    @Autowired
    private TableReservationRepository tableReservationRepository;

    @Autowired
    private PersonService personService;



    public TableReservation saveTableReservation(TableReservation reservation) {
        personService.savePerson(reservation.getPerson());
        return tableReservationRepository.save(reservation);
        //TODO: check hours availability
        // יש בTABLE RESERVATION רשימה של הזמנות, דרך הרשימה נוציא את השולחנות שיהיה תפוסים בטווח שעות
        // יש לנו רשימה של כל השולנות במסעדה, נמחק ממנה את השולחנות שמופיעים הרשימה הראשונה
        // אם נותרו שולחנות פנויים - יופי, אם לא - לרשימת המתנה
            }

    public void deleteTableReservation(TableReservation reservation) {
        tableReservationRepository.findById(reservation.getReservationId()).
                ifPresentOrElse((t) -> tableReservationRepository.delete(reservation),
                        () -> {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "There is no reservation with ID: " + reservation.getReservationId());
                        });
        ;
    }

    public List<TableReservation> getTableReservationsByDates(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {// table reservations for a single day
            return tableReservationRepository.findByDate(startDate);
        }
        return tableReservationRepository.findByDateIsBetween(startDate, endDate);
    }

    public List<TableReservation> getTableReservationsByCustomer(String phoneNumber) {
        return tableReservationRepository.findByPersonPhoneNumber(phoneNumber);
    }

    public List<TableReservation> findAll() {
        return tableReservationRepository.findAll();
    }
}
