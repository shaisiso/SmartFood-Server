package com.restaurant.smartfood.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.restaurant.smartfood.entities.TableReservation;
import com.restaurant.smartfood.entities.WaitingList;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.WaitingListRepository;
import com.restaurant.smartfood.security.JwtProperties;
import com.restaurant.smartfood.utility.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
        var person = personService.savePerson(waitingList.getPerson());
        waitingListRepository.findByPersonIdAndDateAndHour(person.getId(), waitingList.getDate(), waitingList.getHour())
                .ifPresent(w -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "There is already waiting list request with those details.");
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
        var w = waitingListRepository.findById(waitingList.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no waiting list request with those details."));
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request was in bad format");
        }
    }

    public List<WaitingList> getWaitingListByMember(Long memberId) {
        return waitingListRepository.findByPersonId(memberId);
    }

    public WaitingList getWaitingListRequestById(Long waitingListId) {
        return waitingListRepository.findById(waitingListId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no waiting list request with this id: " + waitingListId));
    }

    @Async
    public void checkWaitingLists(LocalDate date, LocalTime hour) {
        var waitingList = waitingListRepository
                .findByDateIsAndHourIsBetween(date, hour.minusHours(durationForReservation), Utils.hourPlusDurationForReservation(hour, durationForReservation));
        // Waiting list returned  fifo
        log.debug("waitingList: size -" + waitingList.size() + " , " + waitingList);
        // Save all the available reservation to make sure that no one else is bypassing the queue
        Map<TableReservation, WaitingList> savedReservationsMap = saveWaitingAsReservations(waitingList);
        if (savedReservationsMap.isEmpty()) // if no reservation can be saved then this method is done.
            return;

        final int[] i = {0};
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(() -> {
            log.debug("savedReservation size: " + savedReservationsMap.size());
            var mapIterator = savedReservationsMap.entrySet().iterator();
            while (mapIterator.hasNext()) {
                var entry = mapIterator.next();
                var reservation = entry.getKey();
                var waitingRequest = entry.getValue();
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


        // Start timer for response
//        var timer = new Timer();
//        timer.schedule(new TimerTask() {
//            private int i=0;
//            @Override
//            public void run() {
//                log.debug("savedReservation from timer: size: " + savedReservationsMap.size());
//                var mapIterator = savedReservationsMap.entrySet().iterator();
//                while (mapIterator.hasNext()) {
//                    var entry= mapIterator.next();
//                    var reservation = entry.getKey();
//                    var waitingRequest =entry.getValue();
//                    if (waitingListRepository.findById(waitingRequest.getId()).isPresent()) { // if waiting list was not deleted it means that the time passed, and we need to move to the next
//                        waitingList.removeIf(w -> w.getId().equals(waitingRequest.getId()));
//                        deleteFromWaitingList(waitingRequest.getId());  // delete table reservation and waiting list
//                        tableReservationService.deleteWaitingListReservation(reservation);
//                    }
//                    mapIterator.remove();
//                }
//                log.debug("round: "+i+" waitingList size: " + waitingList.size());
//                i++;
//                if (waitingList.isEmpty()){  // if no one is on waiting list we can stop this action, otherwise we send messages to rest of the list
//                    log.debug("Stopped because waiting list was empty");
//                    cancel();
//                }
//
//                savedReservationsMap.putAll(saveWaitingAsReservations(waitingList));
//                if (savedReservationsMap.isEmpty()) {  // if no one is on waiting list we can stop this action, otherwise we send messages to rest of the list
//                    log.debug("Stopped because could not enter more reservations");
//                    cancel();
//                }
//            }
//        }, 30*1000, 30*1000);
    }

    private long getMsForResponse() {
        return minutesForWaitingListResponse * 60 * 1000;
    }

    private String getMessageForAvailableReservation(TableReservation savedReservation, WaitingList waitingListRequest) {
        var reservationToken = getReservationToken(savedReservation, waitingListRequest);
        // var url = domainUrl + "/waiting-list/" + reservationToken; // TODO: Return this
        var url = reservationToken;
        return "Hi, your reservation at: " + savedReservation.getDate() + " " + savedReservation.getHour() +
                " can now take place please confirm on the link: " + url + "<div>If you won't response in 2 hours it will be cancelled automatically!</div>";
    }

    private String getReservationToken(TableReservation savedReservation, WaitingList waitingListRequest) {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // Create JWT Token
        String token = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + getMsForResponse()))
                .withClaim("reservationId", savedReservation.getReservationId())
                .withClaim("waitingListId", waitingListRequest.getId())
                .sign(algorithm);
        return token;
    }

    private Map<TableReservation, WaitingList> saveWaitingAsReservations(List<WaitingList> waitingList) {
        Map<TableReservation, WaitingList> savedReservations = new LinkedHashMap<>(); //Order is important
        var iterator = waitingList.iterator();
        while (iterator.hasNext()) {
            var waitingRequest = iterator.next();
            var optionalWR = waitingListRepository.findById(waitingRequest.getId());
            if (!optionalWR.isPresent()){
                iterator.remove();
                continue;
            }
            var waitingRequestInDB = optionalWR.get();
            if (waitingRequestInDB.getWasNotified()){
                iterator.remove();
                continue;
            }
            var reservation = TableReservation.builder()
                    .date(waitingRequest.getDate())
                    .numberOfDiners(waitingRequest.getNumberOfDiners())
                    .hour(waitingRequest.getHour())
                    .person(waitingRequest.getPerson())
                    .build();
            try {
                var savedReservation = tableReservationService.saveTableReservation(reservation, false);
                waitingRequestInDB.setWasNotified(true);
                waitingRequestInDB = waitingListRepository.save(waitingRequestInDB);
                savedReservations.put(savedReservation, waitingRequestInDB);
                // Send message to inform if reservation can be fulfilled
                messageService.sendMessages(savedReservation.getPerson(), "Request For Table Reservation",
                        getMessageForAvailableReservation(savedReservation, waitingRequestInDB));
                log.info("Send Reservation fulfilled requests to: " + savedReservation.getHour() + " " + reservation.getDate() + " " + reservation.getPerson().getName());
            } catch (ResponseStatusException e) {
                if (!e.getStatus().equals(HttpStatus.CONFLICT)) {
                    log.warn("Unexpected response status");
                    throw e;
                }
            }
        }

        return savedReservations;
    }

    public TableReservation approveReservation(String reservationToken) {
        Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
        // parse the token and validate it
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(reservationToken);
            var reservationId = decodedJWT.getClaim("reservationId").asLong();
            var waitingListId = decodedJWT.getClaim("waitingListId").asLong();
            deleteFromWaitingList(waitingListId);
            return tableReservationService.getTableReservationById(reservationId);
        } catch (SignatureVerificationException e) {
            log.error("Authorization was failed . " + e.getMessage());
            log.error("Token was changed and cannot be trusted");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This link is invalid.");
        } catch (TokenExpiredException e) {
            log.warn("Token expired");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This link is expired");
        } catch (Exception e) {
            if (e instanceof ResponseStatusException)
                throw e;
            else {
                log.error("Could not verify token for a certain reason");
                log.error(e.toString());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This link is invalid");
            }
        }
    }
}
