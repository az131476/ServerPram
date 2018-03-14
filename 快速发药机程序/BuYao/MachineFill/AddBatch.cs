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
    public partial class AddBatch : Form
    {
        private string drugcode;

        public AddBatch(string drugcode)
        {
            InitializeComponent();
            this.drugcode = drugcode;
        }


        private void button1_Click(object sender, EventArgs e)
        {
            //新增批次
            string drugCode = drugcode;
            string bnotp = textBox1.Text;
            DateTime t = dateTimePicker1.Value;
            string date = t.ToString("yyyy-MM-dd");

            string sql = "insert into drug_bnotp_list(drug_code,bnotp,exp_date) values('"+drugCode+"','"+bnotp+"','"+date+"')";
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                cmd.ExecuteNonQuery();
                MessageBox.Show("保存成功");
            }
            catch (Exception ex)
            {
                new LogInfo().info("插入效期失败："+ex.Message);
            }
            finally 
            {
                con.Close();
            }
            this.Close();
            
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void AddBatch_Load(object sender, EventArgs e)
        {
            
        }
    }
}
