package com.gkudos.jasperserver.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Value Object
 * 
 * @author juan.mendez
 * 
 */
public class Report implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6082809270329433203L;
	/**
	 * 
	 */
	

	public static final String FORMAT_PDF = "PDF";
	public static final String FORMAT_EXCEL = "XLS";
	public static final String FORMAT_CSV = "CSV";

	private String url;
	private Map<String, String> params;
	private String format;
	private String outputFolder;

	/**
	 * 
	 */
	public Report() {
		format = FORMAT_PDF;
		params = new HashMap<String, String>();
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> parametros) {
		this.params = parametros;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, String value) {
		params.put(key, value);
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
