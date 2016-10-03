package com.synload.diary;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class Data {
	public String value;
	public String label;
	public String desc;
	public String icon;
	public Data (String val, String lab, String des, String ico){
		icon = ico;
		value = val;
		label = lab;
		desc = des;
	}
}
