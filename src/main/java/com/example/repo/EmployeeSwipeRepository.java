package com.example.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dto.EmployeeFloorSummary;
import com.example.util.EmployeeSwipe;

@Repository
public interface EmployeeSwipeRepository extends JpaRepository<EmployeeSwipe, Integer> {

    @Query("""
        SELECT new com.example.dto.EmployeeFloorSummary(
            0,
            e.employeeId,
            e.employeeName,
            e.designation,
            SUM(CASE WHEN e.tower = 'A' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.tower = 'B' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.tower = 'C' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.tower = 'D' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.tower = 'E'  THEN 1 ELSE 0 END)
        )
        FROM EmployeeSwipe e
        WHERE e.swipeTime BETWEEN :start AND :end
          AND TRIM(UPPER(e.location)) = UPPER(:location)
        GROUP BY e.employeeId, e.employeeName, e.designation
    """)
    List<EmployeeFloorSummary> getTowerWiseSummaryBetween(@Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end,
                                                          @Param("location") String location);
}
