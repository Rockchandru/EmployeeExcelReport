package com.example.dto;

public class EmployeeFloorSummary {

	private Integer sNo;
    private String employeeId; // Changed from Long to String
    private String employeeName;
    private String designation;
    private String location;
    private Long floorF1;
    private Long floorF2;
    private Long floorF3;
    private Long floorF4;
    private Long floorF5;
    private Long floorF6;
    private Long floorF7;
    private Long floorB;
    private Long total;

   	public EmployeeFloorSummary(Integer sNo, String employeeId, String employeeName, String designation, String location, Long floorF1,
			Long floorF2, Long floorF3, Long floorF4, Long floorF5, Long floorF6, Long floorF7, Long floorB) {
		super();
		this.sNo = sNo;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.designation = designation;
		this.location=location;
		this.floorF1 = floorF1 != null ? floorF1: 0;
		this.floorF2 = floorF2 != null ? floorF2: 0;
		this.floorF3 = floorF3 != null ? floorF3: 0;
		this.floorF4 = floorF4 != null ? floorF4: 0;
		this.floorF5 = floorF5 != null ? floorF5: 0;
		this.floorF6 = floorF6 != null ? floorF6: 0;
		this.floorF7 = floorF7 != null ? floorF7: 0;
		this.floorB = floorB != null ? floorB: 0;
		this.total = this.floorF1 + this.floorF2 + this.floorF3 + this.floorF4 + this.floorF5 + this.floorF6 + this.floorF7 + this.floorB;
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

	
	public void setlocation(String location) {
		this.location=location;
	}
	
	public String getlocation() {
		return location;
	}
	
	public Long getFloorF1() {
		return floorF1;
	}

	public void setFloorF1(Long floorF1) {
		this.floorF1 = floorF1;
	}

	public Long getFloorF2() {
		return floorF2;
	}

	public void setFloorF2(Long floorF2) {
		this.floorF2 = floorF2;
	}

	public Long getFloorF3() {
		return floorF3;
	}

	public void setFloorF3(Long floorF3) {
		this.floorF3 = floorF3;
	}

	public Long getFloorF4() {
		return floorF4;
	}

	public void setFloorF4(Long floorF4) {
		this.floorF4 = floorF4;
	}

	public Long getFloorF5() {
		return floorF5;
	}

	public void setFloorF5(Long floorF5) {
		this.floorF5 = floorF5;
	}

	public Long getFloorF6() {
		return floorF6;
	}

	public void setFloorF6(Long floorF6) {
		this.floorF6 = floorF6;
	}

	public Long getFloorF7() {
		return floorF7;
	}

	public void setFloorF7(Long floorF7) {
		this.floorF7 = floorF7;
	}

	public Long getFloorB() {
		return floorB;
	}

	public void setFloorB(Long floorB) {
		this.floorB = floorB;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	
}

