package com.example.appointment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
public class BookingService {
    @Id
    private String id;

    private String name;
    private String description;
    private int duration;
    private double price;

}
