using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    class DrugBnotp
    {
        public DataTable getDrugBnotp(string drugCode)
        {
            DataTable table = new DataTable();

            table.Columns.Add("批次");
            table.Columns.Add("效期");
            
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                string sql = @"select BNOTP as 批次,DATE_FORMAT(Exp_Date,'%Y-%m-%d') as 有效期,Use_Flag as 状态 from drug_bnotp_list where Drug_Code='" + drugCode + "' order by Exp_Date desc limit 0,50";

                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["批次"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["效期"] = dr.IsDBNull(1) ? "" : dr.GetString(1);

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
