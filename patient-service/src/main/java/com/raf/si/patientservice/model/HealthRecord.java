package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date registrationDate = new Date(System.currentTimeMillis());

    @Column
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column
    @Enumerated(EnumType.STRING)
    private RHFactor rhFactor;

    @Column
    private Boolean deleted = false;

    @OneToOne(mappedBy = "healthRecord", fetch = FetchType.LAZY)
    private Patient patient;

    @OneToMany(mappedBy = "healthRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Allergy> allergies;

    @OneToMany(mappedBy = "healthRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Vaccination> vaccinations;

    @OneToMany(mappedBy = "healthRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Operation> operations;

    @OneToMany(mappedBy = "healthRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MedicalExamination> medicalExaminations;

    @OneToMany(mappedBy = "healthRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MedicalHistory> medicalHistory;
}
