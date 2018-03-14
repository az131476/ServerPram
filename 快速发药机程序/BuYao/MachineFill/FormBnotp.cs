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
    public partial class FormBnotp : Form
    {
        private string drugCode;
        public string bnotp = "";
        public string date = "";
        public FormBnotp(string drugCode)
        {
            InitializeComponent();
            this.drugCode = drugCode;
        }

        private void FormBnotp_Load(object sender, EventArgs e)
        {
            showdataview2();
        }
        public void showdataview2() 
        {
            dataGridView2.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            dataGridView2.DataSource = new DrugBnotp().getDrugBnotp(drugCode);
            dataGridView2.RowHeadersDefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridView2.ClearSelection();//取消选中
            //dataGridView2.AllowUserToAddRows = true;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (dataGridView2.Rows.Count > 0)
            {
                bnotp = dataGridView2.Rows[dataGridView2.CurrentRow.Index].Cells[0].Value.ToString();
                date = dataGridView2.Rows[dataGridView2.CurrentRow.Index].Cells[1].Value.ToString();
            }
            else 
            {
                MessageBox.Show("无查询结果"+dataGridView2.Rows.Count+"行");
            }
            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button6_Click(object sender, EventArgs e)
        {
            AddBatch addbatch = new AddBatch(drugCode);
            addbatch.ShowDialog();
        }

        private void button7_Click(object sender, EventArgs e)
        {
            showdataview2();
        }

        private void button5_Click(object sender, EventArgs e)
        {
            //删除批次
            string bnotp_id = dataGridView2.Rows[dataGridView2.CurrentCell.RowIndex].Cells[0].Value.ToString();

            string sql = "DELETE from drug_bnotp_list where drug_code='" + drugCode + "' and bnotp='" + bnotp_id + "'";
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                cmd.ExecuteNonQuery();
                MessageBox.Show("删除成功");
            }
            catch (Exception ex)
            {
                MessageBox.Show("删除失败"+ex.Message);
            }
            finally
            {
                con.Close();
            }
        }
    }
}
