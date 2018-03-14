using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;
using System.Windows.Forms;

namespace MachineFill
{
    class DrugFill
    {
        private static int G_PAGE_ITEM = 20;
        public static int G_PAGE = 1;
        //public static int ORDER = 1;

        public DataTable getFillData(string sql)
        {
            DataTable table = new DataTable();
            table.Columns.Add("药品编码");
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("厂家");
            table.Columns.Add("可补量");
            table.Columns.Add("库位码");
            table.Columns.Add("当前量");
            table.Columns.Add("最大库存");
            table.Columns.Add("库位限量");
            table.Columns.Add("药长");
            table.Columns.Add("药宽");
            //table.Columns.Add("槽数");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                //string sql = "";
                //if (ORDER == 1)
                //{
                //    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";
                
                //    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') order by stockfillqty desc";
                //}
                //else
                //{
                //    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "')";
                //}
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["药品编码"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["药品名称"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["规格"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row["厂家"] = dr.IsDBNull(3) ? "" : dr.GetString(3);

                    //if (dr.GetInt32(8) == 0)
                    //{
                        row["可补量"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    //}
                    //else
                    //{
                    //    row["可补量"] = (int.Parse(dr.GetString(8)) * int.Parse(dr.GetString(11)) - int.Parse(dr.GetString(6))).ToString();
                    //}
                    row["库位码"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row["当前量"] = dr.IsDBNull(6) ? "" : dr.GetString(6);

                    //if (dr.GetInt32(8) == 0)
                    //{
                        row["最大库存"] = dr.IsDBNull(7) ? "" : dr.GetString(7);
                    //}
                    //else
                    //{
                    //    row["最大库存"] = (int.Parse(dr.GetString(8)) * int.Parse(dr.GetString(11))).ToString();
                    //}
                    
                    if (dr.IsDBNull(8) || "0".Equals(dr.GetString(8)))
                    {
                        row["库位限量"] = "" + 1100 / int.Parse(dr.GetString(9));
                    }
                    else
                    {
                        row["库位限量"] = dr.IsDBNull(8) ? "" : dr.GetString(8);
                    }
                    row["药长"] = dr.IsDBNull(9) ? "" : dr.GetString(9);
                    row["药宽"] = dr.IsDBNull(10) ? "" : dr.GetString(10);
                    //row["槽数"] = dr.IsDBNull(11) ? "" : dr.GetString(11);

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

        public DataTable getWaitingList(VODrugInfo drugInfo)
        {
            DataTable table = new DataTable();
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("厂家");
            table.Columns.Add("可补量");
            table.Columns.Add("库位码");
            table.Columns.Add("当前量");
            table.Columns.Add("最大库存");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "select Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,drug_code,Stock_Limit,StockCount from v_f_stock2 where StockFillQty>0 order by StockFillQty desc";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    try
                    {
                        if (drugInfo.DrugCode.Equals(dr.GetString(7)))
                        {
                            continue;
                        }
                    }
                    catch (System.Exception ex)
                    {
                    	//
                    }
                    DataRow row = table.NewRow();

                    row["药品名称"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["规格"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["厂家"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    //添加单库位限制量
                    //if (!dr.GetString(8).Equals("0"))
                    //{
                    //    row[" 可补量"] = dr.GetString(9) + "";
                    //}
                    //else
                    //{
                        row[" 可补量"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    //}
                    row["库位码"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row["当前量"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row["最大库存"] = dr.IsDBNull(6) ? "" : dr.GetString(6);

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

        public DataTable getStockList(string drugCode)
        {
            DataTable table = new DataTable();
            table.Columns.Add("库位编码");
            table.Columns.Add("  行号");
            table.Columns.Add("  列号");
            table.Columns.Add(" 库存量");
            table.Columns.Add(" 可补量");           
            table.Columns.Add("hor");
            table.Columns.Add("ver");
            table.Columns.Add("len");
            table.Columns.Add("wid");
            table.Columns.Add("xl");
            table.Columns.Add("有效期");
            table.Columns.Add("批号");

            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "select a.Stock_ID,Row_NO,Col_NO,ifnull(quantity,0),FLOOR(Stock_Length/Package_Length) as cb,Stock_Hor,Stock_Ver,Stock_length,Stock_width,Stock_Limit,TO_DAYS(exp_date)-TO_DAYS(NOW()) as days,bnotp  from stock_list a left join (select stock_id,sum(quantity) as quantity,Exp_Date,bnotp  from stock_detail where Drug_Code='" + drugCode + "' GROUP BY Stock_ID) b on a.Stock_ID=b.stock_id left join drug_list c on a.Drug_Code=c.Drug_Code where a.state=1 and a.Drug_Code='" + drugCode + "'";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    if (dr.GetInt32(4) - dr.GetInt32(3) < 1)
                    {
                        continue;
                    }

                    DataRow row = table.NewRow();

                    row["库位编码"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["  行号"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["  列号"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row[" 库存量"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    //添加单库位限制量
                    //if (!dr.GetString(9).Equals("0"))
                    //{
                    //    row[" 可补量"] = (dr.GetInt32(9) - dr.GetInt32(3)) + "";
                    //}
                    //else
                    //{
                        row[" 可补量"] = (dr.GetInt32(4) - dr.GetInt32(3)) + "";
                    //}
                    row["hor"] = dr.IsDBNull(5) ? "0" : dr.GetString(5);
                    row["ver"] = dr.IsDBNull(6) ? "0" : dr.GetString(6);
                    row["len"] = dr.IsDBNull(7) ? "0" : dr.GetString(7);
                    row["wid"] = dr.IsDBNull(8) ? "0" : dr.GetString(8);
                    row["xl"] = dr.IsDBNull(4) ? "0" : dr.GetString(4);
                    row["有效期"] = dr.IsDBNull(10) ? "0" : dr.GetString(10);
                    row["批号"] = dr.IsDBNull(11) ? "0" : dr.GetString(11);

                    table.Rows.Add(row);
                }
                dr.Close();
            }
            catch (System.Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
            finally
            {
                pool.releaseConnection(conn);
            }

            return table;
        }

        public bool updateStock(int stockID, int quantity, string bnotp, string expdate)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sGUID = "";
                string sql = "select stock_guid from stock_detail where stock_id="+stockID+" and bnotp='"+bnotp+"' order by pos_no desc limit 0,1";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    sGUID = dr.IsDBNull(0) ? "" : dr.GetString(0);
                }
                dr.Close();

                if ("".Equals(sGUID))
                {
                    int posno = 1;
                    string sql2 = "select ifnull(max(pos_no),0)+1 from stock_detail where eqp_id='F1' and stock_id=" + stockID + "";
                    MySqlCommand cmd2 = new MySqlCommand(sql2, conn);
                    MySqlDataReader dr2 = cmd.ExecuteReader();
                    while (dr2.Read())
                    {
                        posno = dr2.GetInt32(0);
                    }
                    dr2.Close();
                    
                    sql = "insert into stock_detail(stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity) select uuid(),stock_id,eqp_id,row_no,col_no,"+posno+",drug_code," + quantity + " from stock_list where stock_id=" + stockID;
                } 
                else
                {
                    sql = "update stock_detail set quantity=quantity+" + quantity + " where stock_guid='" + sGUID + "'";
                }
              //  Console.WriteLine(sql);
                new MySqlCommand(sql, conn).ExecuteNonQuery();
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

        public bool updateStock(int row, int col, int quantity, string bnotp, string expdate)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sGUID = "";
                string sql = "select stock_guid from stock_detail where eqp_id='F1' and row_no="+row+" and col_no="+col+" and bnotp='"+bnotp+"' order by pos_no desc limit 0,1";
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    sGUID = dr.IsDBNull(0) ? "" : dr.GetString(0);
                }
                dr.Close();

                if ("".Equals(sGUID))
                {
                    int posno = 1;
                    string sql2 = "select ifnull(max(pos_no),0)+1 from stock_detail where eqp_id='F1' and row_no=" + row + " and col_no=" + col + "";
                    MySqlCommand cmd2 = new MySqlCommand(sql2, conn);
                    MySqlDataReader dr2 = cmd.ExecuteReader();
                    while (dr2.Read())
                    {
                        posno = dr2.GetInt32(0);
                    }
                    dr2.Close();
                    sql = "insert into stock_detail(stock_guid,stock_id,eqp_id,row_no,col_no,pos_no,drug_code,quantity) select uuid(),stock_id,eqp_id,row_no,col_no,"+posno+",drug_code," + quantity + " from stock_list where eqp_id='F1' and row_no="+row+" and col_no="+col;
                }
                else
                {
                    sql = "update stock_detail set quantity=quantity+" + quantity + " where stock_guid='" + sGUID + "'";
                }
                new MySqlCommand(sql, conn).ExecuteNonQuery();
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
