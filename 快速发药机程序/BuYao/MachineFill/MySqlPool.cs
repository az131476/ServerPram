using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MySql.Data.MySqlClient;
using System.Collections;
using System.Threading;
using System.Windows.Forms;
using System.Data;

namespace MachineFill
{
    class MySqlPool
    {
        private static volatile MySqlPool pool;
        private Hashtable map;
        
        private int initPoolSize = 3;//初始连接数
        private int maxPoolSize = 6;//最大连接数
        private int waitTime = 100;

        private static Object lockObj = new Object();

        private MySqlPool()
        {
            init();
        }

        public static MySqlPool getInstance() 
        {
            if (pool == null) 
            {
                lock (lockObj) 
                {
                    if(pool == null) 
                    {
                        pool = new MySqlPool();
                    }
                }
            }
            return pool;
        }
     
        private void init()
        {
            try 
            {
                map = Hashtable.Synchronized(new Hashtable());
                for (int i = 0; i < initPoolSize; i++) 
                {
                    map.Add(getNewConnection(), true);
                }
            }
            catch (System.Exception ex) 
            {
                //
            }
        }

        private MySqlConnection getNewConnection()
        {
            try
            {
                MySqlConnection conn = new MySqlConnection(Paramters.G_DB_CONN_STR);
                conn.Open();
                return conn;
            }
            catch (System.Exception ex)
            {
                //
            }
            return null;
        }
     
        public MySqlConnection getConnection() 
        {
            lock(lockObj)
            {
                MySqlConnection conn = null;
                try 
                {
                    foreach (DictionaryEntry item in map)
                    {
                        if ((Boolean)item.Value)
                        {
                            conn = (MySqlConnection)item.Key;
                            map[conn] = false;
                            break;
                        }
                    }

                    if (conn == null)
                    {
                        if (map.Count < maxPoolSize)
                        {
                            conn = getNewConnection();
                            map.Add(conn, false);
                        }
                        else
                        {
                            Thread.Sleep(waitTime);
                            conn = getConnection();
                        }
                    }
                } 
                catch (System.Exception ex) 
                {
                    MessageBox.Show(ex.Message);
                    return null;
                }
                return conn;
            }
        }

        public void releaseConnection(MySqlConnection conn)
        {
            lock (lockObj)
            {
                if (conn == null)
                {
                    return;
                }
                try
                {
                    if (map.ContainsKey(conn))
                    {
                        map[conn] = true;

                        if (conn.State == ConnectionState.Closed)
                        {
                            map.Remove(conn);
                        }
                    }
                    else
                    {
                        conn.Close();
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
