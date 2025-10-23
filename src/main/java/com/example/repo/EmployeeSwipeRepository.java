package com.example.repo;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.util.EmployeeSwipe;

@Repository
public interface EmployeeSwipeRepository extends JpaRepository<EmployeeSwipe, Integer> {

    @Query(value = """
        SELECT 
            0 AS sNo,
            e.employee_no AS employeeId,
            e.name AS employeeName,
            e.designation AS designation,
            SUM(CASE WHEN e.tower = 'A' THEN 1 ELSE 0 END) AS towerA,
            SUM(CASE WHEN e.tower = 'B' THEN 1 ELSE 0 END) AS towerB,
            SUM(CASE WHEN e.tower = 'C' THEN 1 ELSE 0 END) AS towerC,
            SUM(CASE WHEN e.tower = 'D' THEN 1 ELSE 0 END) AS towerD,
            SUM(CASE WHEN e.tower = 'E' THEN 1 ELSE 0 END) AS towerE
        FROM (
            SELECT 
                employee_no,
                name,
                designation,
                tower,
                location,
                DATE_FORMAT(time_from_device, '%Y-%m-%d %H:%i') AS swipe_minute
            FROM main
            WHERE time_of_sheet_update BETWEEN :start AND :end
              AND TRIM(UPPER(location)) = UPPER(:location)
            GROUP BY employee_no, tower, swipe_minute
        ) AS e
        GROUP BY e.employee_no, e.name, e.designation
        ORDER BY e.employee_no
    """, nativeQuery = true)
    List<Object[]> getTowerWiseSummaryBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("location") String location);
}


























/*
 * import java.time.LocalDateTime; import java.util.List;
 * 
 * import org.springframework.data.jpa.repository.*; import
 * org.springframework.data.repository.query.Param; import
 * org.springframework.stereotype.Repository;
 * 
 * import com.example.dto.EmployeeFloorSummary; import
 * com.example.util.EmployeeSwipe;
 * 
 * @Repository public interface EmployeeSwipeRepository extends
 * JpaRepository<EmployeeSwipe, Integer> {
 * 
 * @Query(value = """ SELECT 0 AS sNo, e.employee_no AS employeeId, e.name AS
 * employeeName, e.designation AS designation, SUM(CASE WHEN e.tower = 'A' THEN
 * 1 ELSE 0 END) AS towerA, SUM(CASE WHEN e.tower = 'B' THEN 1 ELSE 0 END) AS
 * towerB, SUM(CASE WHEN e.tower = 'C' THEN 1 ELSE 0 END) AS towerC, SUM(CASE
 * WHEN e.tower = 'D' THEN 1 ELSE 0 END) AS towerD, SUM(CASE WHEN e.tower = 'E'
 * THEN 1 ELSE 0 END) AS towerE FROM ( SELECT DISTINCT employee_no, name,
 * designation, tower, location, DATE_FORMAT(time_from_device, '%Y-%m-%d %H:%i')
 * AS swipe_minute, time_of_sheet_update FROM main WHERE time_of_sheet_update
 * BETWEEN :start AND :end AND TRIM(UPPER(location)) = UPPER(:location) ) AS e
 * GROUP BY e.employee_no, e.name, e.designation ORDER BY e.employee_no """,
 * nativeQuery = true)
 * 
 * List<EmployeeFloorSummary> getTowerWiseSummaryBetween(@Param("start")
 * LocalDateTime start,
 * 
 * @Param("end") LocalDateTime end,
 *
 * @Param("location") String location);
 * 
 * 
 * List<Object[]> getTowerWiseSummaryBetween(@Param("start") LocalDateTime
 * start,
 * 
 * @Param("end") LocalDateTime end,
 * 
 * @Param("location") String location); }
 */