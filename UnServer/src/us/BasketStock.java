package us;

public class BasketStock {
	private String cacheNO;
	private String checkCode;
	private String lightCode;
	private String closeCode;
	private boolean basket;
	private boolean light;

	public String getCacheNO() {
		return cacheNO;
	}

	public void setCacheNO(String cacheNO) {
		this.cacheNO = cacheNO;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getLightCode() {
		return lightCode;
	}

	public void setLightCode(String lightCode) {
		this.lightCode = lightCode;
	}

	public String getCloseCode() {
		return closeCode;
	}

	public void setCloseCode(String closeCode) {
		this.closeCode = closeCode;
	}

	public boolean isBasket() {
		return basket;
	}

	public void setBasket(boolean basket) {
		this.basket = basket;
	}

	public boolean isLight() {
		return light;
	}

	public void setLight(boolean light) {
		this.light = light;
	}
}
