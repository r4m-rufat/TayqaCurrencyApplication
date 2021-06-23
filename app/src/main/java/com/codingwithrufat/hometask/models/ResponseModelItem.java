package com.codingwithrufat.hometask.models;

public class ResponseModelItem{
	private String date;
	private String code;
	private double rate;
	private double inverseRate;
	private String name;
	private String alphaCode;
	private String numericCode;

	public String getDate(){
		return date;
	}

	public String getCode(){
		return code;
	}

	public double getRate(){
		return rate;
	}

	public double getInverseRate(){
		return inverseRate;
	}

	public String getName(){
		return name;
	}

	public String getAlphaCode(){
		return alphaCode;
	}

	public String getNumericCode(){
		return numericCode;
	}
}
