using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using MySql.Data.MySqlClient;
using System.Configuration;

namespace MachineFill
{
    public partial class DrugViewSearch : Form
    {
        public DrugViewSearch()
        {
            InitializeComponent();
        }

        private void DrugViewSearch_Load(object sender, EventArgs e)
        {
            string sql = "select Drug_Name as 药品名称,Drug_Spec as 药品规格,Manufactory as 厂家,StockFillQty as 可补量,storageloc as 二级库位,stockqty as 库存量,stockmaxqty as 最大库存 ";
                   sql += "from v_f_stock2 where stockfillqty>0 and StockQty /StockMaxQty <0.3 ";
                   sql += "order by stockfillqty desc ";
            search(sql);

            // 
        }
        public void search(string sql) 
        {
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                DataSet ds = new DataSet();
                MySqlDataAdapter adapter = new MySqlDataAdapter(cmd);
                adapter.Fill(ds);
                dataGridView1.DataSource = ds.Tables[0];
            }
            catch (Exception ex)
            {
                new LogInfo().info("补药查询错误！" + ex.Message);
            }
            finally 
            {
                con.Close();
            }
        }

        private void button_H_query_Click(object sender, EventArgs e) //
        {
            string sql = "select Drug_Name as 药品名称,Drug_Spec as 药品规格,Manufactory as 厂家,StockFillQty as 可补量,storageloc as 二级库位,stockqty as 库存量,stockmaxqty as 最大库存 ";
                   sql += "from v_f_stock2 where stockfillqty>0  ";
                   sql += "and (short_code like '%" + tb_search.Text + "%' or drug_barcode='" + tb_search.Text + "') order by stockfillqty desc";
            search(sql);
            tb_search.Clear();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void tb_search_TextChanged(object sender, EventArgs e)
        {
            if (tb_search.Text.Length == 13) 
            {
                button_H_query_Click(sender,e);
                
            }
        }
    }
}
