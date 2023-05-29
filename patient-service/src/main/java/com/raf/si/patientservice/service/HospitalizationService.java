package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.UUID;

public interface HospitalizationService {
    HospitalizationResponse hospitalize(HospitalizationRequest request, String token);

    HospitalisedPatientsListResponse getHospitalisedPatients(String token, UUID pbo, UUID lbp,
                                                             String firstName, String lastName, String jmbg,
                                                             Pageable pageable);

    HospPatientByHospitalListResponse getHospitalisedPatientsByHospital(String token, UUID pbb, UUID lbp,
                                                                        String firstName, String lastName, String jmbg,
                                                                        Pageable pageable);

    PatientConditionResponse createPatientCondition(UUID lbp, PatientConditionRequest patientConditionRequest);

    PatientConditionListResponse getPatientConditions(UUID lbp, Date dateFrom, Date dateTo, Pageable pageable);
}
