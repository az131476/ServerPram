package us;

import java.util.Random;

public class CRCKey {
	public static void main(String[] args) {
		System.out.println("���ɵ�4Ϊ�����Ϊ��" + getKey());
	}

	/**
	 * java����������ֺ���ĸ���
	 * 
	 * @param length
	 *            [����������ĳ���]
	 * @return
	 */
	public static String getKey() {
		int length = 4;
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// �����ĸ��������
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// �ַ���
			if ("char".equalsIgnoreCase(charOrNum)) {
				// ȡ�ô�д��ĸ����Сд��ĸ
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) { // ����
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}
}
