package us;

import java.util.ArrayList;

class ModbusCode {
	public final static int MOBUS_DATA_ONE_INT = 0x01;
	public final static int MOBUS_DATA_TWO_INT = 0x02;
	public final static int MOBUS_DATA_ONE_REAL = 0x03;
	public final static int MOBUS_DATA_TWO_REAL = 0x04;
	
	/**
	 * 8λ16����תΪ10����
	 * @param hexString
	 * @return
	 */
	public static int hexToInt(String hexString) {
		if (hexString.substring(0, 1).toUpperCase().equals("F")) {
			// ����
			String string = "";
			for (int i = 0; i < 8; i++) {
				String str = hexString.substring(i, i + 1);
				if (str.equals("0")) {
					string += "F";
				} else if (str.equals("1")) {
					string += "E";
				} else if (str.equals("2")) {
					string += "D";
				} else if (str.equals("3")) {
					string += "C";
				} else if (str.equals("4")) {
					string += "B";
				} else if (str.equals("5")) {
					string += "A";
				} else if (str.equals("6")) {
					string += "9";
				} else if (str.equals("7")) {
					string += "8";
				} else if (str.equals("8")) {
					string += "7";
				} else if (str.equals("9")) {
					string += "6";
				} else if (str.equals("A")) {
					string += "5";
				} else if (str.equals("B")) {
					string += "4";
				} else if (str.equals("C")) {
					string += "3";
				} else if (str.equals("D")) {
					string += "2";
				} else if (str.equals("E")) {
					string += "1";
				} else if (str.equals("F")) {
					string += "0";
				} else {

				}
			}
			
			return -Integer.parseInt(string, 16) - 1;
		} else {
			// ����
			return Integer.parseInt(hexString, 16);
		}
	}
	
	/**
	 * ��λʮ������ת��Ϊ������
	 * @param hexString	��λʮ�������ַ���
	 * @return	����ֵ
	 */
	public static double hexToDouble (String hexString) {
		double realValue = 0;
		String tString = "";
		for (int i = 0; i < 8; i++) {
			String tpString = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			tString += tpString.substring(tpString.length() - 4);
		}
		int flag = Integer.parseInt(tString.substring(0, 1));
		int power = Integer.parseInt(tString.substring(1, 9), 2);
		double value = 0;
		for (int i = 9; i < 32; i++) {
			value += Math.pow(2, -(i-8)) * Integer.parseInt(tString.substring(i, i + 1));
		}
		realValue = Math.pow(-1, flag) * (1 + value) * Math.pow(2, (power - 127));
		return realValue;
	}
	
	/**
	 * ���Modbusдָ��
	 * @param data	�洢����ʼ��ַ��10����
	 * @param value	д���ֵ
	 * @param len	����
	 * @return		дָ��
	 */
	public synchronized static String wCodeFormat(int data, int[] value, int[] len) {
		String code = "";
		int length = 0;
		for (int i = 0; i < value.length; i++) {
			if (len[i] == 1) {
				code += String.format("%04X", value[i]);
			} else {
				String str = String.format("%08X", value[i]);
				code = code + str.substring(4) + str.substring(0, 4);
			}
			length += len[i];
		}
		return "0000000000" + String.format("%02X", length*2 + 7) + "0110" + String.format("%04X", data) + String.format("%04X", length) + String.format("%02X", length*2) + code;
	}
	
	/**
	 * ���Modbus��ȡָ��
	 * @param data 	�洢����ʼ��ַ��10����
	 * @param len  	��ȡ����
	 * @return		��ȡָ��
	 */
	public synchronized static String rCodeFormat(int data, int[] len) {
		int lenght = 0;
		for (int i : len) {
			lenght += i;
		}
		return String.format("0000000000060103%04X%04X", data, lenght);
	}
	
	//81���Ƿ��Ĺ����롣���յ��Ĺ�����EDA ģ�鲻֧�֡�
	//82����ȡ�Ƿ������ݵ�ַ��ָ��������λ�ó���EDA ģ��Ŀɶ�ȡ�ĵ�ַ��Χ��
	//83���Ƿ�������ֵ�����յ��������͵�����ֵ����EDA ģ����Ӧ��ַ�����ݷ�Χ��
	public synchronized static ArrayList<Integer> codeParse(String code, int[] len) {
		ArrayList<Integer> valueList = null;
		if (code.equals("")) {
			// EDA����ָ����Ч���ж�
			return null;
		} else {
			valueList = new ArrayList<Integer>();
			int aLen = 0;
			for (int i : len) {
				switch (i) {
				case ModbusCode.MOBUS_DATA_ONE_INT:
					aLen += 1;
					break;
				case ModbusCode.MOBUS_DATA_TWO_INT:
					aLen += 2;
					break;
				case ModbusCode.MOBUS_DATA_ONE_REAL:
					aLen += 1;
					break;
				case ModbusCode.MOBUS_DATA_TWO_REAL:
					aLen += 2;
					break;
				default:
					break;
				}
			}
			// ��ȡ���ص�ֵ����
			code = code.substring(code.length() - aLen*4);
			for (int i : len) {
				switch (i) {
				case ModbusCode.MOBUS_DATA_ONE_INT:
					valueList.add(Integer.parseInt(code.substring(0, 4), 16));
					code = code.substring(4);
					break;
				case ModbusCode.MOBUS_DATA_TWO_INT:
					valueList.add(hexToInt(code.substring(0, 8)));
					code = code.substring(8);
					break;
				case ModbusCode.MOBUS_DATA_ONE_REAL:
					// �ݲ�����
					code = code.substring(4);
					break;
				case ModbusCode.MOBUS_DATA_TWO_REAL:
					valueList.add((int)hexToDouble(code.substring(0, 8)));
					code = code.substring(8);
					break;
				default:
					break;
				}
			}//end for
		}
		return valueList;
	}
	
	public static void main(String[] args) {
		int[] value = {10,11};
		int[] len = {1,1};
		System.out.println(wCodeFormat(10, value, len));
		int[] len1 = {ModbusCode.MOBUS_DATA_TWO_INT, ModbusCode.MOBUS_DATA_ONE_INT, ModbusCode.MOBUS_DATA_TWO_REAL};
		ArrayList<Integer> aList = codeParse("00000000000000FFFFDCD8000A40dd1eb8", len1);
		if (aList == null) {
			System.out.println("error code");
		} else {
			for (Integer integer : aList) {
				System.out.println(integer);
			}
		}
	}
}
