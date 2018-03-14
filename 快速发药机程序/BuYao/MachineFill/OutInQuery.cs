using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    class OutInQuery
    {
        public DataTable getOutInData()
        {
            DataTable table = new DataTable();
            table.Columns.Add("序号");
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("厂家");
            table.Columns.Add("出库数量");
            table.Columns.Add("入库数量");
            table.Columns.Add("二级库位");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "select Drug_Name,Drug_Spec,Manufactory,ck,rk,storageloc from v_stock_inout9";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                int i = 1;
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["序号"] = i++;
                    row["药品名称"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["规格"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["厂家"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row["出库数量"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    row["入库数量"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row["二级库位"] = dr.IsDBNull(5) ? "" : dr.GetString(5);

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

            return table;
        }
    }
}
