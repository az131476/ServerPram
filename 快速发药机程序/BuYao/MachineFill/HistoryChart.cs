using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    class HistoryChart
    {
        public string[] xValues;
        public double[] yValues1;
        public double[] yValues2;

        public bool getChart()
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                xValues = new string[8];
                yValues1 = new Double[8];
                yValues2 = new Double[8];
                string sql = @"select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 7 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 7 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 6 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 6 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 5 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 5 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 4 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 4 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 3 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 3 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 2 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 2 DAY) 
                        union select DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 1 DAY),'%m月%d日'),out_qty,in_qty from task_log WHERE count_date=DATE_SUB(CURDATE(),INTERVAL 1 DAY) 
                        union select DATE_FORMAT(CURDATE(),'%m月%d日'),(select sum(quantity) from equipment_out where eqp_id='" + Paramters.G_EQP_ID + "' and datediff(now(),out_time)=0),(select sum(quantity) from equipment_in where eqp_id='" + Paramters.G_EQP_ID + "' and datediff(now(),in_time)=0)";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                int i = 0;
                while (dr.Read())
                {
                    xValues[i] = dr.GetString(0);
                    yValues1[i] = dr.IsDBNull(1) ? 0 : dr.GetDouble(1);
                    yValues2[i] = dr.IsDBNull(2) ? 0 : dr.GetDouble(2);
                    i++;
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
            return true;
        }
    }
}
