package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "operations")
@Getter
@Setter
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String description;

    @Column
    private Boolean deleted = false;

    //FK
    @Column(nullable = false)
    private UUID pbo;

    @ManyToOne
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;
}
