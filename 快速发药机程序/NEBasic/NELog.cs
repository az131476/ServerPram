using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace NEBasic
{
    public class NELog
    {
        private static object monitor = new object();
        private static string logFile = "";
        public static string logPath = "";
        
        /// <summary>
        /// 追加一条信息
        /// </summary>
        /// <param name="text"></param>
        private void Write(string text)
        {
            using (StreamWriter sw = new StreamWriter(logFile, true, Encoding.UTF8))
            {
                sw.Write(DateTime.Now.ToString("[yyyy-MM-dd HH:mm:ss] ") + text);
            }
        }

        /// <summary>
        /// 追加一条信息
        /// </summary>
        /// <param name="logFile"></param>
        /// <param name="text"></param>
        private void Write(string logFile, string text)
        {
            using (StreamWriter sw = new StreamWriter(logFile, true, Encoding.UTF8))
            {
                sw.Write(DateTime.Now.ToString("[yyyy-MM-dd HH:mm:ss] ") + text);
            }
        }
        
        /// <summary>
        /// 追加一行信息
        /// </summary>
        /// <param name="logFile"></param>
        /// <param name="text"></param>
        private void WriteLine(string logFile, string text)
        {
            text += "\r\n";
            using (StreamWriter sw = new StreamWriter(logFile, true, Encoding.UTF8))
            {
                sw.Write(DateTime.Now.ToString("[yyyy-MM-dd HH:mm:ss] ") + text);
            }
        }

        /// <summary>
        /// 写日志
        /// </summary>
        /// <param name="path">路径</param>
        /// <param name="text">内容</param>
        private static void WriteLog(string path, string text)
        {
            lock (monitor)
            {
                logFile = path + "\\" + DateTime.Now.ToString("LOG_yyyy-MM-dd") + ".log";
                text += "\r\n";
                using (StreamWriter sw = new StreamWriter(logFile, true, Encoding.UTF8))
                {
                    sw.Write(DateTime.Now.ToString("[yyyy-MM-dd HH:mm:ss] ") + text);
                }
            }
        }

        /// <summary>
        /// 写日志
        /// </summary>
        /// <param name="path">路径</param>
        /// <param name="text">内容</param>
        public static void WriteLog(string text)
        {
            lock (monitor)
            {
                try
                {
                    logFile = logPath + "\\" + DateTime.Now.ToString("LOG_yyyy-MM-dd") + ".log";
                    text += "\r\n";
                    using (StreamWriter sw = new StreamWriter(logFile, true, Encoding.UTF8))
                    {
                        sw.Write(DateTime.Now.ToString("[yyyy-MM-dd HH:mm:ss] ") + text);
                    }
                }
                catch (System.Exception ex)
                {
                	//
                }
            }
        }
    }
}
