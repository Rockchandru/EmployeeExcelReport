package com.example.dto;

public class EmployeeFloorSummary {

	private Integer sNo;
    private String employeeId; // ✅ Changed from Long to String
    private String employeeName;
    private String designation;
    private Long floorA;
    private Long floorB;
    private Long floorC;
    private Long floorD;
    private Long floorE;
    private Long total;

    // ✅ Constructor used in JPQL projection
    public EmployeeFloorSummary(Integer sNo, String employeeId, String employeeName, String designation,
                                Long floorA, Long floorB, Long floorC, Long floorD, Long floorE) {
    	this.sNo = sNo;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.designation = designation;
        this.floorA = floorA != null ? floorA : 0;
        this.floorB = floorB != null ? floorB : 0;
        this.floorC = floorC != null ? floorC : 0;
        this.floorD = floorD != null ? floorD : 0;
        this.floorE = floorE != null ? floorE : 0;
        this.total = this.floorA + this.floorB + this.floorC + this.floorD + this.floorE;
    }

	// ✅ Getters and Setters
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

    public Long getFloorA() {
        return floorA;
    }

    public void setFloorA(Long floorA) {
        this.floorA = floorA;
    }

    public Long getFloorB() {
        return floorB;
    }

    public void setFloorB(Long floorB) {
        this.floorB = floorB;
    }

    public Long getFloorC() {
        return floorC;
    }

    public void setFloorC(Long floorC) {
        this.floorC = floorC;
    }

    public Long getFloorD() {
        return floorD;
    }

    public void setFloorD(Long floorD) {
        this.floorD = floorD;
    }

    public Long getFloorE() {
        return floorE;
    }

    public void setFloorE(Long floorE) {
        this.floorE = floorE;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}

