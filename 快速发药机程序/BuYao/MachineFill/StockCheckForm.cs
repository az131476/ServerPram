using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using NEBasic;
using System.Configuration;
using MySql.Data.MySqlClient;

namespace MachineFill
{
    public partial class StockCheckForm : Form
    {
        private List<VOStockList> sList;
        private bool pause = false;
        private int iProcess = 0;
        private bool haschecked = true;

        public StockCheckForm(List<VOStockList> sList)
        {
            InitializeComponent();
            this.sList = sList;
        }

        private void StockCheckForm_Load(object sender, EventArgs e)
        {
            dataGridViewCK.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;
            dataGridViewCK.AutoGenerateColumns = false;

            dataGridViewCK.Columns[1].Width = 60;
            dataGridViewCK.Columns[2].Width = 60;
            dataGridViewCK.Columns[3].Width = 60;
            dataGridViewCK.Columns[4].Width = 250;
            dataGridViewCK.Columns[5].Width = 190;
            dataGridViewCK.Columns[6].Width = 110;
            dataGridViewCK.Columns[7].Width = 110;
            dataGridViewCK.Columns[1].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[2].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[3].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[6].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[7].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;

            dataGridViewCK.Columns[0].Visible = false;
            dataGridViewCK.ClearSelection();//取消选中

            label5.Text = "0/" + sList.Count;
            label2.Text = "";

            timer1.Interval = 1000;
            timer1.Enabled = true;
            timer2.Interval = 500;
            timer2.Enabled = true;

            //timer3.Interval = 200;
            //timer3.Enabled = true;

            
        }

        private void dataGridViewCK_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (dataGridViewCK.Columns[e.ColumnIndex].Name == "Edit")
            {
                MessageBox.Show("行: " + e.RowIndex.ToString() + ", 列: " + e.ColumnIndex.ToString() + "; 被点击了");
                dataGridViewCK.Rows.RemoveAt(dataGridViewCK.CurrentRow.Index);
            }
            else if (dataGridViewCK.Columns[e.ColumnIndex].Name == "Submit")
            {
                MessageBox.Show("行: " + e.RowIndex.ToString() + ", 列: " + e.ColumnIndex.ToString() + "; 被点击了");
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            //添加行数据                    
            DataGridViewRow Row = new DataGridViewRow();
            dataGridViewCK.RowHeadersWidth = 45;
            Row.Height = 35;

            int index = dataGridViewCK.Rows.Add(Row);
            dataGridViewCK.Rows[index].Cells[0].Value = "1";
            dataGridViewCK.Rows[index].Cells[1].Value = "2";
        }

        private void button2_Click(object sender, EventArgs e)
        {
            timer1.Enabled = false;
            timer2.Enabled = false;
            timer3.Enabled = false;
            this.Close();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            pause = !pause;
            if (pause)
            {
                button1.Text = "继续盘点";
            } 
            else
            {
                button1.Text = "暂停盘点";
            }
        }
        
        private void button5_Click(object sender, EventArgs e)
        {
            if (dataGridViewCK.CurrentRow == null || dataGridViewCK.CurrentRow.Index < 0)
            {
                MessageBox.Show("没有记录！");
                return;
            }
            for (int i = 0; i < dataGridViewCK.Rows.Count; i++)
            {
                new StockCheckDao().stockChecked(dataGridViewCK.Rows[i].Cells[0].Value.ToString(), int.Parse(dataGridViewCK.Rows[i].Cells[6].Value.ToString()), int.Parse(dataGridViewCK.Rows[i].Cells[7].Value.ToString()));
            }
            dataGridViewCK.Rows.Clear();
        }

        private void button3_Click_1(object sender, EventArgs e)
        {
            if (dataGridViewCK.CurrentRow == null || dataGridViewCK.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择记录！");
                return;
            }
            StockCheckForm2 form = new StockCheckForm2();
            if (form.ShowDialog() == DialogResult.OK)
            {
                new StockCheckDao().stockChecked(dataGridViewCK.CurrentRow.Cells[0].Value.ToString(), int.Parse(dataGridViewCK.CurrentRow.Cells[6].Value.ToString()), form.cqty);
            }
            dataGridViewCK.Rows.RemoveAt(dataGridViewCK.CurrentRow.Index);
        }

