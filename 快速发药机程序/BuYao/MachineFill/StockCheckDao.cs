using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;
using System.Windows.Forms;
using NEBasic;
using System.Configuration;

namespace MachineFill
{
    class StockCheckDao
    {
        public DataTable getCheckList(string code, int flag)
        {
            DataTable table = new DataTable();
            
            table.Columns.Add("  设备");
            table.Columns.Add("药槽编码");
            table.Columns.Add("  行号");
            table.Columns.Add("  列号");
            table.Columns.Add("  库位状态");
            table.Columns.Add("  备注");
            table.Columns.Add("  药品名称");
            table.Columns.Add("  规格");
            table.Columns.Add("  库存数量");
            table.Columns.Add("药品长度");
            table.Columns.Add("库位长度");
            table.Columns.Add("库位宽度");
            table.Columns.Add("HOR");
            table.Columns.Add("VER");

            //table.Columns.Add("药槽编码");
            //table.Columns.Add("  设备");
            //table.Columns.Add("  行号");
            //table.Columns.Add("  列号");
            //table.Columns.Add("  药品名称");
            //table.Columns.Add("  规格");
            //table.Columns.Add("  库存数量");
            //table.Columns.Add("  库位状态");
            //table.Columns.Add("药品长度");
            //table.Columns.Add("库位长度");
            //table.Columns.Add("库位宽度");
            //table.Columns.Add("HOR");
            //table.Columns.Add("VER");

            

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "";
                if (flag == 1)
                {
                    sql = @"select a.stock_id,a.Eqp_ID,a.Row_NO,a.Col_NO,drug_name,drug_spec,ifnull(sum(quantity),0) as qty,state,package_length,stock_length,stock_width,stock_hor,stock_ver,remark from stock_list a left join drug_list b on a.Drug_Code=b.Drug_Code
                        left join stock_detail c on a.Stock_ID=c.Stock_ID where a.eqp_id='" + Paramters.G_EQP_ID + "' GROUP BY a.stock_id order by a.Row_NO,a.Col_NO";
                } 
                else if (flag == 2)
                {
                    sql = @"select a.stock_id,a.Eqp_ID,a.Row_NO,a.Col_NO,drug_name,drug_spec,ifnull(sum(quantity),0) as qty,state,package_length,stock_length,stock_width,stock_hor,stock_ver,remark from stock_list a left join drug_list b on a.Drug_Code=b.Drug_Code
                        left join stock_detail c on a.Stock_ID=c.Stock_ID where a.eqp_id='" + Paramters.G_EQP_ID + "' and (drug_name like '%" + code + "%' or short_code like '%" + code + "%' or drug_barcode like '%" + code + "%') and state=0 GROUP BY a.stock_id order by a.Row_NO,a.Col_NO";
                } 
                else if (flag == 3)
                {
                    sql = @"select a.stock_id,a.Eqp_ID,a.Row_NO,a.Col_NO,drug_name,drug_spec,ifnull(sum(quantity),0) as qty,state,package_length,stock_length,stock_width,stock_hor,stock_ver,remark from stock_list a left join drug_list b on a.Drug_Code=b.Drug_Code
                        left join stock_detail c on a.Stock_ID=c.Stock_ID where a.eqp_id='" + Paramters.G_EQP_ID + "' and (drug_name like '%" + code + "%' or short_code like '%" + code + "%' or drug_barcode like '%" + code + "%') and a.Drug_Code>'' GROUP BY a.stock_id order by a.Row_NO,a.Col_NO";
                    
                } 
                else
                {
                }
                Console.WriteLine(sql);
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();
                    row["药槽编码"] = dr.GetString(0);
                    row["  设备"] = dr.GetString(1);
                    row["  行号"] = dr.GetString(2);
                    row["  列号"] = dr.GetString(3);
                    row["  药品名称"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row["  规格"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row["  库存数量"] = dr.IsDBNull(6) ? "" : dr.GetString(6);
                    if (dr.GetString(7).Equals("1"))
                    {
                        row["  库位状态"] = "正常";
                    }
                    else 
                    {
                        row["  库位状态"] = "锁定";
                    }
                    //row["  库位状态"] = dr.IsDBNull(7) ? "" : dr.GetString(7);
                    row["药品长度"] = dr.IsDBNull(8) ? "100" : dr.GetString(8);
                    
                    
                    row["库位长度"] = dr.IsDBNull(9) ? "" : dr.GetString(9);
                    row["库位宽度"] = dr.IsDBNull(10) ? "" : dr.GetString(10);
                    row["HOR"] = dr.IsDBNull(11) ? "" : dr.GetString(11);
                    row["VER"] = dr.IsDBNull(12) ? "" : dr.GetString(12);
                    row["  备注"] = dr.IsDBNull(13) ? "" : dr.GetString(13);

                    
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

        public DataTable getCheckedErrList(string drugCode, int flag)
        {
            DataTable table = new DataTable();
            table.Columns.Add("药槽编码");
            table.Columns.Add("设备");
            table.Columns.Add("行号");
            table.Columns.Add("列号");
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("库存数量");
            table.Columns.Add("盘点数量");

            //MySqlPool pool = MySqlPool.getInstance();
            //MySqlConnection conn = null;
            //try
            //{
            //    conn = pool.getConnection();
            //    for (int i = 1; i < 20;i++ )
            //    {
            //        DataRow row = table.NewRow();
            //        row["药槽编码"] = "F1";
            //        row["设备"] = "F1";
            //        row["行号"] = "1";
            //        row["列号"] = i;
            //        row["药品名称"] = "";
            //        row["规格"] = "";
            //        row["库存数量"] = "100";
            //        row["盘点数量"] = "200";
            //        table.Rows.Add(row);
            //    }
            //}
            //catch (System.Exception ex)
            //{
            //    //
            //}
            //finally
            //{
            //    pool.releaseConnection(conn);
            //}
            return table;
        }

        /// <summary>
        /// 盘点正确
        /// </summary>
        /// <returns></returns>
        public bool stockChecked(string sid, int qty, int cqty)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            MySqlConnection conn2 = null;
            try
            {
                //conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
                pool.getConnection();
                //conn.Open();
                if (qty == cqty)
                {
                    //
                }
                else if (cqty == 0)
                {
                    string sql = "delete from stock_detail where Stock_ID='" + sid + "'";
                    new MySqlCommand(sql, conn).ExecuteNonQuery();
                }
                else if (qty < cqty)
                {
                    if (qty == 0)
                    {
                        // 老库存为0，插入一条新库存，批次信息暂无法获取
                        string sql = "insert into stock_detail(stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity) select uuid(),stock_id,eqp_id,row_no,col_no,1,drug_code,"+cqty+" from stock_list where Stock_ID='" + sid + "'";
                        new MySqlCommand(sql, conn).ExecuteNonQuery();
                    } 
                    else
                    {
                        // 更新最后一条库存记录
                        string stockGuid = "";
                        string sql = "select stock_guid from stock_detail where Stock_ID='" + sid + "' order by Pos_NO desc";
                        MySqlCommand cmd = new MySqlCommand(sql, conn);
                        MySqlDataReader dr = cmd.ExecuteReader();
                        if (dr.Read())
                        {
                            stockGuid = dr.GetString(0);
                        }
                        dr.Close();

                        sql = "update stock_detail set quantity=quantity+" + (cqty - qty) + " where stock_guid='" + stockGuid + "'";
                        new MySqlCommand(sql, conn).ExecuteNonQuery();
                    }
                }
                else
                {
                    conn2 = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                    conn2.Open();
                    // 实际比当前少，循环减库存
                    int offQty = qty - cqty;
                    string sql = "select stock_guid,quantity from stock_detail where Stock_ID='" + sid + "' order by Pos_NO";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    while (dr.Read())
                    {
                        string stockGuid = dr.GetString(0);
                        int sqty = dr.GetInt32(1);
                        if (sqty <= offQty)
                        {
                            string sql1 = "delete from stock_detail where stock_guid='" + stockGuid + "'";
                            new MySqlCommand(sql1, conn2).ExecuteNonQuery();
                        } 
                        else
                        {
                            string sql1 = "update stock_detail set quantity=quantity-" + offQty + " where stock_guid='" + stockGuid + "'";
                            new MySqlCommand(sql1, conn2).ExecuteNonQuery();
                        }
                        offQty = offQty - sqty;
                        if (offQty < 1)
                        {
                            break;
                        }
                    }
                    dr.Close();
                }
            }
            catch (System.Exception ex)
            {
                MessageBox.Show("更新异常2："+ex.ToString());
                return false;
            }
            finally
            {
                pool.releaseConnection(conn);
                pool.releaseConnection(conn2);
                //conn.Close();
                //conn2.Close();
            }
            return true;
        }

        public bool stockChecked(int row, int col, int cqty)
        {
            //MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            MySqlConnection conn2 = null;
            try
            {
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                conn.Open();
                int qty = 0;
                {
                    string sql = "select sum(quantity) from stock_detail where EQP_id='F1' and row_no=" + row + " and col_no="+col;
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    if (dr.Read())
                    {
                        qty = dr.IsDBNull(0) ? 0 : dr.GetInt32(0);
                    }
                    dr.Close();
                }

                if (qty == cqty)
                {
                    //
                }
                else if (cqty == 0)
                {
                    string sql = "delete from stock_detail where EQP_id='F1' and row_no=" + row + " and col_no=" + col;
                    new MySqlCommand(sql, conn).ExecuteNonQuery();
                }
                else if (qty < cqty)
                {
                    if (qty == 0)
                    {
                        // 老库存为0，插入一条新库存，批次信息暂无法获取
                        string sql = "insert into stock_detail(stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity) select uuid(),stock_id,eqp_id,row_no,col_no,1,drug_code," + cqty + " from stock_list where EQP_id='F1' and row_no=" + row + " and col_no=" + col;
                        new MySqlCommand(sql, conn).ExecuteNonQuery();
                    }
                    else
                    {
                        // 更新最后一条库存记录
                        string stockGuid = "";
                        string sql = "select stock_guid from stock_detail where EQP_id='F1' and row_no=" + row + " and col_no=" + col + " order by Pos_NO desc";
                        MySqlCommand cmd = new MySqlCommand(sql, conn);
                        MySqlDataReader dr = cmd.ExecuteReader();
                        if (dr.Read())
                        {
                            stockGuid = dr.GetString(0);
                        }
                        dr.Close();

                        sql = "update stock_detail set quantity=quantity+" + (cqty - qty) + " where stock_guid='" + stockGuid + "'";
                        new MySqlCommand(sql, conn).ExecuteNonQuery();
                    }
                }
                else
                {
                    conn2 = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                    conn2.Open();
                    // 实际比当前少，循环减库存
                    int offQty = qty - cqty;
                    string sql = "select stock_guid,quantity from stock_detail where EQP_id='F1' and row_no=" + row + " and col_no=" + col + " order by Pos_NO";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    while (dr.Read())
                    {
                        string stockGuid = dr.GetString(0);
                        int sqty = dr.GetInt32(1);
                        if (sqty <= offQty)
                        {
                            string sql1 = "delete from stock_detail where stock_guid='" + stockGuid + "'";
                            new MySqlCommand(sql1, conn2).ExecuteNonQuery();
                        }
                        else
                        {
                            string sql1 = "update stock_detail set quantity=quantity-" + offQty + " where stock_guid='" + stockGuid + "'";
                            new MySqlCommand(sql1, conn2).ExecuteNonQuery();
                        }
                        offQty = offQty - sqty;
                        if (offQty < 1)
                        {
                            break;
                        }
                    }
                    dr.Close();
                }
            }
            catch (System.Exception ex)
            {
                MessageBox.Show(ex.ToString());
                return false;
            }
            finally
            {
                //pool.releaseConnection(conn);
                //pool.releaseConnection(conn2);
                conn.Close();
                conn2.Close();
            }
            return true;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="row"></param>
        /// <param name="col"></param>
        /// <param name="len"></param>
        /// <returns></returns>
        public VOStockList stockChecked(int row, int col, int checkqty, int len)
        {
            VOStockList stockList = null;
            //MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                conn.Open();

                string sql = "update stock_list set Check_Length=" + len + ",Check_Quantity=" + checkqty + ",Check_Time=now() where Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and row_no=" + row + " and col_no=" + col;
                new MySqlCommand(sql, conn).ExecuteNonQuery();

                sql = "select ifnull(Check_Quantity,0),ifnull(sum(quantity),0) from stock_list a left join stock_detail b on a.Stock_ID=b.Stock_ID where a.Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and a.row_no=" + row + " and a.col_no=" + col;
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    if (dr.GetInt32(0) == dr.GetInt32(1))
                    {
                        break;
                    }
                    stockList = new VOStockList();
                    stockList.Quantity = dr.GetString(1);
                    stockList.CheckQuantity = dr.GetString(0);
                }
                dr.Close();
            }
            catch (System.Exception ex)
            {
                //
            }
            finally
            {
                //pool.releaseConnection(conn);
                conn.Close();
            }
            return stockList;
        }
    }
}
