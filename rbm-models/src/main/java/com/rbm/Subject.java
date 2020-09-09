package com.rbm;

public class Subject {
	
	private String subName;
	private int age;
	private int bp;
	
	
	public Subject(String subName, int age, int bp) {
		super();
		this.subName = subName;
		this.age = age;
		this.bp = bp;
	}
	
	public String getSubName() {
		return subName;
	}
	public void setSubName(String subName) {
		this.subName = subName;
	}
	
	public int getBp() {
		return bp;
	}
	public void setBp(int bp) {
		this.bp = bp;
	}

	public int getAge() {
		return age; 
	}

	public void setAge(int age) {
		this.age = age;
	}
	

}
