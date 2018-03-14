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
        /// ׷��һ����Ϣ
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
        /// ׷��һ����Ϣ
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
        /// ׷��һ����Ϣ
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
        /// д��־
        /// </summary>
        /// <param name="path">·��</param>
        /// <param name="text">����</param>
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
        /// д��־
        /// </summary>
        /// <param name="path">·��</param>
        /// <param name="text">����</param>
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
