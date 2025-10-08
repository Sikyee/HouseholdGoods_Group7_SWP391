package com.example.appointment.repository;

import com.example.appointment.entity.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingServiceRepository extends JpaRepository<BookingService, String> {
    Page<BookingService> findAll(Pageable pageable);
}
