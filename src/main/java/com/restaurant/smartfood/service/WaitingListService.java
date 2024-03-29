package com.restaurant.smartfood.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.exception.BadRequestException;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.WaitingListRepository;
import com.restaurant.smartfood.security.JwtProperties;
import com.restaurant.smartfood.utility.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class WaitingListService {
    private final WaitingListRepository waitingListRepository;
    private final TableReservationService tableReservationService;
    private final MessageService messageService;
    private final PersonService personService;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    @Value("${domain-url}")
    private String domainUrl;
    @Value("${timezone.name}")
    private String timezone;
    @Value("${reservation-duration}")
    private int durationForReservation;
    @Value("${waiting-list.reserve-time}")
    private int minutesForWaitingListResponse;


    @Autowired
    public WaitingListService(WaitingListRepository waitingListRepository, @Lazy TableReservationService tableReservationService,
                              MessageService messageService, PersonService personService, TaskScheduler taskScheduler) {
        this.waitingListRepository = waitingListRepository;
        this.tableReservationService = tableReservationService;
        this.messageService = messageService;
        this.personService = personService;
        this.taskScheduler = taskScheduler;
    }

    @Transactional
    public WaitingList addToWaitingList(WaitingList waitingList) {
        Person person = personService.savePerson(waitingList.getPerson());
        waitingListRepository.findByPersonIdAndDateAndHour(person.getId(), waitingList.getDate(), waitingList.getHour())
                .ifPresent(w -> {
                    throw new ConflictException("There is already waiting list request with those details.");
                });
        waitingList.setWasNotified(false);
        messageService.sendMessages(waitingList.getPerson(), "Waiting List Request", getNewWaitingListMsg(waitingList));
        return waitingListRepository.save(waitingList);
    }

    private String getNewWaitingListMsg(WaitingList waitingList) {
        return "Hi " + waitingList.getPerson().getName() + ", Your reservation request for " + waitingList.getNumberOfDiners() + " diners at the date: "
                + waitingList.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " at " + waitingList.getHour() + ", is now in the waiting list." +
                " We will update you in case that it can be fulfilled. ";
    }

    @Transactional
    public WaitingList updateWaitingList(WaitingList waitingList) {
        WaitingList w = waitingListRepository.findById(waitingList.getId())
                .orElseThrow(() -> new ResourceNotFoundException("There is no waiting list request with those details."));
        if (waitingList.getDate().equals(w.getDate()) && waitingList.getHour().equals(w.getHour())) {
            w.setNumberOfDiners(waitingList.getNumberOfDiners());
            return waitingListRepository.save(w);
        }
        waitingListRepository.delete(w);
        return waitingListRepository.save(waitingList);
    }

    @Transactional
    public void deleteFromWaitingList(Long waitingListId) {
        waitingListRepository.delete(getWaitingListRequestById(waitingListId));
    }

    public List<WaitingList> getWaitingListByDateTime(String date, String hour) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalTime localHour = LocalTime.parse(hour, DateTimeFormatter.ofPattern("HH:mm"));
            return waitingListRepository.findByDateAndHour(localDate, localHour);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BadRequestException("The request was in bad format");
        }
    }

    public List<WaitingList> getWaitingListByMember(Long memberId) {
        return waitingListRepository.findByPersonId(memberId);
    }

    public WaitingList getWaitingListRequestById(Long waitingListId) {
        return waitingListRepository.findById(waitingListId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no waiting list request with this id: " + waitingListId));
    }

    @Async
    public void checkAllWaitingLists() {
        List<WaitingList> waitingList = waitingListRepository.findByDateIsGreaterThanEqual(LocalDate.now(ZoneId.of(timezone)));
        checkWaitingList(waitingList);
    }

    @Async
    public void checkWaitingListsForTime(LocalDate date, LocalTime hour) {
        List<WaitingList> waitingList = waitingListRepository
                .findByDateIsAndHourIsBetween(date, hour.minusHours(durationForReservation), Utils.hourPlusDurationForReservation(hour, durationForReservation));
        checkWaitingList(waitingList);
    }

    private void checkWaitingList(List<WaitingList> waitingList) {
        // Waiting list returned  fifo
        log.debug("waitingList: size -" + waitingList.size() + " , " + waitingList);
        if (waitingList.isEmpty())
            return;
        // Save all the available reservation to make sure that no one else is bypassing the queue
        Map<TableReservation, WaitingList> savedReservationsMap = saveWaitingAsReservations(waitingList);
        if (savedReservationsMap.isEmpty()) // if no reservation can be saved then this method is done.
            return;

        final int[] i = {0};
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(() -> {
            log.debug("savedReservation size: " + savedReservationsMap.size());
            Iterator<Map.Entry<TableReservation, WaitingList>> mapIterator = savedReservationsMap.entrySet().iterator();
            while (mapIterator.hasNext()) {
                Map.Entry<TableReservation, WaitingList> entry = mapIterator.next();
                TableReservation reservation = entry.getKey();
                WaitingList waitingRequest = entry.getValue();
                if (waitingListRepository.findById(waitingRequest.getId()).isPresent()) { // if waiting list was not deleted it means that the time passed, and we need to move to the next
                    waitingList.removeIf(w -> w.getId().equals(waitingRequest.getId()));
                    deleteFromWaitingList(waitingRequest.getId());  // delete table reservation and waiting list
                    tableReservationService.deleteWaitingListReservation(reservation);
                }
                mapIterator.remove();
            }
            log.debug("round: " + i[0] + " waitingList size: " + waitingList.size());
            i[0]++;
            if (waitingList.isEmpty() && scheduledFuture != null && !scheduledFuture.isCancelled()) {  // if no one is on waiting list we can stop this action, otherwise we send messages to rest of the list
                log.debug("Stopped because waiting list was empty");
                scheduledFuture.cancel(false);
            }
            savedReservationsMap.putAll(saveWaitingAsReservations(waitingList));
            if (savedReservationsMap.isEmpty() && scheduledFuture != null && !scheduledFuture.isCancelled()) {  // if no one is on waiting list we can stop this action, otherwise we send messages to rest of the list
                log.debug("Stopped because could not enter more reservations");
                scheduledFuture.cancel(false);
            }
        }, new Date(System.currentTimeMillis() + getMsForResponse()), getMsForResponse());

    }

    private long getMsForResponse() {
        return (long) minutesForWaitingListResponse * 60 * 1000;
    }

    private String getMessageForAvailableReservation(TableReservation savedReservation, WaitingList waitingListRequest) {
        String reservationToken = getReservationToken(savedReservation, waitingListRequest);
        String url = domainUrl + "/waiting-list/" + reservationToken;
        return "Hi, your reservation at: " + savedReservation.getDate() + " " + savedReservation.getHour() +
                " can now take place please confirm on the link: " + url +
                "<div>If you won't response in " + minutesForWaitingListResponse + " minutes, it will be cancelled automatically!</div>";
    }

    private String getReservationToken(TableReservation savedReservation, WaitingList waitingListRequest) {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // Create JWT Token
        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + getMsForResponse()))
                .withClaim("reservationId", savedReservation.getReservationId())
                .withClaim("waitingListId", waitingListRequest.getId())
                .sign(algorithm);
    }

    private Map<TableReservation, WaitingList> saveWaitingAsReservations(List<WaitingList> waitingList) {
        Map<TableReservation, WaitingList> savedReservations = new LinkedHashMap<>(); //Order is important
        Iterator<WaitingList> iterator = waitingList.iterator();
        while (iterator.hasNext()) {
            WaitingList waitingRequest = iterator.next();
            Optional<WaitingList> optionalWR = waitingListRepository.findById(waitingRequest.getId());
            if (!optionalWR.isPresent()) {
                iterator.remove();
                continue;
            }
            WaitingList waitingRequestInDB = optionalWR.get();
            if (waitingRequestInDB.getWasNotified()) {
                iterator.remove();
                continue;
            }
            TableReservation reservation = TableReservation.builder()
                    .date(waitingRequest.getDate())
                    .numberOfDiners(waitingRequest.getNumberOfDiners())
                    .hour(waitingRequest.getHour())
                    .person(waitingRequest.getPerson())
                    .build();
            try {
                TableReservation savedReservation = tableReservationService.addTableReservation(reservation, false);
                waitingRequestInDB.setWasNotified(true);
                waitingRequestInDB = waitingListRepository.save(waitingRequestInDB);
                savedReservations.put(savedReservation, waitingRequestInDB);
                // Send message to inform if reservation can be fulfilled
                messageService.sendMessages(savedReservation.getPerson(), "Request For Table Reservation",
                        getMessageForAvailableReservation(savedReservation, waitingRequestInDB));
                log.info("Send Reservation fulfilled requests to: " + savedReservation.getHour() + " " + reservation.getDate() + " " + reservation.getPerson().getName());
            } catch (Exception e) {
                if (!(e instanceof ConflictException)) {
                    log.warn("Unexpected response status");
                    log.error(e.getMessage());
                    throw e;
                }
            }
        }
        return savedReservations;
    }

    @Transactional
    public TableReservation approveReservation(String reservationToken) {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // parse the token and validate it
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(reservationToken);
            Long reservationId = decodedJWT.getClaim("reservationId").asLong();
            Long waitingListId = decodedJWT.getClaim("waitingListId").asLong();
            deleteFromWaitingList(waitingListId);
            return tableReservationService.getTableReservationById(reservationId);
        } catch (SignatureVerificationException e) {
            log.error("Authorization was failed . " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new BadCredentialsException("This link is invalid.");
        } catch (TokenExpiredException e) {
            log.warn("Token expired");
            throw new BadCredentialsException("This link is expired");
        } catch (Exception e) {

            log.error("Could not verify token for a certain reason");
            log.error(e.toString());
            throw new BadCredentialsException("This link is invalid");

        }
    }
}
