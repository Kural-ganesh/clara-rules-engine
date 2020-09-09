package com.rbm;

public class Condition {
	
	private String conditionName;
	private String subjectName;
	
	public Condition(String conditionName, String subjectName) {
		super();
		this.conditionName = conditionName;
		this.subjectName = subjectName;
	}
	public String getConditionName() {
		return conditionName;
	}
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	

}
