package com.codingwithrufat.hometask.models;

import java.util.ArrayList;

/**
 * is combined ResponseModelItem to the list and this class the model of base json
 */
public class ResponseModel{
	private ArrayList<ResponseModelItem> responseModel;

	public ArrayList<ResponseModelItem> getResponseModel(){
		return responseModel;
	}
}