package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.BlastSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlastScheduleRepository extends MongoRepository<BlastSchedule, String> {
    List<BlastSchedule> findByStatus(String status);

    List<BlastSchedule> findByBlastZone(String blastZone);

    List<BlastSchedule> findByDate(LocalDate date);

    List<BlastSchedule> findByAssignedOperator(String assignedOperator);

    List<BlastSchedule> findBySafetyOfficer(String safetyOfficer);

    List<BlastSchedule> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<BlastSchedule> findByBlastType(String blastType);
}
