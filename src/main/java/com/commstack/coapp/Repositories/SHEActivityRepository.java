package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.SHEActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SHEActivityRepository extends MongoRepository<SHEActivity, String> {
    List<SHEActivity> findByActivityType(String activityType);

    List<SHEActivity> findByStatus(String status);

    List<SHEActivity> findByDate(LocalDate date);

    List<SHEActivity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<SHEActivity> findByLocation(String location);

    List<SHEActivity> findByConductedBy(String conductedBy);

    List<SHEActivity> findByDepartment(String department);

    List<SHEActivity> findBySupervisor(String supervisor);

    List<SHEActivity> findByRiskLevel(String riskLevel);

    List<SHEActivity> findByFollowUpRequired(boolean followUpRequired);

    List<SHEActivity> findByFollowUpDateBeforeAndFollowUpStatus(LocalDate date, String status);

    List<SHEActivity> findByDeadlineForActionsBeforeAndStatus(LocalDate date, String status);
}
