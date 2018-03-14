package us;

import java.util.ArrayList;

class ChannelVO {
	private int rowNO;
	private int colNO;
	private int xPosition;
	private int yPosition;
	private int channelWidth;
	private int stockQty;
	private int needQty;
	private boolean checkFlag;

	public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int position) {
		xPosition = position;
	}

	public int getYPosition() {
		return yPosition;
	}

	public int getChannelWidth() {
		return channelWidth;
	}

	public void setChannelWidth(int channelWidth) {
		this.channelWidth = channelWidth;
	}

	public void setYPosition(int position) {
		yPosition = position;
	}

	public int getRowNO() {
		return rowNO;
	}

	public void setRowNO(int rowNO) {
		this.rowNO = rowNO;
	}

	public int getColNO() {
		return colNO;
	}

	public void setColNO(int colNO) {
		this.colNO = colNO;
	}

	public int getStockQty() {
		return stockQty;
	}

	public void setStockQty(int stockQty) {
		this.stockQty = stockQty;
	}

	public int getNeedQty() {
		return needQty;
	}

	public void setNeedQty(int needQty) {
		this.needQty = needQty;
	}

	public boolean isCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(boolean checkFlag) {
		this.checkFlag = checkFlag;
	}
	
}

public class InDrugParamters {
	private String key;
	private int drugLength;
	private int drugWidth;
	private int drugHeight;
	private String side;
	private int needQty;
	private ArrayList<ChannelVO> channelList;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getDrugLength() {
		return drugLength;
	}

	public void setDrugLength(int drugLength) {
		this.drugLength = drugLength;
	}

	public int getDrugWidth() {
		return drugWidth;
	}

	public void setDrugWidth(int drugWidth) {
		this.drugWidth = drugWidth;
	}

	public int getDrugHeight() {
		return drugHeight;
	}

	public void setDrugHeight(int drugHeight) {
		this.drugHeight = drugHeight;
	}

	public int getNeedQty() {
		return needQty;
	}

	public void setNeedQty(int needQty) {
		this.needQty = needQty;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public ArrayList<ChannelVO> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelVO> channelList) {
		this.channelList = channelList;
	}
	
	/**
	 * 
	 * @param code $S201 2 K1 L 000 000 000 000 xxxx*
	 * @return
	 */
	public boolean initCode(String code) {
		key = code.substring(6, 8);
		side = code.substring(8, 9);
		drugLength = Integer.parseInt(code.substring(9, 12));
		drugWidth = Integer.parseInt(code.substring(12, 15));
		drugHeight = Integer.parseInt(code.substring(15, 18));
		needQty = Integer.parseInt(code.substring(18, 21));
		return true;
	}

	// $S202 2 K1 L 1 N 000 M 000 000 001 002 000 000 xxxx*
	public boolean initInChannelCode(String code) {
		key = code.substring(6, 8);
		side = code.substring(8, 9);
		
		code = code.substring(9, code.length() - 5);
		
		ArrayList<ChannelVO> arrayList = new ArrayList<ChannelVO>();
		while (code.length() > 0) {
			ChannelVO channelVO = new ChannelVO();
			
			channelVO.setCheckFlag("1".equals(code.substring(0, 1)));
			code = code.substring(1);
			
			int xl = Integer.parseInt(code.substring(0, 1));
			channelVO.setXPosition(Integer.parseInt(code.substring(1, 1 + xl)));
			code = code.substring(1 + xl);
			
			int yl = Integer.parseInt(code.substring(0, 1));
			channelVO.setYPosition(Integer.parseInt(code.substring(1, 1 + yl)));
			code = code.substring(1 + yl);
			
			channelVO.setChannelWidth(Integer.parseInt(code.substring(0, 3)));
			code = code.substring(3);
			
			channelVO.setRowNO(Integer.parseInt(code.substring(0, 3)));
			code = code.substring(3);
			
			channelVO.setColNO(Integer.parseInt(code.substring(0, 3)));
			code = code.substring(3);
			
			channelVO.setStockQty(Integer.parseInt(code.substring(0, 3)));
			code = code.substring(3);
			
			channelVO.setNeedQty(Integer.parseInt(code.substring(0, 3)));
			code = code.substring(3);
			
			arrayList.add(channelVO);
		}
		setChannelList(arrayList);
		
		return true;
	}
}
