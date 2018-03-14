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
    public partial class BindForm : Form
    {
        private VODrugInfo drugInfoVO;
        public string bindChnCount;
        public string bindChnMaxStock;
        private string code = "";
        private string rowIndex = "";

        public BindForm(VODrugInfo drugInfoVO)
        {
            InitializeComponent();
            this.drugInfoVO = drugInfoVO;
        }

        private void BindForm_Load(object sender, EventArgs e)
        {
            dataGridView1.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;
            checkBox1.Checked = true;
        }

        // 绑定
        private void btnQuery_Click(object sender, EventArgs e)
        {
            if (dataGridView1.CurrentRow == null || dataGridView1.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要绑定的记录！");
                return;
            }

            if (!dataGridView1.CurrentRow.Cells[10].Value.ToString().Equals(""))
            {
                MessageBox.Show("此库位非空闲，无法绑定至此库位！");
                return;
            }

            if (new DrugEdit().bindChannel(drugInfoVO.DrugCode, dataGridView1.CurrentRow.Cells[0].Value.ToString(), int.Parse(dataGridView1.CurrentRow.Cells[3].Value.ToString())))
            {
                dataGridView1.CurrentRow.Cells[10].Value = drugInfoVO.DrugName;
                dataGridView1.CurrentRow.Cells[11].Value = drugInfoVO.DrugSpec;
                dataGridView1.CurrentRow.Cells[12].Value = drugInfoVO.Manufactory;
            } 
            else
            {
                MessageBox.Show("绑定失败！");
            }

            parseInfo(new DrugEdit().getBindInfo(drugInfoVO));
        }

        // 解除
        private void btnSave_Click(object sender, EventArgs e)
        {
            if (dataGridView1.CurrentRow == null || dataGridView1.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要解除绑定的记录！");
                return;
            }
            else
            {
                if (DialogResult.No == MessageBox.Show("是否确认解除绑定？", "提示", MessageBoxButtons.YesNo))
                {
                    return;
                }
            }

            if (new DrugEdit().clearChannel(dataGridView1.CurrentRow.Cells[0].Value.ToString()))
            {
                dataGridView1.CurrentRow.Cells[10].Value = "";
                dataGridView1.CurrentRow.Cells[11].Value = "";
                dataGridView1.CurrentRow.Cells[12].Value = "";
            } 
            else
            {
                MessageBox.Show("解除绑定失败！");
            }

            parseInfo(new DrugEdit().getBindInfo(drugInfoVO));
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            string row = "";
            string col = "";
            if (checkBox1.Checked)
            {
                DataTable dt = new DrugEdit().getStockByPackage(true, drugInfoVO);
                if (dt.Rows.Count > 0)
                {
                    dataGridView1.DataSource = dt;
                    
                }
                else 
                {
                    MessageBox.Show("空");
                }
            }
            else
            {
                dataGridView1.DataSource = new DrugEdit().getStockByPackage(false, drugInfoVO);
            }

            dataGridView1.Columns[3].Frozen = true;
            for (int i = 0; i < 10; i++)
            {
                dataGridView1.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridView1.Columns[6].DefaultCellStyle.ForeColor = Color.Red;
            dataGridView1.Columns[9].DefaultCellStyle.ForeColor = Color.Red;
            dataGridView1.Columns[0].Visible = false;
            dataGridView1.ClearSelection();//取消选中

            DataTable table = new DrugEdit().getStockByPackage(true, drugInfoVO);
            for (int i = 0; i < table.Rows.Count; i++)
            {

                string name = table.Rows[i]["药品名称"].ToString();
                if (!name.Equals(""))
                {
                    row = table.Rows[i]["行号"].ToString()+"行";
                    col = table.Rows[i]["列号"].ToString()+"列 ;";

                    code += row + col;
                    rowIndex += i + ";";
                }
            }

            label3.Text = "已绑定药槽：" + code ;

            parseInfo(new DrugEdit().getBindInfo(drugInfoVO));
        }

        private void parseInfo(string info)
        {
            string[] arr = info.Split(';');
            if (arr.Length == 2)
            {
                bindChnCount = arr[0];
                bindChnMaxStock = arr[1];
                label1.Text = "当前设备已绑定库位数：" + arr[0] + " 最大库存数量：" + arr[1];
            } 
            else
            {
                label1.Text = "统计失败!" + "info:" + info;
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            
            dataGridView1.Rows[Int32.Parse(rowIndex.Substring(0,rowIndex.IndexOf(';',0)))].Selected = true;
            //跳转至绑定行--将某行设置为第一行显示
            dataGridView1.FirstDisplayedScrollingRowIndex = Int32.Parse(rowIndex.Substring(0, rowIndex.IndexOf(';', 0)));
            new LogInfo().info(rowIndex+"");
            new LogInfo().info(rowIndex.IndexOf(';', 0)+"");
        }
    }
}
