using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using NEBasic;
using MySql.Data.MySqlClient;
using System.Windows.Forms;
using System.Configuration;

namespace MachineFill
{
    class ChannelEdit
    {
        public DataTable getChannelList(string h, string l)
        {
            DataTable table = new DataTable();

            table.Columns.Add(" 药槽编码");
            table.Columns.Add(" 设备编号");
            table.Columns.Add(" 行号");
            table.Columns.Add(" 列号");
            table.Columns.Add(" 药槽长度");
            table.Columns.Add(" 药槽宽度");
            table.Columns.Add(" 药槽高度");
            table.Columns.Add(" 上药横向坐标");
            table.Columns.Add(" 上药纵向坐标");
            table.Columns.Add(" 状态");
            table.Columns.Add(" 备注");
            table.Columns.Add(" 水平偏移");
            table.Columns.Add(" 垂直偏移");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "";

                if ("".Equals(h))
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Length,Stock_Width,Stock_Height,Stock_Hor,Stock_Ver,State,Remark,Stock_Hor_correct,Stock_Ver_correct from Stock_list 
                        where Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and Col_NO=" + l;
                } 
                else if ("".Equals(l))
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Length,Stock_Width,Stock_Height,Stock_Hor,Stock_Ver,State,Remark,Stock_Hor_correct,Stock_Ver_correct from Stock_list 
                        where Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and Row_NO=" + h;
                }
                else if (!h.Equals("") && !l.Equals("") && !h.Equals("0") && !l.Equals("0"))
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Length,Stock_Width,Stock_Height,Stock_Hor,Stock_Ver,State,Remark,Stock_Hor_correct,Stock_Ver_correct from Stock_list 
                        where Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and Row_NO=" + h + " and Col_NO=" + l;
                    
                }
                else if (h.Equals("0") && l.Equals("0"))
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Length,Stock_Width,Stock_Height,Stock_Hor,Stock_Ver,State,Remark,Stock_Hor_correct,Stock_Ver_correct from Stock_list 
                        where Eqp_ID='" + NEIni.ReadValue("./config.ini", "params", "equipmentid", "F1") + "' and state='2'";
                       
                }
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row[" 药槽编码"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row[" 设备编号"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row[" 行号"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row[" 列号"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    row[" 药槽长度"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row[" 药槽宽度"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row[" 药槽高度"] = dr.IsDBNull(6) ? "" : dr.GetString(6);
                    row[" 上药横向坐标"] = dr.IsDBNull(7) ? "" : dr.GetString(7);
                    row[" 上药纵向坐标"] = dr.IsDBNull(8) ? "" : dr.GetString(8);
                    if (dr.GetString(9).Equals("2"))
                    {
                        row[" 状态"] = "已停用";
                    }
                    else
                    {
                        row[" 状态"] = dr.IsDBNull(9) ? "" : dr.GetString(9);
                    }
                    row[" 备注"] = dr.IsDBNull(10) ? "" : dr.GetString(10);
                    row[" 水平偏移"] = dr.IsDBNull(11) ? "" : dr.GetString(11);
                    row[" 垂直偏移"] = dr.IsDBNull(12) ? "" : dr.GetString(12);
                    
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

        public bool updateStockList(VOStockList stockList)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                //if (stockList.State.Equals("0"))
                //{
                string sql = "update stock_list set stock_length=" + stockList.StockLength + ",stock_width=" + stockList.StockWidth + ",stock_height=" + stockList.StockHeight + ",stock_hor=" + stockList.StockHor + ",stock_ver=" + stockList.StockVer + ",state=" + stockList.State + ",Stock_Hor_correct='"+stockList.Stock_Hor_correct+"',Stock_Ver_correct='"+stockList.Stock_Ver_correct+"' where stock_id=" + stockList.StockID;
                    new MySqlCommand(sql, conn).ExecuteNonQuery();
                //}
                //else if (stockList.State.Equals("1"))
                //{
                //    string sql = "update stock_list set eqp_id = 'F1',stock_length=" + stockList.StockLength + ",stock_width=" + stockList.StockWidth + ",stock_height=" + stockList.StockHeight + ",stock_hor=" + stockList.StockHor + ",stock_ver=" + stockList.StockVer + ",state=" + stockList.State + " where stock_id=" + stockList.StockID;
                //    new MySqlCommand(sql, conn).ExecuteNonQuery();
                //}
            }
            catch (System.Exception ex)
            {
                MessageBox.Show(ex.ToString());
                return false;
            }
            finally
            {
                pool.releaseConnection(conn);
            }
            return true;
        }
    }
}
