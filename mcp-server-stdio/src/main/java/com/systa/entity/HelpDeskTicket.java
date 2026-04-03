package com.systa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "helpdesk_tickets")
public record HelpDeskTicket(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        String username,
        String issue,
        String status,
        LocalDateTime createdAt,
        LocalDateTime eta){
}
