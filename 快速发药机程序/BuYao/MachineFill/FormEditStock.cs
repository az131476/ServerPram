using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using NEBasic;

namespace MachineFill
{
    public partial class FormEditStock : Form
    {
        private VOStockList stockList;

        public FormEditStock(VOStockList stockList)
        {
            InitializeComponent();
            this.stockList = stockList;
        }

        private void FormEditStock_Load(object sender, EventArgs e)
        {
            textBox1.Text = stockList.StockID;
            textBox3.Text = stockList.RowNO;
            textBox4.Text = stockList.ColNO;
            textBox7.Text = stockList.StockLength;
            textBox8.Text = stockList.StockWidth;
            textBox9.Text = stockList.StockHeight;
            textBox10.Text = stockList.StockHor;
            textBox11.Text = stockList.StockVer;
            textBox5.Text = stockList.Stock_Hor_correct;
            textBox6.Text = stockList.Stock_Ver_correct;
            comboBox1.SelectedIndex = int.Parse(stockList.State);

            //读取back文件中水平和垂直偏移量
            if (Convert.ToInt32(stockList.ColNO) < 60)
            {
                string readHor1 = NEIni.ReadValue(@"D:\equipment\script\back2.script", "param", "checkOffsetHor_1", "");
                label13.Text = "默认值" + readHor1.Substring(1);
            }
            else 
            {
                label13.Text = "默认值" + NEIni.ReadValue(@"D:\equipment\script\back2.script", "param", "checkOffsetHor_2", "");
            }
            
            label14.Text = "默认值"+NEIni.ReadValue(@"D:\equipment\script\back2.script", "param", "checkOffsetVer_1", "");
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            stockList.StockLength = textBox7.Text;
            stockList.StockWidth = textBox8.Text;
            stockList.StockHeight = textBox9.Text;
            stockList.StockHor = textBox10.Text;
            stockList.StockVer = textBox11.Text;
            if (!textBox5.Text.Equals(""))
            {
                //try
                //{
                //    if (Convert.ToInt32(textBox5.Text) > Convert.ToInt32(label13.Text.Substring(3,2)))
                //    {
                //        if ((Convert.ToInt32(textBox5.Text) - Convert.ToInt32(label13.Text.Substring(3,2))) < Convert.ToInt32(textBox8.Text))
                //        {
                            stockList.Stock_Hor_correct = textBox5.Text.ToString();
                //        }
                //        else
                //        {
                //            MessageBox.Show("水平偏移超出限制，请重新设置");
                //            return;
                //        }
                //    }
                //}catch(Exception ex)
                //{
                //    if (MessageBox.Show(ex.Message+"请重新输入水平偏移!", "Error", MessageBoxButtons.OK) == DialogResult.OK)
                //    {
                //        return;
                //    }
                //}
            }
            else {
                stockList.Stock_Hor_correct = "0";

            }
            if (!textBox6.Text.Equals(""))
            {
                
                //try
                //{
                //    if (Convert.ToInt32(textBox6.Text) < Convert.ToInt32(textBox11.Text))
                //    {
                        stockList.Stock_Ver_correct = textBox6.Text.ToString();
                //    }
                //    else
                //    {
                //        MessageBox.Show("垂直偏移超出限制，请重新设置");
                //        return;
                //    }
                //}
                //catch (Exception ex)
                //{
                //    if (MessageBox.Show(ex.Message + "请重新输入垂直偏移!", "Error", MessageBoxButtons.OK) == DialogResult.OK)
                //    {
                //        return;
                //    }
                //}
            }
            else 
            {
                stockList.Stock_Ver_correct = "0";
            }
            stockList.State = comboBox1.SelectedIndex + "";

            if (new ChannelEdit().updateStockList(stockList))
            {
                MessageBox.Show("保存成功！");
            } 
            else
            {
                MessageBox.Show("保存失败！");
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
