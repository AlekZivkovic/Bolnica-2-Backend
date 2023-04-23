package com.raf.si.laboratoryservice.controllers;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.service.ReferralService;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/referral")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @PostMapping(path = "/create")
    public ResponseEntity<ReferralResponse> createReferral(@Valid @RequestBody CreateReferralRequest createReferralRequest) {
        return ResponseEntity.ok(referralService.createReferral(createReferralRequest));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping(value = "/history")
    public ResponseEntity<ReferralListResponse> referralHistory(@Valid @RequestParam UUID lbp,
                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
                                                                @RequestParam (defaultValue = "0") int page,
                                                                @RequestParam (defaultValue = "5") int size) {
        return ResponseEntity.ok(referralService.referralHistory(lbp, dateFrom, dateTo, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ReferralResponse> getReferral(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(referralService.getReferral(id));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ReferralResponse> deleteReferral(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(referralService.deleteReferral(id));
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR')" + "or hasRole('ROLE_LAB_TEHNICAR')")
    @GetMapping(value = "/unprocessed")
    public ResponseEntity<ReferralListResponse> unprocessedReferrals(@Valid @RequestParam UUID lbp) {
        return ResponseEntity.ok(referralService.unprocessedReferrals(lbp));
    }

}
