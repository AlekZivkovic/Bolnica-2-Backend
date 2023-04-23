package com.raf.si.laboratoryservice.controller;

import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import com.raf.si.laboratoryservice.services.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/order")
public class WorkOrderController {
    private final WorkOrderService orderService;

    public WorkOrderController(WorkOrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('ROLE_LAB_TEHNICAR') or hasRole('ROLE_VISI_LAB_TEHNICAR')")
    @PostMapping("/create/{orderId}")
    public ResponseEntity<OrderResponse> createWorkOrder(@PathVariable("orderId") Long orderId){
        OrderResponse response = orderService.createOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_NACELNIK_ODELJENJA') or hasRole('ROLE_DOKTOR_SPEC') or hasRole('DOKTOR_SPEC_POV')")
    @GetMapping("/history")
    public ResponseEntity<OrderHistoryResponse> orderHistory(@Valid @RequestBody OrderHistoryRequest historyRequest,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size){
        return ResponseEntity.ok(orderService.orderHistory(historyRequest, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_MED_BIOHEM') or hasRole('ROLE_SPEC_MED_BIOHEM')")
    @PutMapping("/saveResult")
    public ResponseEntity<SaveResultResponse> saveResult(@Valid @RequestBody SaveResultRequest saveRequest){
        return ResponseEntity.ok(orderService.saveResult(saveRequest));
    }

    @PreAuthorize("hasRole('ROLE_NACELNIK_ODELJENJA') or hasRole('ROLE_DOKTOR_SPEC') " +
            "or hasRole('ROLE_DOKTOR_SPEC_POV') or hasRole('ROLE_LAB_TEHNICAR')" +
            " or hasRole('ROLE_VISI_LAB_TEHNICAR') or hasRole('ROLE_MED_BIOHEM')" +
            "or hasRole('ROLE_SPEC_MED_BIOHEM')")
    @GetMapping("/results/{orderId}")
    public ResponseEntity<ResultResponse> getResults(@PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(orderService.getResults(orderId));
    }

    @PreAuthorize("hasRole('ROLE_LAB_TEHNICAR') or hasRole('ROLE_VISI_LAB_TEHNICAR')" +
            " or hasRole('ROLE_MED_BIOHEM') or hasRole('ROLE_SPEC_MED_BIOHEM')")
    @PostMapping("/historyForLab")
    public ResponseEntity<OrderHistoryResponse> orderHistoryForLab(@Valid @RequestBody OrderHistoryForLabRequest historyRequest,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size){
        return ResponseEntity.ok(orderService.orderHistoryForLab(historyRequest, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('SPEC_MED_BIOHEM')")
    @PostMapping("/verify/{orderId}")
    public ResponseEntity<OrderResponse> verify(@PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(orderService.verify(orderId));
    }
}
