package com.example.appointment.service;

import com.example.appointment.entity.Appointment;
import com.example.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository repository;

    public List<Appointment> getAll() {
        return repository.findAll();
    }

    public Appointment getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Appointment save(Appointment appointment) {
        if (appointment.getId() == null || appointment.getId().isEmpty()) {
            appointment.setId(UUID.randomUUID().toString());
        }
        appointment.setStatus(Appointment.Status.PENDING);
        return repository.save(appointment);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
