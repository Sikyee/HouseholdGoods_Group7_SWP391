package com.example.appointment.controller;

import com.example.appointment.dto.AppointmentDTO;
import com.example.appointment.entity.Appointment;
import com.example.appointment.entity.BookingService;
import com.example.appointment.repository.BookingServiceRepository;
import com.example.appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    @GetMapping
    public List<Appointment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Appointment create(@RequestBody AppointmentDTO dto) {
        Appointment a = new Appointment();
        a.setId(dto.id != null ? dto.id : UUID.randomUUID().toString());
        a.setCustomerName(dto.customerName);
        a.setCustomerEmail(dto.customerEmail);
        a.setAppointmentTime(LocalDateTime.parse(dto.appointmentTime));
        a.setStatus(Appointment.Status.valueOf(dto.status));

        BookingService serviceEntity = bookingServiceRepository.findById(dto.serviceId).orElseThrow();
        a.setService(serviceEntity);

        return service.save(a);
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable String id, @RequestBody AppointmentDTO dto) {
        Appointment a = new Appointment();
        a.setId(id);
        a.setCustomerName(dto.customerName);
        a.setCustomerEmail(dto.customerEmail);
        a.setAppointmentTime(LocalDateTime.parse(dto.appointmentTime));
        a.setStatus(Appointment.Status.valueOf(dto.status));

        BookingService serviceEntity = bookingServiceRepository.findById(dto.serviceId).orElseThrow();
        a.setService(serviceEntity);

        return service.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
