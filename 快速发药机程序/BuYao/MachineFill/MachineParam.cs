using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MachineFill
{
    class MachineParam
    {
        // PLC状态
        private static int plcstate;
        public static int Plcstate
        {
            get
            {
                return plcstate;
            }
            set
            {
                plcstate = value;
            }
        }

        private static string plcinfo;
        public static string Plcinfo
        {
            get
            {
                if (plcstate == 1)
                {
                    return "PLC初始化失败，错误代码：" + plcinfo;
                }
                else if (plcstate == 2)
                {
                    return "PLC初始化成功";
                } 
                else
                {
                    return "PLC正在进行初始化";
                }
            }
            set
            {
                plcinfo = value;
            }
        }

        // 数据库连接状态
        private static int dbstate;
        public static int Dbstate
        {
            get
            {
                return dbstate;
            }
            set
            {
                dbstate = value;
            }
        }

        private static string dbinfo;
        public static string Dbinfo
        {
            get
            {
                if (dbstate == 1)
                {
                    return "数据库初始化失败，错误代码：" + dbinfo;
                }
                else if (dbstate == 2)
                {
                    return "数据库初始化成功";
                }
                else
                {
                    return "数据库正在进行初始化";
                }
            }
            set
            {
                dbinfo = value;
            }
        }

        // 出药单元底层
        private static int outstate;
        public static int Outstate
        {
            get
            {
                return outstate;
            }
            set
            {
                outstate = value;
            }
        }

        private static string outinfo;
        public static string Outinfo
        {
            get
            {
                if (outstate == 1)
                {
                    return "出药单元底层硬件初始化失败，错误代码：" + outinfo;
                }
                else if (outstate == 2)
                {
                    return "出药单元底层硬件初始化成功";
                }
                else
                {
                    return "出药单元底层硬件正在进行初始化";
                }
            }
            set
            {
                outinfo = value;
            }
        }

        // 皮带机
        private static int lifterstate;
        public static int Lifterstate
        {
            get
            {
                return lifterstate;
            }
            set
            {
                lifterstate = value;
            }
        }

        private static string lifterinfo;
        public static string Lifterinfo
        {
            get
            {
                if (lifterstate == 1)
                {
                    return "升降机初始化失败，错误代码：" + lifterinfo;
                }
                else if (lifterstate == 2)
                {
                    return "升降机初始化成功";
                }
                else
                {
                    return "升降机正在进行初始化";
                }
            }
            set
            {
                lifterinfo = value;
            }
        }

        // 测距
        private static int sensorstate;
        public static int Sensorstate
        {
            get
            {
                return sensorstate;
            }
            set
            {
                sensorstate = value;
            }
        }

        private static string sensorinfo;
        public static string Sensorinfo
        {
            get
            {
                if (sensorstate == 1)
                {
                    return "测距单元初始化失败，错误代码：" + sensorinfo;
                }
                else if (sensorstate == 2)
                {
                    return "测距单元初始化成功";
                }
                else
                {
                    return "测距单元正在进行初始化";
                }
            }
            set
            {
                sensorinfo = value;
            }
        }

        // X轴
        private static int xstate;
        public static int Xstate
        {
            get
            {
                return xstate;
            }
            set
            {
                xstate = value;
            }
        }

        private static string xinfo;
        public static string Xinfo
        {
            get
            {
                if (xstate == 1)
                {
                    return "X轴初始化失败，错误代码：" + xinfo;
                }
                else if (xstate == 2)
                {
                    return "X轴初始化成功";
                }
                else
                {
                    return "X轴正在进行初始化";
                }
            }
            set
            {
                xinfo = value;
            }
        }

        // Y轴
        private static int ystate;
        public static int Ystate
        {
            get
            {
                return ystate;
            }
            set
            {
                ystate = value;
            }
        }

        private static string yinfo;
        public static string Yinfo
        {
            get
            {
                if (ystate == 1)
                {
                    return "Y轴初始化失败，错误代码：" + yinfo;
                }
                else if (ystate == 2)
                {
                    return "Y轴初始化成功";
                }
                else
                {
                    return "Y轴正在进行初始化";
                }
            }
            set
            {
                yinfo = value;
            }
        }

        // 批量机械手拨杆
        private static int bgstate;
        public static int Bgstate
        {
            get
            {
                return bgstate;
            }
            set
            {
                bgstate = value;
            }
        }

        private static string bginfo;
        public static string Bginfo
        {
            get
            {
                if (bgstate == 1)
                {
                    return "批量机械手拨杆初始化失败，错误代码：" + bginfo;
                }
                else if (bgstate == 2)
                {
                    return "批量机械手拨杆初始化成功";
                }
                else
                {
                    return "批量机械手拨杆正在进行初始化";
                }
            }
            set
            {
                bginfo = value;
            }
        }

        // 批量机械手齐药
        private static int qystate;
        public static int Qystate
        {
            get
            {
                return qystate;
            }
            set
            {
                qystate = value;
            }
        }

        private static string qyinfo;
        public static string Qyinfo
        {
            get
            {
                if (qystate == 1)
                {
                    return "批量机械手齐药初始化失败，错误代码：" + qyinfo;
                }
                else if (qystate == 2)
                {
                    return "批量机械手齐药初始化成功";
                }
                else
                {
                    return "批量机械手齐药正在进行初始化";
                }
            }
            set
            {
                qyinfo = value;
            }
        }

        // 批量机械手出药门
        private static int cymstate;
        public static int Cymstate
        {
            get
            {
                return cymstate;
            }
            set
            {
                cymstate = value;
            }
        }

        private static string cyminfo;
        public static string Cyminfo
        {
            get
            {
                if (cymstate == 1)
                {
                    return "批量机械手出药门初始化失败，错误代码：" + cyminfo;
                }
                else if (cymstate == 2)
                {
                    return "批量机械手出药门初始化成功";
                }
                else
                {
                    return "批量机械手出药门正在进行初始化";
                }
            }
            set
            {
                cyminfo = value;
            }
        }
    }
}
