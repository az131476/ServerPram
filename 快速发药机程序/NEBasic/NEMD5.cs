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

            // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
            for (int i = 0; i < s.Length; i++)
            {
                // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符
                pwd = pwd + s[i].ToString("x2");
            }
            return pwd.ToUpper();
        }
    }
}
