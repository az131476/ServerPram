using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MachineFill
{
    public class Paramters
    {
        public static string globalUserID = "";
        public static string globalUserName = "";

        public static string G_DB_IP_STR = "127.0.0.1";
        public static string G_DB_DATABASE_STR = "unisky";
        public static string G_DB_USER_STR = "root";
        public static string G_DB_PWD_STR = "qq1223";
        public static string G_DB_CONN_STR;

        public static int G_CHN_LEN = 1000;//药槽长度
        public static string G_EQP_ID = "F1";

        public static string FillPlanCode
        {
            get
            {
                return DateTime.Now.ToString("yyyyMMddHHmmss");
            }
        }
    }
}
