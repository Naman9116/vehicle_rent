package com.util;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class DatePropertyEditor extends PropertyEditorSupport {
	String[] formats = { "yyyy-MM-dd", "MM/dd/yyyy", "MM/dd/yy" };
	Date defaultDate = new Date(0L);

	public void setAsText(String textValue) {
		if (textValue == null) {
			setValue(defaultDate);
			return;
		}
		Date retDate = defaultDate;
		try {
			retDate = DateUtils.parseDate(textValue, formats);
		} 
		catch (ParseException e) {
		}
		setValue(retDate);
	}
}
