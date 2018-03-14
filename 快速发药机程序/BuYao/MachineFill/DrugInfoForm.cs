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
    public partial class DrugInfoForm : Form
    {
        public VODrugInfo drugInfoVO;

        public DrugInfoForm(VODrugInfo drugInfo)
        {
            InitializeComponent();
            this.drugInfoVO = drugInfo;
        }

        private void DrugInfoForm_Load(object sender, EventArgs e)
        {
            textBox1.Text = drugInfoVO.DrugCode;
            textBox2.Text = drugInfoVO.DrugName;
            textBox3.Text = drugInfoVO.DrugSpec;
            textBox4.Text = drugInfoVO.Manufactory;
            textBox5.Text = drugInfoVO.PyCode;
            textBox6.Text = drugInfoVO.DrugBarCode;
            textBox7.Text = drugInfoVO.PackageLength;
            textBox8.Text = drugInfoVO.PackageWidth;
            textBox9.Text = drugInfoVO.PackageHeight;
            textBox10.Text = drugInfoVO.OutLimit;
            textBox11.Text = drugInfoVO.StockHoldquantity;
            textBox12.Text = drugInfoVO.StockLimit;
            textBox13.Text = drugInfoVO.AlarmStock;
            textBox14.Text = drugInfoVO.AlarmStock2;
            textBox15.Text = drugInfoVO.StorageLoc;
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            drugInfoVO.PyCode = textBox5.Text;
            drugInfoVO.DrugBarCode = textBox6.Text;
            drugInfoVO.PackageLength = textBox7.Text;
            drugInfoVO.PackageWidth = textBox8.Text;
            drugInfoVO.PackageHeight = textBox9.Text;
            drugInfoVO.OutLimit = textBox10.Text;
            drugInfoVO.StockHoldquantity = textBox11.Text;
            drugInfoVO.StockLimit = textBox12.Text;
            drugInfoVO.AlarmStock = textBox13.Text.ToUpper();
            drugInfoVO.AlarmStock2 = textBox14.Text.ToUpper();
            drugInfoVO.StorageLoc = textBox15.Text;
            
            // 类型校验

            // 保存至数据库

            if (new DrugEdit().saveDrugInfo(drugInfoVO))
            {
                // 保存成功，关闭对话框
                this.DialogResult = DialogResult.OK;
                this.Close();
            } 
            else
            {
                MessageBox.Show("药品信息保存失败！");
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

    }
}
