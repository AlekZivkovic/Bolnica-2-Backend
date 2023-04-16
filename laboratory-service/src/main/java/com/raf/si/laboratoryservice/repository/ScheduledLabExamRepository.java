package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledLabExamRepository extends JpaRepository<ScheduledLabExam, Long>, JpaSpecificationExecutor<ScheduledLabExam> {
    @Query("SELECT COUNT(e) FROM ScheduledLabExam e WHERE e.pbo = :pbo AND e.scheduledDate >= :startDate AND e.scheduledDate < :endDate")
    long countByPboIdAndDateRange(@Param("pbo") UUID pboId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM ScheduledLabExam e WHERE e.pbo = :pbo AND e.scheduledDate >= :startDate AND e.scheduledDate < :endDate AND e.lbp = :lbp")
    List<ScheduledLabExam> findByPboAndDateRangeAndLbp(@Param("pbo") UUID pboId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("lbp") UUID lbp);
}
