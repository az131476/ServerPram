using System;
using System.Collections.Generic;
using System.Text;
using System.Security.Cryptography;

namespace NEBasic
{
    public class NEMD5
    {
        public static string GetMD5(string ps)
        {
            MD5 md5 = MD5.Create();
            string pwd = "";
            byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(ps));

            // ͨ��ʹ��ѭ�������ֽ����͵�����ת��Ϊ�ַ��������ַ����ǳ����ַ���ʽ������
            for (int i = 0; i < s.Length; i++)
            {
                // ���õ����ַ���ʹ��ʮ���������͸�ʽ����ʽ����ַ���Сд����ĸ�����ʹ�ô�д��X�����ʽ����ַ��Ǵ�д�ַ�
                pwd = pwd + s[i].ToString("x2");
            }
            return pwd.ToUpper();
        }
    }
}
