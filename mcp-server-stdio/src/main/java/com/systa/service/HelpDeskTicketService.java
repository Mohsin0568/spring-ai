package com.systa.service;

import com.systa.entity.HelpDeskTicket;
import com.systa.model.TicketRequest;
import com.systa.repository.HelpDeskTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpDeskTicketService {

    private final HelpDeskTicketRepository helpDeskTicketRepository;

    public HelpDeskTicket createTicket(TicketRequest ticketInput) {
        HelpDeskTicket ticket =
                new HelpDeskTicket(0l, ticketInput.issue(),ticketInput.username(),
                        "OPEN",LocalDateTime.now(),LocalDateTime.now().plusDays(7));
        return helpDeskTicketRepository.save(ticket);
    }

    public List<HelpDeskTicket> getTicketsByUsername(String username) {
        return helpDeskTicketRepository.findByUsername(username);
    }

}
