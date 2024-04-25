package com.test.TravelPlan.service;

import com.test.TravelPlan.domain.Ticket;
import com.test.TravelPlan.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TravelPlanService {
    private static int maxSeats = 10;
    private static int maxCoaches = 2;

    private static HashMap<String, ArrayList<String>> coachMap = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, User> userMap = new HashMap<String, User>();
    private static HashMap<String, Ticket> ticketMap = new HashMap<String, Ticket>();

    public static HashMap<String, ArrayList<String>> getCoachMap() {
        return coachMap;
    }

    public static HashMap<String, Ticket> getTicketMap() {
        return ticketMap;
    }

    public static HashMap<String, User> getUserMap() {
        return userMap;
    }

    public TravelPlanService() {
    }

    public Ticket bookTicket(Ticket ticket){
        if(seatAvailable()){
            ticket.setId(blockAvailableSeat(ticket));
            ticketMap.put(ticket.getId(), ticket);

            return ticket;
        }
        else
            return null;
    }

    public Ticket getTicketDetails(String id)
    {

        return ticketMap.get(id);
    }

    public HashMap<String, Ticket> getTicketDetailsByCoach(String id)
    {
        HashMap<String, Ticket> ticketsByCoachMap =  new HashMap<String, Ticket>();
        for(Map.Entry<String, Ticket> entry: ticketMap.entrySet())
        {
            String ticketId = entry.getKey();
            if(ticketId.startsWith(id))
            {
                ticketsByCoachMap.put(entry.getKey(), entry.getValue());
            }
        }
        return ticketsByCoachMap;
    }

    private boolean seatAvailable()
    {
        if(ticketMap.size()<(maxSeats*maxCoaches))
            return true;
        return false;
    }

    public boolean deleteUser(String emailId)
    {
        HashMap<String, Ticket> ticketMap = getTicketMap();
        String ticketId = null;
        for(Map.Entry<String, Ticket> entry: ticketMap.entrySet())
        {
            User user = entry.getValue().getUser();

            if(emailId.equalsIgnoreCase(user.getEmailId())) {
                ticketId = entry.getKey();
                break;
            }
        }
        if(null!=ticketId)
        {
            ticketMap.remove(ticketId);
            userMap.remove(ticketId);
            String coachId = ticketId.substring(0,2);
            ArrayList<String> users = coachMap.get(coachId);
            users.remove(emailId);
            coachMap.put(coachId, users);

            return true;
        }
        return false;
    }



    private String blockAvailableSeat(Ticket ticket)
    {
        //getting available seat from each coach
        int coach = 1;
        do {
            String coachNumber = "C" + coach;
            if (coachMap.containsKey(coachNumber)) {
                ArrayList<String> users = coachMap.get(coachNumber);
                if(users.size()<maxSeats)
                {
                    User user = ticket.getUser();
                    users.add(user.getEmailId());
                    coachMap.put(coachNumber, users);
                    userMap.put(coachNumber+"S"+users.size(), user);
                    return coachNumber+"S"+users.size();
                }
            }
            else {
                ArrayList<String> users = new ArrayList<String>();
                User user = ticket.getUser();
                users.add(user.getEmailId());
                coachMap.put(coachNumber, users);
                userMap.put(coachNumber+"S1", user);
                return coachNumber+"S1";
            }
            coach++;
        }while(coach<=maxCoaches);
        return null;
    }
}
