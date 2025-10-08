package com.example.appointment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {
    @Id
    private String id;

    private String customerName;
    private String customerEmail;
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private BookingService service;

    public enum Status {
        PENDING, CONFIRMED, CANCELLED
    }

}
