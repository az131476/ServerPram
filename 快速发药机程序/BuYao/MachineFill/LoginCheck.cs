using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    class LoginCheck
    {
        /// <summary>
        /// 检测登录用户是否合法
        /// </summary>
        /// <param name="userCode">用户工号条码</param>
        /// <returns>true 验证成功；false 验证失败</returns>
        public bool check(string userCode)
        {
            bool flg = false;

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                
                string sql = "select user_id,user_name from user_list where id_code='" + userCode + "'";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    flg = true;
                    Paramters.globalUserID = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    Paramters.globalUserName = dr.IsDBNull(1) ? "" : dr.GetString(1);
                }
                dr.Close();
            }
            catch (System.Exception ex)
            {
            	//
            }
            finally
            {
                pool.releaseConnection(conn);
            }
            return flg;
        }
    }
}
