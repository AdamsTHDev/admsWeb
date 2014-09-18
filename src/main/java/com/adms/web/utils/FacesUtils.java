package com.adms.web.utils;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;

/**
 * JSF utilities.
 */
public class FacesUtils {

	/**
	 * private constructor
	 */
	private FacesUtils() {
	}

	/**
	 * Get servlet context.
	 * 
	 * @return the servlet contexts
	 */
	public static ServletContext getServletContext() {
		return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	}

	public static ExternalContext getExternalContext() {
		final FacesContext fc = FacesContext.getCurrentInstance();
		return fc.getExternalContext();
	}

	public static HttpSession getHttpSession(final boolean create) {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(create);
	}

	/**
	 * Get managed bean based on the bean name.
	 * 
	 * @param beanName the bean name
	 * @return the managed bean associated with the bean name
	 */
	public static Object getManagedBean(final String beanName) {
		return getValueExpression(getJsfEl(beanName)).getValue(getElContext());
	}

	/**
	 * Remove the managed bean based on the bean name.
	 * 
	 * @param beanName the bean name of the managed bean to be removed
	 */
	public static void resetManagedBean(final String beanName) {
		getValueExpression(getJsfEl(beanName)).setValue(getElContext(), null);
	}

	/**
	 * Store the managed bean inside the session scope.
	 * 
	 * @param beanName the name of the managed bean to be stored
	 * @param managedBean the managed bean to be stored
	 */
	public static void setManagedBeanInSession(final String beanName, final Object managedBean) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
	}

	public static Map<String, Object> getRequestMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
	}

	public static Map<String, Object> getSessionMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	}

	public static Map<String, Object> getApplicationMap() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
	}

	/**
	 * Get parameter value from request scope.
	 * 
	 * @param name the name of the parameter
	 * @return the parameter value
	 */
	public static String getRequestParameter(final String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
	}

	/**
	 * 
	 * @param event
	 * @param name
	 * @return
	 */
	public static String getActionAttribute(final ActionEvent event, final String name) {
		return (String) event.getComponent().getAttributes().get(name);
	}

	/**
	 * Add information message.
	 * 
	 * @param msg the information message
	 */
	public static void addInfoMessage(final String msg) {
		addInfoMessage(null, msg);
	}

	/**
	 * Add information message to a specific client.
	 * 
	 * @param clientId the client id
	 * @param msg the information message
	 */
	public static void addInfoMessage(final String clientId, final String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
		getExternalContext().getFlash().setKeepMessages(true);
	}

	/**
	 * Add error message.
	 * 
	 * @param msg the error message
	 */
	public static void addErrorMessage(final String msg) {
		addErrorMessage(null, msg);
	}

	/**
	 * Add error message to a specific client.
	 * 
	 * @param clientId the client id
	 * @param msg the error message
	 */
	public static void addErrorMessage(final String clientId, final String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
		getExternalContext().getFlash().setKeepMessages(true);
	}

	/**
	 * Get resource bundle value from a given key
	 * 
	 * @param basename resource bundle basename
	 * @param Key resource bundle property key for retrieving value
	 * @param params parameters.
	 */
	public static String getResourceBundleValue(String key, Object[] params) {
		ResourceBundle bundle = 
				ResourceBundle.getBundle("com.s5.cgso.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		
		String msg = bundle.getString(key);	
		
		if (params != null && params.length != 0 ){
			msg = MessageFormat.format(msg, params);
		}				

		return msg;		
	}
	
	
	/**
	 * Get resource bundle value from a given key
	 * 
	 * @param basename resource bundle basename
	 * @param Key resource bundle property key for retrieving value
	 * @param 
	 */
	public static String getResourceBundleValue(String key) {
		ResourceBundle bundle = 
				ResourceBundle.getBundle("com.s5.cgso.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

		return bundle.getString(key);		
	}

	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	public static void executeJavaScript(String functionName, String ... parameters) {
		StringBuffer sb = new StringBuffer(functionName);
		sb.append('(');

		if (parameters != null && parameters.length > 0) {
			int i = 1;

			for (String param : parameters) {
				sb.append('\'');
				sb.append(param);
				sb.append('\'');

				if (i < parameters.length) {
					sb.append(", ");
				}

				i++;
			}
		}

		sb.append(')');

		RequestContext.getCurrentInstance().execute(sb.toString());
	}

	public static ValueExpression getValueExpression(final String el) {
		final ExpressionFactory expressionFactory = FacesContext.getCurrentInstance().getApplication()
				.getExpressionFactory();
		return expressionFactory.createValueExpression(getElContext(), el, Object.class);
	}
	
	/**
	 * Returns component by clientId
	 */
	public static UIComponent getComponentByClientId(String clientId) {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
	    
	    UIComponent c = findComponent(root, clientId);
	    return c;
	 }	
	
	/**
	 * Finds component with the given id
	 */	  
	public static UIComponent findComponent(UIComponent c, String id) {
		if (id.equals(c.getClientId())) {
			return c;
		}
		
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		
		return null;   
	}		

	// private
	@SuppressWarnings("unused")
	private static Application getApplication() {
		final ApplicationFactory appFactory = (ApplicationFactory) FactoryFinder
				.getFactory(FactoryFinder.APPLICATION_FACTORY);
		return appFactory.getApplication();
	}

	@SuppressWarnings("unused")
	private static HttpServletRequest getServletRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	@SuppressWarnings("unused")
	private static Object getElValue(final String el) {
		return getValueExpression(el).getValue(getElContext());
	}

	private static ELContext getElContext() {
		return FacesContext.getCurrentInstance().getELContext();
	}

	private static String getJsfEl(final String value) {
		return "#{" + value + "}";
	}

}
