package us;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class OutDrugByRowVO {
	private int rowNO;
	private boolean pFlag;
	private HashMap<String, String> colHashMap = new HashMap<String, String>();
	private HashMap<String, String> resultHashMap = new HashMap<String, String>();

	public int getRowNO() {
		return rowNO;
	}

	public void setRowNO(int rowNO) {
		this.rowNO = rowNO;
	}

	public boolean isPFlag() {
		return pFlag;
	}

	public void setPFlag(boolean flag) {
		pFlag = flag;
	}

	public HashMap<String, String> getColHashMap() {
		return colHashMap;
	}

	public HashMap<String, String> getResultHashMap() {
		return resultHashMap;
	}
}

public class OutParamters {
	private String outterNO;
	private String winNO;
	private String patient;
	private String procCode;
	private String key;
	private HashMap<Integer, OutDrugByRowVO> rowHashMap;
	private ArrayList<Integer> dealRowSortList;

	public String getOutterNO() {
		return outterNO;
	}

	public void setOutterNO(String outterNO) {
		this.outterNO = outterNO;
	}

	public String getWinNO() {
		return winNO;
	}

	public void setWinNO(String winNO) {
		this.winNO = winNO;
	}

	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public String getProcCode() {
		return procCode;
	}

	public void setProcCode(String procCode) {
		this.procCode = procCode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public HashMap<Integer, OutDrugByRowVO> getRowHashMap() {
		return rowHashMap;
	}

	public ArrayList<Integer> getDealRowSortList() {
		return dealRowSortList;
	}

	//$Q401 2 K1 00 00 N 组号  00 000 00 00 00 1 xxxx*
	public boolean initOutCode(String code) {
		code = code.substring(5, code.length() - 5);
		key = code.substring(1, 3);
		outterNO = code.substring(3, 5);
		winNO = code.substring(5, 7);
		code = code.substring(7);
		procCode = code.substring(2, 2 + Integer.parseInt(code.substring(0, 2)));
		code = code.substring(2 + Integer.parseInt(code.substring(0, 2)));
		
		int[] sortArr = new int[code.length()/12];
		HashMap<Integer, String> sortMap = new HashMap<Integer, String>();
		int i = 0;
		while (code.length() > 0) {
			sortArr[i++] = Integer.parseInt(code.substring(0, 5));
			sortMap.put(Integer.parseInt(code.substring(0, 5)), code.substring(0, 12));
			code = code.substring(12);
		}
		
		Arrays.sort(sortArr);
		
		rowHashMap = new HashMap<Integer, OutDrugByRowVO>();
		dealRowSortList = new ArrayList<Integer>();
	//	for (int j = sortArr.length - 1; j >= 0; j--) {
		for (int j = 0; j < sortArr.length; j++) {
			code = sortMap.get(sortArr[j]);
			
			int row = Integer.parseInt(code.substring(0, 2));
			OutDrugByRowVO vo = rowHashMap.get(row);
			if (vo == null) {
				dealRowSortList.add(row);
				
				vo = new OutDrugByRowVO();
				vo.setRowNO(row);
				vo.getColHashMap().put(code.substring(2, 5), code.substring(2, 11));//列+数量+电+电
				vo.setPFlag(code.substring(11, 12).equals("1"));
			} else {
				vo.getColHashMap().put(code.substring(2, 5), code.substring(2, 11));
				if (!vo.isPFlag()) {
					vo.setPFlag(code.substring(11, 12).equals("1"));
				}
			}
			rowHashMap.put(row, vo);
		}
		
		return true;
	}
}
