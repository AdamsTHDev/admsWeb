package com.adms.web.bean.base;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable {

	private static final long serialVersionUID = 1482050801277636503L;
	
	private String localeCode = "th";
	
	private static Map<String, String> countries;
	
	static {
		countries = new LinkedHashMap<>();
		countries.put("ภาษาไทย", "th");
		countries.put("English", "en");
	}
	
	public Map<String, String> getCountriesInMap() {
		return countries;
	}
	
	public String getLocaleCode() {
		return localeCode;
	}
	
	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
	
	public void countryLocaleCodeChanged(ValueChangeEvent value) {
		String newLocaleValue = value.getNewValue().toString();
		//loop country map to compare the locale code
		for (Map.Entry<String, String> entry : countries.entrySet()) {
			if(entry.getValue().toString().equals(newLocaleValue)){
				this.localeCode = newLocaleValue;
				setFacesContextLocale(this.localeCode);
			}
		}
	}
	
	public void setFacesContextLocale(String localeCode) {
		FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(this.localeCode));
	}
}
