package us;


public class FUnitInitialize implements Runnable {
	protected int model;
	
	public FUnitInitialize(int model) {
		this.model = model;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	// 更新初始化状态
	public void setUnitState(String key, boolean state) {
		if (ControlParamters.equimentUnitMap.get(key) != null) {
			synchronized (ControlParamters.LK_EQUMENTUNIT) {
				ControlParamters.equimentUnitMap.get(key).setInitState(state);
			}
		} else {
			EquitmentUnit macPartsVO = new EquitmentUnit();
			macPartsVO.setUnitName(key);
			macPartsVO.setInitState(state);
			synchronized (ControlParamters.LK_EQUMENTUNIT) {
				ControlParamters.equimentUnitMap.put(key, macPartsVO);
			}
		}
	}
	
	// 更新异常状态
	public void setErrorCode(String key, String errorCode) {
		errorCode = errorCode + "00";
		errorCode = errorCode.substring(0, 2);
		if (ControlParamters.equimentUnitMap.get(key) != null) {
			synchronized (ControlParamters.LK_EQUMENTUNIT) {
				ControlParamters.equimentUnitMap.get(key).setErrCode(errorCode);
			}
		} else {
			EquitmentUnit macPartsVO = new EquitmentUnit();
			macPartsVO.setUnitName(key);
			macPartsVO.setErrCode(errorCode);
			synchronized (ControlParamters.LK_EQUMENTUNIT) {
				ControlParamters.equimentUnitMap.put(key, macPartsVO);
			}
		}
	}
}
