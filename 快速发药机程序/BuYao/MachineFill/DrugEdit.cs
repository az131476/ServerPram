using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;
using NEBasic;
using System.Configuration;

namespace MachineFill
{
    class DrugEdit
    {
        /// <summary>
        /// 获取药品信息
        /// </summary>
        /// <param name="code">药品条码、简码、名称</param>
        /// <returns></returns>
        public DataTable getDrugInfo(string code)
        {
            DataTable table = new DataTable();

            table.Columns.Add("药品编码");
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("厂家");
            table.Columns.Add("条码");
            table.Columns.Add("拼音码");
            table.Columns.Add("长度");
            table.Columns.Add("宽度");
            table.Columns.Add("高度");
            table.Columns.Add("日均发药量");//9
            table.Columns.Add("药槽数");
            table.Columns.Add("理论最大库存");
            table.Columns.Add("动作时间");//12
            table.Columns.Add("等待时间");
            table.Columns.Add("保留数量");
            table.Columns.Add("出药限量");
            table.Columns.Add("单库位限量");
            table.Columns.Add("库位码");

            //MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                conn.Open();
                string sql = @"select a.drug_code,short_code,drug_barcode,drug_name,drug_spec,manufactory,package_length,package_width,package_height,out_limit,
                    stock_holdquantity,daily_average,stock_limit,ct,ct*floor(" + Paramters.G_CHN_LEN + "/Package_Length),time1,time2,storageLoc from drug_list a " 
                    + " left join (select drug_code,count(1) as ct from stock_list where eqp_id='" + Paramters.G_EQP_ID + "' group by drug_code) b on a.Drug_Code=b.Drug_Code where drug_name like '%" + code + "%' or short_code like '%" + code + "%' or drug_barcode like '%" + code + "%' limit 0,100";
                
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["药品编码"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["拼音码"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["条码"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row["药品名称"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    row["规格"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row["厂家"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row["长度"] = dr.IsDBNull(6) ? "" : dr.GetString(6);
                    row["宽度"] = dr.IsDBNull(7) ? "" : dr.GetString(7);
                    row["高度"] = dr.IsDBNull(8) ? "" : dr.GetString(8);
                    row["日均发药量"] = dr.IsDBNull(11) ? "" : dr.GetString(11);
                    row["药槽数"] = dr.IsDBNull(13) ? "" : dr.GetString(13);
                    row["理论最大库存"] = dr.IsDBNull(14) ? "" : dr.GetString(14);
                    row["动作时间"] = dr.IsDBNull(15) ? "" : dr.GetString(15);
                    row["等待时间"] = dr.IsDBNull(16) ? "" : dr.GetString(16);
                    row["出药限量"] = dr.IsDBNull(9) ? "" : dr.GetString(9);
                    row["保留数量"] = dr.IsDBNull(10) ? "" : dr.GetString(10);
                    row["单库位限量"] = dr.IsDBNull(12) ? "" : dr.GetString(12);
                    row["库位码"] = dr.IsDBNull(17) ? "" : dr.GetString(17);
                    
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
                //pool.releaseConnection(conn);
                conn.Close();
            }

            return table;
        }

        /// <summary>
        /// 通过药品包装尺寸检索合适的药槽
        /// </summary>
        /// <param name="code">药品编码</param>
        /// <returns></returns>
        public DataTable getStockByPackage(bool used, VODrugInfo drugInfoVO)
        {
            DataTable table = new DataTable();

            table.Columns.Add("库位编码");
            table.Columns.Add("设备编号");
            table.Columns.Add("行号");
            table.Columns.Add("列号");
            table.Columns.Add("库位宽度");
            table.Columns.Add("药品宽度");
            table.Columns.Add("宽度差");
            table.Columns.Add("库位高度");
            table.Columns.Add("药品高度");
            table.Columns.Add("高度差");
            table.Columns.Add("药品名称");
            table.Columns.Add("规格");
            table.Columns.Add("厂家");

            //MySqlPool pool = MySqlPool.getInstance();
            //MySqlConnection conn = null;
            MySqlConnection conn = null;
            try
            {
                //conn = pool.getConnection();
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
                conn.Open();
                string sql = "";
                if (used)
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Width,Stock_Height,Drug_Name,Drug_Spec,Manufactory from Stock_list a left join drug_list b on a.Drug_Code=b.Drug_Code 
                        where ((Stock_width>=(" + drugInfoVO.PackageWidth + "+" + NEIni.ReadValue("./config.ini", "params", "packagewidthlow", "2") + ") and "
                        + " Stock_width<=(" + drugInfoVO.PackageWidth + "+" + NEIni.ReadValue("./config.ini", "params", "packagewidthupp", "10") + ") and "
                        + " Stock_height>=(" + drugInfoVO.PackageHeight + "+" + NEIni.ReadValue("./config.ini", "params", "packageheightlow", "30") + ") and "
                        + " Stock_height<=(" + drugInfoVO.PackageHeight + "+" + NEIni.ReadValue("./config.ini", "params", "packageheightupp", "100") + ") and "
                        + "Drug_Name is null) or a.Drug_Code='" + drugInfoVO.DrugCode + "') and Eqp_ID='" + Paramters.G_EQP_ID + "' ";
                } 
                else
                {
                    sql = @"select Stock_ID,Eqp_ID,Row_NO,Col_NO,Stock_Width,Stock_Height,Drug_Name,Drug_Spec,Manufactory from Stock_list a left join drug_list b on a.Drug_Code=b.Drug_Code 
                        where ((Stock_width>=(" + drugInfoVO.PackageWidth + "+" + NEIni.ReadValue("./config.ini", "params", "packagewidthlow", "2") + ") and "
                        + " Stock_width<=(" + drugInfoVO.PackageWidth + "+" + NEIni.ReadValue("./config.ini", "params", "packagewidthupp", "10") + ") and "
                        + " Stock_height>=(" + drugInfoVO.PackageHeight + "+" + NEIni.ReadValue("./config.ini", "params", "packageheightlow", "30") + ") and "
                        + " Stock_height<=(" + drugInfoVO.PackageHeight + "+" + NEIni.ReadValue("./config.ini", "params", "packageheightupp", "100") + ")) "
                        + " or a.Drug_Code='" + drugInfoVO.DrugCode + "') and Eqp_ID='" + Paramters.G_EQP_ID + "' ";
                }
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    DataRow row = table.NewRow();

                    row["库位编码"] = dr.IsDBNull(0) ? "" : dr.GetString(0);
                    row["设备编号"] = dr.IsDBNull(1) ? "" : dr.GetString(1);
                    row["行号"] = dr.IsDBNull(2) ? "" : dr.GetString(2);
                    row["列号"] = dr.IsDBNull(3) ? "" : dr.GetString(3);
                    row["库位宽度"] = dr.IsDBNull(4) ? "" : dr.GetString(4);
                    row["药品宽度"] = drugInfoVO.PackageWidth;
                    row["宽度差"] = (dr.IsDBNull(4) ? 0 : dr.GetInt32(4)) - int.Parse(drugInfoVO.PackageWidth);
                    row["库位高度"] = dr.IsDBNull(5) ? "" : dr.GetString(5);
                    row["药品高度"] = drugInfoVO.PackageHeight;
                    row["高度差"] = (dr.IsDBNull(5) ? 0 : dr.GetInt32(5)) - int.Parse(drugInfoVO.PackageHeight);
                    row["药品名称"] = dr.IsDBNull(6) ? "" : dr.GetString(6);
                    row["规格"] = dr.IsDBNull(7) ? "" : dr.GetString(7);
                    row["厂家"] = dr.IsDBNull(8) ? "" : dr.GetString(8);

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
                //pool.releaseConnection(conn);
                conn.Close();
            }

            return table;
        }

        /// <summary>
        /// 绑定药槽，需要判断是否可以进行绑定，如果跨区了则提示不能绑定
        /// </summary>
        /// <param name="drugCode">药品编码</param>
        /// <param name="stockID">库位编码</param>
        /// <returns></returns>
        public bool bindChannel(string drugCode, string stockID, int lieh)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                bool flg = false;
                string sql = "";
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());//pool.getConnection();
                conn.Open();
                if (lieh < 60)
                {
                    // 判断是否有四区绑定
                    sql = "select max(Col_NO) from stock_list where Drug_Code='" + drugCode + "'";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    if (dr.Read())
                    {
                        if (!dr.IsDBNull(0) && dr.GetInt32(0) > 60)
                        {
                            flg = true;
                        }
                    }
                    dr.Close();
                } 
                else if (lieh > 60)  
                {
                    // 判断是否有一区绑定 //小于60不能绑定>60
                    sql = "select min(Col_NO) from stock_list where Drug_Code='" + drugCode + "'";
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    if (dr.Read())
                    {
                        if (!dr.IsDBNull(0) && dr.GetInt32(0) < 60)
                        {
                            flg = true;
                        }
                    }
                    dr.Close();
                } 
                
                if (flg)
                {
                    return false;
                }

                sql = "update stock_list set drug_Code='" + drugCode + "' where Stock_ID=" + stockID;
                new MySqlCommand(sql, conn).ExecuteNonQuery();
            }
            catch (System.Exception ex)
            {
                return false;
            }
            finally
            {
                //pool.releaseConnection(conn);
                conn.Close();
            }
            return true;
        }

        /// <summary>
        /// 清空药槽
        /// </summary>
        /// <param name="stockID">药槽编码</param>
        /// <returns></returns>
        public bool clearChannel(string stockID)
        {
            MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();

                string sql = "delete from stock_detail where Stock_ID=" + stockID;
                new MySqlCommand(sql, conn).ExecuteNonQuery();

                sql = "update stock_list set Drug_Code=null where Stock_ID=" + stockID;
                new MySqlCommand(sql, conn).ExecuteNonQuery();
            }
            catch (System.Exception ex)
            {
                return false;
            }
            finally
            {
                pool.releaseConnection(conn);
            }
            return true;
        }

        public string getBindInfo(VODrugInfo drugInfoVO)
        {
            string bindinfo = "";
            //MySqlPool pool = MySqlPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
                conn.Open();
                string sql = "select count(1),count(1)*floor(" + Paramters.G_CHN_LEN + "/" + drugInfoVO .PackageLength + ") from stock_list where eqp_id='" + Paramters.G_EQP_ID + "' and drug_code='" + drugInfoVO.DrugCode + "'";

                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                if (dr.Read())
                {
                    bindinfo = (dr.IsDBNull(0) ? "0" : dr.GetString(0)) + ";" + (dr.IsDBNull(1) ? "0" : dr.GetString(1));
                }
                else
                {
                    bindinfo = "0;0";
                }
                dr.Close();
            }
            catch (System.Exception ex)
            {
                return "Error";
            }
            finally
            {
                //pool.releaseConnection(conn);
                conn.Close();
            }
            return bindinfo;
        }

        public bool saveDrugInfo(VODrugInfo drugInfo)
        {
            string sql = "update drug_list set Drug_Barcode='" + drugInfo.DrugBarCode + "',package_length='" + drugInfo.PackageLength
                 + "',package_width='" + drugInfo.PackageWidth + "',package_height='" + drugInfo.PackageHeight + "',time1='" + drugInfo.AlarmStock.ToUpper() + "',time2='" + drugInfo.AlarmStock2.ToUpper()
                 + "',out_limit='" + drugInfo.OutLimit + "',stock_limit='" + drugInfo.StockLimit + "',stock_holdquantity='" + drugInfo.StockHoldquantity + "',storageLoc='" + drugInfo.StorageLoc + "' where drug_code='" + drugInfo.DrugCode + "'";
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                cmd.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                new LogInfo().info("保存失败" + ex.Message);
                return false;
            }
            finally
            {
                con.Close();
            }
            return true;
        }
    }
}
