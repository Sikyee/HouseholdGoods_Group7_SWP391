package com.example.appointment.controller;

import com.example.appointment.entity.BookingService;
import com.example.appointment.service.BookingServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class BookingServiceController {
    @Autowired
    private BookingServiceService service;

    @GetMapping
    public List<BookingService> getAll() {
        return service.getAll();
    }

    @GetMapping("/paged")
    public Page<BookingService> getPaged(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        return service.getPagedServices(page, size);
    }

    @GetMapping("/{id}")
    public BookingService getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public BookingService create(@RequestBody BookingService s) {
        return service.save(s);
    }

    @PutMapping("/{id}")
    public BookingService update(@PathVariable String id, @RequestBody BookingService s) {
        s.setId(id);
        return service.save(s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
