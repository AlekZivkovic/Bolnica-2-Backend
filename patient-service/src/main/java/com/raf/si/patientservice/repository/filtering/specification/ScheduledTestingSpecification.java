package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledTesting;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledTestingFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ScheduledTestingSpecification implements Specification<ScheduledTesting> {

    private final ScheduledTestingFilter filter;

    public ScheduledTestingSpecification(ScheduledTestingFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<ScheduledTesting> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Patient> patient = root.get("patient");
        Path<LocalDateTime> dateAndTime = root.get("dateAndTime");

        final List<Predicate> predicates = new ArrayList<>();
        if (filter.getPatient() != null) {
            predicates.add(criteriaBuilder.equal(patient, filter.getPatient()));
        }
        if (filter.getDate() != null) {
            LocalDateTime startDate = filter.getDate().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endDate = startDate.plusDays(1);
            predicates.add(criteriaBuilder.between(dateAndTime, startDate, endDate));
        } else {
            LocalDateTime currDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(dateAndTime, currDate));
        }

        query.orderBy(criteriaBuilder.asc(dateAndTime));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
