package com.systa.ai.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "help_desk_ticket")
public record HelpDeskTicket(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        String username,
        String issueDescription,
        String status,
        LocalDate createdDate,
        LocalDate resolvedDate
) {
}
