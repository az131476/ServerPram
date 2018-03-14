using System;
using System.Collections.Generic;
using System.Text;

namespace NEBasic
{
    public class NEIni
    {
        // ����INI�ļ���д�������� WritePrivateProfileString()
        [System.Runtime.InteropServices.DllImport("kernel32")]
        private static extern long WritePrivateProfileString(string section, string key, string val, string filePath);

        // ����INI�ļ��Ķ��������� GetPrivateProfileString()
        [System.Runtime.InteropServices.DllImport("kernel32")]
        private static extern int GetPrivateProfileString(string section, string key, string def, System.Text.StringBuilder retVal, int size, string filePath);

        //public IniHelper(string path)
        //{
        //   this.sPath = path;
        //}

        public static void Write(string sPath, string section, string key, string value)
        {
            // section=���ýڣ�key=������value=��ֵ��path=·��
            WritePrivateProfileString(section, key, value, sPath);
        }

        public static string ReadValue(string sPath, string section, string key, string def)
        {
            // ÿ�δ�ini�ж�ȡ�����ֽ�
            System.Text.StringBuilder temp = new System.Text.StringBuilder(255);
            // section=���ýڣ�key=������temp=���棬path=·��
            GetPrivateProfileString(section, key, def, temp, 255, sPath);
            return temp.ToString();
        }
    }
}
