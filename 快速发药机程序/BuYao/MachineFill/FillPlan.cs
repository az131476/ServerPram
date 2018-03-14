using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    class FillPlan
    {
        private static int G_PAGE_ITEM = 20;
        public static int G_PAGE = 1;
        public static int ORDER = 1;

        /// <summary>
        /// 获取补药计划内容
        /// </summary>
        /// <param name="page">页号</param>
        /// <param name="order">排序规则</param>
        /// <returns></returns>
        
        public DataSet getFillPlan(int page)
        {
            page = page < 1 ? 1 : page;
            FillPlan.G_PAGE = page;

            DataSet fDataSet = new DataSet();
            DataTable table = new DataTable();
            table.TableName = "MyData";

            table.Columns.Add("YPMC");
            table.Columns.Add("YPGG");
            table.Columns.Add("SCCJ");
            table.Columns.Add("SL");
            table.Columns.Add("KW");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                string sql = "";
                if (ORDER == 1)
                {
                    sql = "select Drug_Name,Drug_Spec,Manufactory,StockFillQty,Package_Unit,storageloc from v_f_stock2 order by storageloc limit " + (page - 1) * G_PAGE_ITEM + "," + G_PAGE_ITEM;
                } 
                else
                {
                    sql = "select Drug_Name,Drug_Spec,Manufactory,StockFillQty,Package_Unit,storageloc from v_f_stock2 limit " + (page - 1) * G_PAGE_ITEM + "," + G_PAGE_ITEM;
                }
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["YPMC"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["YPGG"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["SCCJ"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row["SL"] = (dr.IsDBNull(3) ? "" : dr.GetString(3)) + (dr.IsDBNull(4) ? "" : dr.GetString(4));
                    row["KW"] = dr.IsDBNull(5) ? "" : dr.GetString(5);

                    table.Rows.Add(row);
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

            fDataSet.Tables.Add(table);
            return fDataSet;
        }
    }
}
