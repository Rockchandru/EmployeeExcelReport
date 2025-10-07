package com.example.util;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "main")
public class EmployeeSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sno")
    private Integer sNo;

    @Column(name = "employee_no")
    private String employeeId;

    @Column(name = "name")
    private String employeeName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "tower")
    private String tower;

    @Column(name = "floor_number")
    private String floor;

    @Column(name = "location") // ✅ Updated to match current DB column
    private String location;

    @Column(name = "time_of_sheet_update")
    private LocalDateTime swipeTime;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSNo() {
        return sNo;
    }

    public void setSNo(Integer sNo) {
        this.sNo = sNo;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getTower() {
        return tower;
    }

    public void setTower(String tower) {
        this.tower = tower;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getSwipeTime() {
        return swipeTime;
    }

    public void setSwipeTime(LocalDateTime swipeTime) {
        this.swipeTime = swipeTime;
    }
}
