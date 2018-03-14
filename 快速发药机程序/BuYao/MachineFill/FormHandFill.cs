using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace MachineFill
{
    public partial class FormHandFill : Form
    {
        private VODrugInfo druginfo;

        public FormHandFill(VODrugInfo druginfo)
        {
            InitializeComponent();
            this.druginfo = druginfo;
        }

        private void FormHandFill_Load(object sender, EventArgs e)
        {
            this.Text = druginfo.DrugName;
            dataGridView1.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;
            dataGridView1.DataSource = new DrugFill().getStockList(druginfo.DrugCode);

            for (int i = 0; i < 5; i++)
            {
                dataGridView1.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridView1.Columns[5].Visible = false;
            dataGridView1.Columns[6].Visible = false;
            dataGridView1.Columns[7].Visible = false;
            dataGridView1.Columns[8].Visible = false;
            dataGridView1.Columns[4].DefaultCellStyle.ForeColor = Color.Red;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if ("".Equals(textBox1.Text) || dataGridView1.CurrentRow == null || dataGridView1.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择记录！");
                return;
            }

            try
            {
                int i = int.Parse(textBox4.Text);
                if (i == 0)
                {
                    MessageBox.Show("补药数量不能为0！");
                    return;
                }
            }
            catch (System.Exception ex)
            {
                MessageBox.Show("数量格式不正确！");
                return;
            }

            new DrugFill().updateStock(int.Parse(textBox1.Text), int.Parse(textBox4.Text), "", "");

            textBox1.Text = "";
            textBox2.Text = "";
            textBox3.Text = "";
            textBox4.Text = "";
            dataGridView1.Rows.RemoveAt(dataGridView1.CurrentRow.Index);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void dataGridView1_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            textBox1.Text = dataGridView1.Rows[dataGridView1.CurrentRow.Index].Cells[0].Value.ToString();
            textBox2.Text = dataGridView1.Rows[dataGridView1.CurrentRow.Index].Cells[1].Value.ToString();
            textBox3.Text = dataGridView1.Rows[dataGridView1.CurrentRow.Index].Cells[2].Value.ToString();
            textBox4.Text = dataGridView1.Rows[dataGridView1.CurrentRow.Index].Cells[4].Value.ToString();
        }
    }
}
