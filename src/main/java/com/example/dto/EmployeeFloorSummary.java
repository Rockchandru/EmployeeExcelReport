package com.example.dto;
public class EmployeeFloorSummary {
    private Integer sNo;
    private String employeeId;
    private String employeeName;
    private String designation;
    private Long towerA;
    private Long towerB;
    private Long towerC;
    private Long towerD;
    private Long towerE;
    private Long total;

    public EmployeeFloorSummary(Integer sNo, String employeeId, String employeeName,
                                String designation,
                                Long towerA, Long towerB, Long towerC, Long towerD ,Long towerE) {
        this.sNo = sNo;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.designation = designation;
        this.towerA = towerA != null ? towerA : 0;
        this.towerB = towerB != null ? towerB : 0;
        this.towerC = towerC != null ? towerC : 0;
        this.towerD = towerD != null ? towerD : 0;
        this.towerE =towerE  !=null ?  towerE : 0;
        this.total = this.towerA + this.towerB + this.towerC + this.towerD +this.towerE;
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

	public Long getTowerA() {
		return towerA;
	}

	public void setTowerA(Long towerA) {
		this.towerA = towerA;
	}

	public Long getTowerB() {
		return towerB;
	}

	public void setTowerB(Long towerB) {
		this.towerB = towerB;
	}

	public Long getTowerC() {
		return towerC;
	}

	public void setTowerC(Long towerC) {
		this.towerC = towerC;
	}

	public Long getTowerD() {
		return towerD;
	}

	public void setTowerD(Long towerD) {
		this.towerD = towerD;
	}

	public Long getTowerE() {
		return towerE;
	}

	public void setTowerE(Long towerE) {
		this.towerE = towerE;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}

