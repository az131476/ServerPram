using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using NEBasic;
using MySql.Data.MySqlClient;
using System.Configuration;
using System.Speech;
using System.Speech.Synthesis;
using System.Threading;

namespace MachineFill
{
    public partial class FillForm : Form
    {
        private int offsetHor;
        private string errKey = "";
        private string errCode = "";
        private VODrugInfo drugInfo;
        private int side;
        //private SpeechSynthesizer speech = new SpeechSynthesizer();
        public FillForm(VODrugInfo drugInfo)
        {
            InitializeComponent();
            this.drugInfo = drugInfo;
        }

        private void FillForm_Load(object sender, EventArgs e)
        {
            offsetHor = int.Parse(NEIni.ReadValue(".\\config.ini", "param", "offsetHor", "0"));
            label3.Text = drugInfo.DrugName;
            label4.Text = drugInfo.DrugSpec;
            label5.Text = drugInfo.Manufactory;
            //label8.Text = drugInfo;   //读取已有批号
            label7.Text = "";
            label6.Text = "正在进行补药……";
            label2.Text = "";
            label8.Text = "";

            dataGridView2.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            dataGridView2.DataSource = new DrugFill().getStockList(drugInfo.DrugCode);
            for (int i = 0; i < 6; i++)
            {
                dataGridView2.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridView2.Columns[5].Visible = false;
            dataGridView2.Columns[6].Visible = false;
            dataGridView2.Columns[7].Visible = false;
            dataGridView2.Columns[8].Visible = false;
            dataGridView2.Columns[9].Visible = false;
 
            dataGridView2.RowHeadersDefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridView2.ClearSelection();//取消选中

            //dataGridView2.CurrentRow.Selected = true;
            //label8.Text = dataGridView2.Rows[dataGridView2.CurrentCell.RowIndex].Cells[11].Value.ToString();

            if (System.IO.File.Exists(@"./images/" + drugInfo.DrugCode + ".png"))
            {
                pictureBox1.Image = Image.FromFile(@"./images/" + drugInfo.DrugCode + ".png", false);
            }
            else
            {
                pictureBox1.Image = Image.FromFile(@"./images/warning.png", false);
            }
            
            timer1.Interval = 500;
            timer1.Enabled = true;
            timer2.Interval = 500;
            timer2.Enabled = true;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            timer1.Enabled = false;
            if(dataGridView2.RowCount == 0)
            {
                label7.Text = "此药品库位可能被锁定。无需要补药的库位";
                return;
            }
            // 判断用哪一侧上药
            side = 1;
            for (int i = 0; i < dataGridView2.RowCount; i++)
            {
                if (int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()) > 60)
                {
                    side = 2;
                    break;
                }
            }
            string code = "$Q2012F1" + side + NEIni.ReadValue(".\\config.ini", "param", "check", "0");
            code += string.Format("{0:D3}", int.Parse(drugInfo.PackageLength));
            code += string.Format("{0:D3}", int.Parse(drugInfo.PackageWidth));
            //code += string.Format("{0:D2}", int.Parse(drugInfo.StockLimit));
            code += string.Format("{0:D2}", int.Parse(dataGridView2.Rows[0].Cells[9].Value.ToString()));

            int needQty = 0;
            // 药槽
            for (int i = 0; i < dataGridView2.RowCount; i++)
            {
                code += string.Format("{0:D2}", int.Parse(dataGridView2.Rows[i].Cells[1].Value.ToString()));
                code += string.Format("{0:D3}", int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()));
                code += string.Format("{0:D2}", int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()));
                needQty += int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString());

