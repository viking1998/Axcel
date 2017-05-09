package com.spring.hibernate.axcel.models;

import java.util.HashMap;
import java.util.List;

public class ExcelSheetWrapper {
	
	private Integer rows;
	private HashMap<Integer, List<MyCell>> cells;
	
	public ExcelSheetWrapper() {
		// TODO Auto-generated constructor stub
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public HashMap<Integer, List<MyCell>> getCells() {
		if(cells == null) {
			cells = new HashMap<Integer, List<MyCell>>();
		}
		return cells;
	}

	public void setCells(HashMap<Integer, List<MyCell>> cells) {
		this.cells = cells;
	}

}