        private void button4_Click(object sender, EventArgs e)
        {
            if (dataGridViewCK.CurrentRow == null || dataGridViewCK.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择记录！");
                return;
            }
            new StockCheckDao().stockChecked(dataGridViewCK.CurrentRow.Cells[0].Value.ToString(), int.Parse(dataGridViewCK.CurrentRow.Cells[6].Value.ToString()), int.Parse(dataGridViewCK.CurrentRow.Cells[7].Value.ToString()));
            try
            {
                dataGridViewCK.Rows.RemoveAt(dataGridViewCK.CurrentRow.Index);
            }catch(System.ArgumentOutOfRangeException ex)
            {
                MessageBox.Show("异常1："+ex.Message);
            }catch(InvalidOperationException ey)
            {
                MessageBox.Show("异常2"+ey.Message);
            }
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (haschecked && !pause)
            {
                if (iProcess >= sList.Count)
                {
                    label2.Text = "盘点完毕！";
                    timer1.Enabled = false;
                    label5.Text = sList.Count + "/" + sList.Count;
                    return;
                }
                else
                {
                    label5.Text = iProcess + "/" + sList.Count;
                }
                label2.Text = "正在盘点 " + sList[iProcess].RowNO + " 行 " + sList[iProcess].ColNO + " 列";

                // 发送盘点指令
                string side = "1";
                if (int.Parse(sList[iProcess].ColNO) < 61)
                {
                    side = "1";
                } 
                else
                {
                    side = "2";
                }
                NELog.WriteLog("开始盘点："+iProcess);
                
                //string code = "$Q3012F1" + side + sList[iProcess].StockHor.Length + sList[iProcess].StockHor + sList[iProcess].StockVer.Length + sList[iProcess].StockVer + string.Format("{0:D3}", int.Parse(sList[iProcess].StockWidth)) + string.Format("{0:D2}", int.Parse(sList[iProcess].RowNO)) + string.Format("{0:D3}", int.Parse(sList[iProcess].ColNO)) + string.Format("{0:D3}", int.Parse(sList[iProcess].PackageLength)) + Rand.Str(4) + "*";
                string code = "$Q3012F1" + side + sList[iProcess].StockHor.Length + sList[iProcess].StockHor + sList[iProcess].StockVer.Length + sList[iProcess].StockVer + string.Format("{0:D3}",int.Parse(sList[iProcess].StockWidth)) + string.Format("{0:D2}", int.Parse(sList[iProcess].RowNO)) + string.Format("{0:D3}", int.Parse(sList[iProcess].ColNO)) + string.Format("{0:D3}", int.Parse(sList[iProcess].PackageLength)) + Rand.Str(4) + "*";

                MachineFill.sendMsg(code);
                haschecked = false;
            }
        }
        
        private void timer2_Tick(object sender, EventArgs e)
        {
            try
            {
                if (CoderQueue.ckSyncdQueue.Count < 1)
                {
                    return;
                }
                string code = (string)CoderQueue.ckSyncdQueue.Dequeue();

                if (code == null)
                {
                    return;
                }
                /*
                 * 判断是否门控异常
                 */
 
                if (code.Substring(0, 5).Equals("$Q302"))
                {
                    NELog.WriteLog("盘点完毕："+iProcess);
                    // 解析盘点返回，$Q302/4 2 F1 01 002 000 0000 XXXX*
                    int h = int.Parse(code.Substring(8, 2));
                    int l = int.Parse(code.Substring(10, 3));
                    int c = int.Parse(code.Substring(13, 3));
                    int cd = int.Parse(code.Substring(16, 4));
                    VOStockList stock = new StockCheckDao().stockChecked(h, l, c, cd);
                    if (stock != null && iProcess < sList.Count)
                    {
                        int index = this.dataGridViewCK.Rows.Add();
                        this.dataGridViewCK.Rows[index].Cells[0].Value = sList[iProcess].StockID;
                        this.dataGridViewCK.Rows[index].Cells[1].Value = sList[iProcess].EqpID;
                        this.dataGridViewCK.Rows[index].Cells[2].Value = sList[iProcess].RowNO;
                        this.dataGridViewCK.Rows[index].Cells[3].Value = sList[iProcess].ColNO;
                        this.dataGridViewCK.Rows[index].Cells[4].Value = sList[iProcess].DrugName;
                        this.dataGridViewCK.Rows[index].Cells[5].Value = sList[iProcess].DrugSpec;
                        this.dataGridViewCK.Rows[index].Cells[6].Value = stock.Quantity;
                        this.dataGridViewCK.Rows[index].Cells[7].Value = stock.CheckQuantity;
                    }
                    label2.Text = "盘点完毕 " + sList[iProcess].RowNO + " 行 " + sList[iProcess].ColNO + " 列";
                    NELog.WriteLog("-------" + iProcess);
                    iProcess = iProcess + 1;
                    haschecked = true;
                }
                else if (code.Substring(0, 5).Equals("$Q306"))
                {
                    NELog.WriteLog("----------------");
                    haschecked = true;
                    iProcess = iProcess + 1;
                }
                else if (code.Substring(0, 5).Equals("$Q990"))
                {
                    // E1机械手运动异常
                    string errCode = code.Substring(10, 2);

                    if (errCode.Equals("11"))
                    {
                        label1.Text = "机械手未初始化！";
                    }
                    else if (errCode.Equals("02"))
                    {
                        label1.Text = "急停异常！";
                    }
                    else if (errCode.Equals("03"))
                    {
                        label1.Text = "门控异常！";
                    }
                    else if (errCode.Equals("04"))
                    {
                        label1.Text = "对射异常！";
                    }
                    else if (errCode.Equals("21"))
                    {
                        label1.Text = "运动超时！";
                    }
                    else
                    {
                        label1.Text = "机械手运动异常：" + errCode;
                    }
                    button6.Visible = true;
                }
            }
            catch (System.Exception ex)
            {
                NELog.WriteLog(ex.ToString());	
            }
        }

        private void timer3_Tick(object sender, EventArgs e)
        {
            NELog.WriteLog("==============" + iProcess);
        }

        private void StockCheckForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            timer1.Enabled = false;
            timer2.Enabled = false;
            timer3.Enabled = false;
        }

        private void button6_Click(object sender, EventArgs e)
        {
            string code = (string)CoderQueue.ckSyncdQueue.Dequeue();
            string errKey = code.Substring(0, 5);
            if (errKey.Equals("$Q990"))
            {
                // 异常解决
                MachineFill.sendMsg("$Q3052F1" + Rand.Str(4) + "*");
            }
            button6.Visible = false;
            label1.Text = "运行正常";
        }
    }
}
