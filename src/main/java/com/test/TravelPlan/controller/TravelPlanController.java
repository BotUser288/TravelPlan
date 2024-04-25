package com.test.TravelPlan.controller;

import com.test.TravelPlan.domain.User;
import com.test.TravelPlan.service.TravelPlanService;
import com.test.TravelPlan.domain.Ticket;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
public class TravelPlanController {

    private final TravelPlanService  service;

    public TravelPlanController(TravelPlanService service)
    {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> get(@PathVariable(name="id") String id)
    {
        Ticket ticket = service.getTicketDetails(id);
        if(null!=ticket)
        {
            return ResponseEntity.ok().body(ticket);
        }
        else
            return ResponseEntity.notFound().build();

    }
    @GetMapping
    public ResponseEntity<Object[]> getAllSeats()
    {

        return ResponseEntity.ok().body(service.getTicketMap().values().toArray());


    }

    @GetMapping("/status/{coachId}")
    public ResponseEntity<Object[]> getAllSeats(@PathVariable(name="coachId") String coachId)
    {

        return ResponseEntity.ok().body(service.getTicketDetailsByCoach(coachId).values().toArray());


    }

    @PostMapping
    public ResponseEntity create(@RequestBody Ticket ticket){
        Ticket bookedTicket = service.bookTicket(ticket);
        if(null!=bookedTicket)
        {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/id")
                    .buildAndExpand(bookedTicket.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(bookedTicket);
        }
        else
            return ResponseEntity.unprocessableEntity().body("No Seats available");
    }

    @DeleteMapping("/{email}")
    public ResponseEntity deleteUser(@PathVariable(name="email") String email)
    {
        boolean userDeleted = service.deleteUser(email);
        if(userDeleted)
            return ResponseEntity.ok().body("User Deleted Successfully");
        else
            return ResponseEntity.notFound().build();
    }

}
