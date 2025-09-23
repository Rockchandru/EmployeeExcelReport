package com.example.util;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employee_swipe")
public class EmployeeSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "sno")
    private Integer sNo;
    
    @Column(name = "employee_no")
    private String employeeId;

    @Column(name = "name")
    private String employeeName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "floor_number")
    private String floor;

    @Column(name = "time_of_sheet_update")
    private LocalDateTime swipeTime;

    
    
    
    // Getters and Setters

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
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

    public String getFloor() {
        return floor;
    }
    public void setFloor(String floor) {
        this.floor = floor;
    }

    public LocalDateTime getSwipeTime() {
        return swipeTime;
    }
    public void setSwipeTime(LocalDateTime swipeTime) {
        this.swipeTime = swipeTime;
    }
    
	@Override
	public String toString() {
		return "EmployeeSwipe [id=" + id + ", sNo=" + sNo + ", employeeId=" + employeeId + ", employeeName="
				+ employeeName + ", designation=" + designation + ", floor=" + floor + ", swipeTime=" + swipeTime + "]";
	}

    
}











































/*
 * package com.example.util;
 * 
 * import java.time.LocalDateTime;
 * 
 * import jakarta.persistence.*;
 * 
 * @Entity
 * 
 * @Table(name = "employee_swipe") public class EmployeeSwipe {
 * 
 * @Id
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-increment
 * primary key private Long id;
 * 
 * 
 * @Column(name = "employee_id") private Long employeeId;
 * 
 * @Column(name = "employee_name") private String employeeName;
 * 
 * private String designation;
 * 
 * private String floor;
 * 
 * @Column(name = "swipe_time") private LocalDateTime swipeTime;
 * 
 * // ✅ Getters and Setters public Long getId() { return id; } public void
 * setId(Long id) { this.id = id; }
 * 
 * public Long getEmployeeId() { return employeeId; } public void
 * setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
 * 
 * public String getEmployeeName() { return employeeName; } public void
 * setEmployeeName(String employeeName) { this.employeeName = employeeName; }
 * 
 * public String getDesignation() { return designation; } public void
 * setDesignation(String designation) { this.designation = designation; }
 * 
 * public String getFloor() { return floor; } public void setFloor(String floor)
 * { this.floor = floor; }
 * 
 * public LocalDateTime getSwipeTime() { return swipeTime; } public void
 * setSwipeTime(LocalDateTime swipeTime) { this.swipeTime = swipeTime; }
 * 
 * 
 * @Override public String toString() { return "EmployeeSwipe [id=" + id +
 * ", employeeId=" + employeeId + ", employeeName=" + employeeName +
 * ", designation=" + designation + ", floor=" + floor + ", swipeTime=" +
 * swipeTime + "]"; } }
 * 
 */