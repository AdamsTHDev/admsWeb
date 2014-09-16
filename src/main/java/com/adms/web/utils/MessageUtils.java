package com.adms.web.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class MessageUtils {
	
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
	 * Add info message
	 * @param msg
	 */
	public static void addInfoMessage(final String msg) {
		addInfoMessage(null, msg);
	}
	
	/**
	 * Add info message
	 * @param clientId
	 * @param msg
	 */
	public static void addInfoMessage(final String clientId, final String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
		getExternalContext().getFlash().setKeepMessages(true);
	}
	
	/**
	 * Add error message
	 * @param msg
	 */
	public static void addErrorMessage(final String msg) {
		addErrorMessage(null, msg);
	}
	
	/**
	 * Add error message
	 * @param clientId
	 * @param msg
	 */
	public static void addErrorMessage(final String clientId, final String msg) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
		getExternalContext().getFlash().setKeepMessages(true);
	}
}
