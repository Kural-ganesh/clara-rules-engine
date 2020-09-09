package com.iqvia.rbm.rbm_webapp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.rbm.Condition;
import com.rbm.Subject;
import com.rbm.SubjectServices;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import clara.rules.QueryResult;
import clara.rules.RuleLoader;
import clara.rules.WorkingMemory;
import rules.clojava;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	String offers = "discount my-discount 15 when customer status is platinum. "+
    			   "discount extra-discount 10 when customer status is gold and total value > 200. "+
    			   "promotion free-widget-month free-widget when customer status is gold and order month is august.";
        final VerticalLayout layout = new VerticalLayout();    
        setContent(layout);
        SubjectServices subjectServices = new SubjectServices();
        List<Subject> allSubjects = subjectServices.fetchSubjects();
        
        layout.addComponent(new Label("All Subjects"));
        allSubjects.forEach(sub -> layout.addComponent(new Label(sub.getSubName() + " with Bp "+ sub.getBp() + " and age "+ sub.getAge())));
        
        TextArea rulesTextArea = new TextArea();
        layout.addComponent(rulesTextArea);
        
        Button applyRules = new Button("Apply Rules");
        layout.addComponent(applyRules);
        
        VerticalLayout rulesOutput =  new VerticalLayout();
        layout.addComponent(rulesOutput);
        
        applyRules.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				String rule = rulesTextArea.getValue();
				rulesOutput.removeAllComponents();
				
				clojava clja = new clojava();
		        WorkingMemory wm = (WorkingMemory) clja.javamethod(rule);
		        System.out.println("Working memory created");
//		        WorkingMemory wm = RuleLoader.loadRules("rules.condition-rules");
		        Iterable<QueryResult> res = wm.insert(allSubjects).fireRules().query("rules.clojava/get-conditions");
		        
		        List<Condition> conditions = new ArrayList<>();
		        res.forEach(r -> conditions.add((Condition) r.getResult("?con")));
		        
		        conditions.forEach(con -> rulesOutput.addComponent(new Label(con.getSubjectName() + " has "+ con.getConditionName())));
			}
		});
        
        
        
        
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
