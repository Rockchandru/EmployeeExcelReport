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
    		e.sNo,
            e.employeeId,
            e.employeeName,
            e.designation,
            SUM(CASE WHEN e.floor = 'A' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.floor = 'B' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.floor = 'C' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.floor = 'D' THEN 1 ELSE 0 END),
            SUM(CASE WHEN e.floor = 'E' THEN 1 ELSE 0 END)
        )
        FROM EmployeeSwipe e
        WHERE e.swipeTime BETWEEN :start AND :end
        GROUP BY e.sNo, e.employeeId, e.employeeName, e.designation
    """)
    List<EmployeeFloorSummary> getDailyFloorSummary(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);
}












































/*
 * package com.example.repo;
 * 
 * import java.time.LocalTime; import java.util.List; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.data.jpa.repository.Query; import
 * org.springframework.data.repository.query.Param; import
 * org.springframework.stereotype.Repository;
 * 
 * import com.example.dto.EmployeeFloorSummary; import
 * com.example.util.EmployeeSwipe;
 * 
 * @Repository public interface EmployeeSwipeRepository extends
 * JpaRepository<EmployeeSwipe, Long> {
 * 
 * @Query(""" SELECT new com.example.dto.EmployeeFloorSummary( e.employeeId,
 * e.employeeName, e.designation, SUM(CASE WHEN e.floor = 'F1' THEN 1 ELSE 0
 * END), SUM(CASE WHEN e.floor = 'F2' THEN 1 ELSE 0 END), SUM(CASE WHEN e.floor
 * = 'F3' THEN 1 ELSE 0 END), SUM(CASE WHEN e.floor = 'F4' THEN 1 ELSE 0 END),
 * SUM(CASE WHEN e.floor = 'F5' THEN 1 ELSE 0 END) ) FROM EmployeeSwipe e WHERE
 * FUNCTION('TIME', e.swipeTime) BETWEEN :start AND :end GROUP BY e.employeeId,
 * e.employeeName, e.designation """) List<EmployeeFloorSummary>
 * getDailyFloorSummary(@Param("start") LocalTime start,
 * 
 * @Param("end") LocalTime end); }
 * 
 * 
 * 
 * 
 */