                if (side == 2 && int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()) < 61)
                {
                    int hor = int.Parse(dataGridView2.Rows[i].Cells[5].Value.ToString()) - offsetHor + int.Parse(dataGridView2.Rows[i].Cells[8].Value.ToString());
                    code += hor.ToString().Length;
                    code += hor;
                }
                else
                {
                    code += dataGridView2.Rows[i].Cells[5].Value.ToString().Length;
                    code += dataGridView2.Rows[i].Cells[5].Value.ToString();//H
                }
                code += dataGridView2.Rows[i].Cells[6].Value.ToString().Length;
                code += dataGridView2.Rows[i].Cells[6].Value.ToString();//V
                code += string.Format("{0:D3}", int.Parse(dataGridView2.Rows[i].Cells[8].Value.ToString()));
            }
            code += Rand.Str(4) + "*";
         //   NELog.WriteLog(code);
            label2.Text = needQty.ToString();
            dataGridView2.CurrentRow.Selected = true;
            label8.Text = dataGridView2.Rows[dataGridView2.CurrentCell.RowIndex].Cells[11].Value.ToString();
            MachineFill.sendMsg(code);  //获取补药及位置信息-完成初始化
        }

        private void timer2_Tick(object sender, EventArgs e)
        {
            try
            {
                string code = (string)CoderQueue.fdSyncdQueue.Dequeue();
                if (code == null)
                {
                    return;
                }

                if (code.Substring(0, 5).Equals("$Q202")) //完成初始化
                {
                    //SpeechSynthesizer ss = new SpeechSynthesizer();
                    //ss.Rate = -1;
                    //if (side.Equals("1"))
                    //{
                    //    ss.Speak("请将药品放到左侧");
                    //}
                    //else 
                    //{
                    //    ss.Speak("请将药品放到右侧");
                    //}
                    // 提示可放药了 $Q2022F1 00(正常) xxxx*
                    label7.Text = side == 1 ? "将药品放到左侧" : "将药品放到右侧";
                    button5.Visible = true;
                    button5.Enabled = true;
                    textBox1.Visible = false;
                }
                else if (code.Substring(0, 5).Equals("$Q204"))
                {
                    // 盘点返回 $Q2042F1 01 001 05 xxxx*
                    int row = int.Parse(code.Substring(8, 2));
                    int col = int.Parse(code.Substring(10, 3));
                    int qty = int.Parse(code.Substring(13, 2));
                    new StockCheckDao().stockChecked(row, col, qty);
                    for (int i = 0; i < dataGridView2.RowCount; i++)
                    {
                        if (int.Parse(dataGridView2.Rows[i].Cells[1].Value.ToString()) == row && int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()) == col)
                        {
                            dataGridView2.Rows[i].Cells[3].Value = qty + "";
                            dataGridView2.Rows[i].Cells[4].Value = (int.Parse(drugInfo.StockLimit) - qty) + "";
                            if (int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()) < 1)
                            {
                                dataGridView2.Rows.RemoveAt(i);
                            }
                            break;
                        }                       
                    }
                }
                else if (code.Substring(0, 5).Equals("$Q206"))
                {
                    // 上药结果 $Q2062F1 01 001 05 xxxx*
                    int row = int.Parse(code.Substring(8, 2));
                    int col = int.Parse(code.Substring(10, 3));
                    int qty = int.Parse(code.Substring(13, 2));
                    new DrugFill().updateStock(row, col, qty, label8.Text, "");
                    for (int i = 0; i < dataGridView2.RowCount; i++)
                    {
                        if (int.Parse(dataGridView2.Rows[i].Cells[1].Value.ToString()) == row && int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()) == col)
                        {
                            dataGridView2.Rows[i].Cells[3].Value = (int.Parse(dataGridView2.Rows[i].Cells[3].Value.ToString()) + qty) + "";
                            dataGridView2.Rows[i].Cells[4].Value = (int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()) - qty) + "";
                            if (int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()) < 1)
                            {
                                dataGridView2.Rows.RemoveAt(i);
                            }
                            updataBnot(row.ToString(), col.ToString());
                            break;
                        }
                        label2.Text = "  " + int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString());
                    }
                    int needQty = 0;
                    for (int i = 0; i < dataGridView2.RowCount; i++)
                    {
                        needQty += int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString());
                    }
                    //label2.Text = "    "+needQty.ToString();
                }
                else if (code.Substring(0, 5).Equals("$Q992"))
                {
                    // 上药结果 $Q2062F1 01 001 05 xxxx* 返回数量为0，提示盘点
                    int row = int.Parse(code.Substring(8, 2));
                    int col = int.Parse(code.Substring(10, 3));
                    int qty = int.Parse(code.Substring(13, 2));
                    new DrugFill().updateStock(row, col, qty, label8.Text, "");
                    for (int i = 0; i < dataGridView2.RowCount; i++)
                    {
                        if (int.Parse(dataGridView2.Rows[i].Cells[1].Value.ToString()) == row && int.Parse(dataGridView2.Rows[i].Cells[2].Value.ToString()) == col)
                        {
                            dataGridView2.Rows[i].Cells[3].Value = (int.Parse(dataGridView2.Rows[i].Cells[3].Value.ToString()) + qty) + "";
                            dataGridView2.Rows[i].Cells[4].Value = (int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()) - qty) + "";
                            if (int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString()) < 1)
                            {
                                dataGridView2.Rows.RemoveAt(i);
                            }
                            updataBnot(row.ToString(), col.ToString());
                            break;
                        }
                    }

                    int needQty = 0;
                    for (int i = 0; i < dataGridView2.RowCount; i++)
                    {
                        needQty += int.Parse(dataGridView2.Rows[i].Cells[4].Value.ToString());
                    }
                    label2.Text = needQty.ToString();
                    textBox1.Visible = true;
                    textBox1.Text = row + "行" + col + "完成加药数量为" + qty + "  药槽计数可能发生异常，建议盘点";
                    textBox1.ForeColor = Color.Red;
                }
                else if (code.Substring(0, 5).Equals("$Q208"))
                {
                    // 上药完成
                    if (dataGridView2.RowCount == 0)
                    {
                        //SpeechSynthesizer ss = new SpeechSynthesizer();
                        //ss.Rate = -1;
                        label7.Text = "上药完成！";
                        //ss.SpeakAsync(label7.Text);
                        timer1.Enabled = false;
                        timer2.Enabled = false;
                        this.Close();
                    }
                    else
                    {
                        // 还有药槽没完成，继续
                        timer1.Enabled = true;
                    }
                }
                //else if (code.Substring(0, 5).Equals("$Q991"))
                //{
                //    string code_d = code.Substring(8, 2);
                //    string num_back = code.Substring(10, 2);
                //    if (code_d.Equals("E3"))
                //    {
                //        //返回已加药数量,同步到界面
                //        label2.ForeColor = Color.Red;
                //        int number = Convert.ToInt32(label2.Text) - Convert.ToInt32(num_back);
                //        label15.Text = number.ToString()+"/"+label2.Text;
                //    }
                //}
                else if (code.Substring(0, 5).Equals("$Q210"))
                {
                    //SpeechSynthesizer ss = new SpeechSynthesizer();
                    //ss.Rate = -1;
                    errKey = code.Substring(8, 2);
                    errCode = code.Substring(10, 2);

                    // 异常 $Q2102F1 E0 01(初始化时急停)/02(初始化门控)/03(有药) xxxx*
                    if (errKey.Equals("E0"))
                    {
                        if (errCode.Equals("01"))
                        {
                            label7.Text = "急停异常！旋开急停后点击『继续』按钮";
                            //ss.SpeakAsync(label7.Text);
                        }
                        else if (errCode.Equals("03"))
                        {
                            label7.Text = "机械手上有药品！取出药品后点击『继续』按钮";
                            //ss.SpeakAsync(label7.Text);
                        }else if(errCode.Equals("04"))
                        {
                            label7.Text = "返回窗口错误，检查是否异常？点击『继续』按钮";
                        }
                        button2.Visible = true;
                    } 
                    else if (errKey.Equals("E2"))
                    {
                        // E2加药过程异常
                        if (errCode.Equals("01"))
                        {
                            label7.Text = "药品未进入药槽！";
                            //ss.SpeakAsync(label7.Text);
                        } 
                        else if (errCode.Equals("02"))
                        {
                            label7.Text = "拨药异常报警！";
                            //ss.SpeakAsync(label7.Text);
                        }
                        else if (errCode.Equals("03"))
                        {
                            label7.Text = "齐药异常报警！";
                            //ss.SpeakAsync(label7.Text);
                        }
                        else if (errCode.Equals("04"))
                        {
                            Thread.Sleep(3000);
                            label7.Text = "加药异常！";
                            //ss.SpeakAsync("加药出现异常，请处理");
                        }else if(errCode.Equals("05"))
                        {
                            label7.Text = "加药超时";
                        }
                        else
                        {
                            label7.Text = "未知异常："+errCode;
                            //ss.SpeakAsync(label7.Text);
                        }

                        button4.Visible = true;
                        //textBox1.Visible = true;
                        //textBox1.Text = "该药槽可能未计到数，建议盘点";
                        //textBox1.ForeColor = Color.Red;

                    }
                    else if (errKey.Equals("E1"))
                    {
                        // E1机械手运动异常
                        if (errCode.Equals("11"))
                        {
                            label7.Text = "机械手未初始化！";
                        }
                        else if (errCode.Equals("02"))
                        {
                            label7.Text = "急停异常！";
                        }
                        else if (errCode.Equals("03"))
                        {
                            label7.Text = "门控异常！";
                        }
                        else if (errCode.Equals("04"))
                        {
                            label7.Text = "对射异常！";
                        }
                        else if (errCode.Equals("21"))
                        {
                            label7.Text = "运动超时！";
                        }
                        else
                        {
                            label7.Text = "机械手运动异常：" + errCode;
                        }
                        button2.Visible = true;
                    }
                }
            }
            catch (System.Exception ex)
            {
                NELog.WriteLog(ex.ToString());
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            MachineFill.sendMsg("$Q2032F101" + Rand.Str(4) + "*");
            button5.Enabled = false;
            label7.Text = "";
        }

        // 停止
        private void button3_Click(object sender, EventArgs e)
        {
            MachineFill.sendMsg("$Q2052F101" + Rand.Str(4) + "*");
            timer1.Enabled = false;
            timer2.Enabled = false;
            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (errKey.Equals("E0"))
            {
                // 初始化继续
                timer1.Enabled = true;
            }
            else if (errKey.Equals("E1"))
            {
                // 异常解决
                MachineFill.sendMsg("$Q2112F101" + Rand.Str(4) + "*");
            }
            button2.Visible = false;
            label7.Text = "";
        }

        private void button1_Click(object sender, EventArgs e)
        {

        }

        private void button4_Click(object sender, EventArgs e)
        {
            /*
             * 卡药，先降落-处理药品-重新加药
             */
            MachineFill.sendMsg("$Q2192F101" + Rand.Str(4) + "*");
            button4.Visible = false;

            /*
             * 异常处理完毕，放好药品点继续加药
             */
            //button2.Visible = true;
            //label7.Text = "降落后请摆放好药品，点继续操作！";
            //speech.Rate = -1;
            //speech.SpeakAsync("机械手降落到能处理的位置时拍下急停按钮打开玻璃门，将");
        }

        // 选批次
        private void button6_Click(object sender, EventArgs e)
        {
            FormBnotp form = new FormBnotp(drugInfo.DrugCode);
            form.ShowDialog();

            label8.Text = form.bnotp;

            //根据行列号，返回加药完成后将选择的批号保存到数据库中
            //更新批号前先判断上一次的效期是否在提醒范围内
            
            bnotpTemp(form.bnotp,form.date);
            
        }
        private void updataBnot(string row,string col)
        {
            string sql_1 = "select bnotp,state,date from bnotp_temp"; //从临时表中取出录入的效期
            MySqlConnection con_1 = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            MySqlDataReader rd = null;
            string bnotp = null;
            string exp_date = null;
            try
            {
                con_1.Open();
                MySqlCommand cmd_1 = new MySqlCommand(sql_1, con_1);
                rd = cmd_1.ExecuteReader();
                while (rd.Read())
                {
                    bnotp = rd[0].ToString();
                    string state = rd[1].ToString();
                    exp_date = rd[2].ToString();
                }
            }
            catch (Exception ex) { }
            finally { rd.Close(); con_1.Close(); }

            //更具加药时的行列号以及录入的效期，更新此效期到stock_detail表中
            string sql = "update stock_detail set bnotp='" + bnotp + "' ,Exp_Date='" + exp_date + "' where Row_NO='" + row + "' and Col_NO='" + col + "'";
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                cmd.CommandText = sql;
                cmd.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                //
            }
            finally
            {
                con.Close();
            }
        }
        private void bnotpTemp(string bnop,string date)//选择效期后将其保存到临时数据表中
        {
            string sql = "update bnotp_temp set bnotp = '"+bnop+"',date='"+date+"' where state='1'";
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                cmd.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                new LogInfo().info("保存失败"+ex.Message);
            }
            finally
            {
                con.Close();
            }
        }

        private void button7_Click(object sender, EventArgs e)
        {
            DrugViewSearch dvs = new DrugViewSearch();
            dvs.Show();
        }

        private void button8_Click(object sender, EventArgs e)
        {

            if (MessageBox.Show("    机械手回到窗口后未完成初始化?", "提示", MessageBoxButtons.OKCancel) == DialogResult.OK)
            {
                MachineFill.sendMsg("$Qfwb" + Rand.Str(4) + "*");

                button8.BackColor = Color.Gray;
                button8.Enabled = false;

                Thread.Sleep(2000);

                button8.BackColor = Color.Red;
                button8.Enabled = true;
            }
            else 
            {

            }
        }
    }
}
