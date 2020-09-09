package com.rbm;

import java.util.ArrayList;
import java.util.List;

public class SubjectServices {
	
	public List<Subject> fetchSubjects() {
		List<Subject> allSubjects = new ArrayList<Subject>();
		for(int i=1; i<=10;i++) {
			Subject sub = new Subject("Subject "+i, i*10, i*3);
			allSubjects.add(sub);
		}
		return allSubjects;
	}

}
