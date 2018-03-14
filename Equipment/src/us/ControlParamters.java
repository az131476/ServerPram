package us;

import java.util.HashMap;



public class ControlParamters {
	public static boolean frontAccessControl;
	public static boolean backAccessControl;
	
	// 后对射异常等待解决状态
	public static boolean backSensorDealState = true;
	// 后急停等待旋开状态
	public static boolean backEmgenceDealState = true;
	
	// 前对射异常待解决状态
	public static boolean frontSensorDealState = true;
	// 前翻板异常待解救状态
	public static boolean frontBeltDoorDealState = true;
	
	public static final Object LK_EQUMENTUNIT = new Object();
	public static HashMap<String, EquitmentUnit> equimentUnitMap = new HashMap<String, EquitmentUnit>();
}
