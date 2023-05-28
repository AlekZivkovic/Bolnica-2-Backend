package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.service.AppointmentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request,
                                                                 @RequestHeader("Authorization") String authorizationHeader) {

        return ResponseEntity.ok(appointmentService.createAppointment(request, authorizationHeader));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping
    public ResponseEntity<AppointmentListResponse> getAppointments(@Valid @RequestParam(required = false) UUID lbp,
                                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
                                                                   @RequestHeader("Authorization") String authorizationHeader,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(appointmentService.getAppointments(lbp, date, authorizationHeader, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PutMapping("/change-status/{id}")
    public ResponseEntity<AppointmentResponse> changeStatus(@PathVariable("id") Long id,
                                                            @RequestParam String status,
                                                            @RequestHeader("Authorization") String authorizationHeader) {

        return ResponseEntity.ok(appointmentService.changeStatus(id, status, authorizationHeader));
    }
}
