package us;

import java.util.HashMap;



public class ControlParamters {
	public static boolean frontAccessControl;
	public static boolean backAccessControl;
	
	// ������쳣�ȴ����״̬
	public static boolean backSensorDealState = true;
	// ��ͣ�ȴ�����״̬
	public static boolean backEmgenceDealState = true;
	
	// ǰ�����쳣�����״̬
	public static boolean frontSensorDealState = true;
	// ǰ�����쳣�����״̬
	public static boolean frontBeltDoorDealState = true;
	
	public static final Object LK_EQUMENTUNIT = new Object();
	public static HashMap<String, EquitmentUnit> equimentUnitMap = new HashMap<String, EquitmentUnit>();
}
