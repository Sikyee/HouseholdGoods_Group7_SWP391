package com.example.appointment.service;

import com.example.appointment.entity.BookingService;
import com.example.appointment.repository.BookingServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceService {
    @Autowired
    private BookingServiceRepository repository;

    public List<BookingService> getAll() {
        return repository.findAll();
    }

    public Page<BookingService> getPagedServices(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    public BookingService getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public BookingService save(BookingService service) {
        if (service.getId() == null || service.getId().isEmpty()) {
            service.setId(UUID.randomUUID().toString());
        }
        return repository.save(service);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
