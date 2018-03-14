using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using MySql.Data.MySqlClient;

namespace OrderPrint
{
    public partial class FormView2 : Form
    {
       // private GridppReport Report = new GridppReport();

        public FormView2()
        {
            InitializeComponent();
        }

        private void FormView2_Load(object sender, EventArgs e)
        {
            this.WindowState = FormWindowState.Maximized;

            this.listView1.GridLines = true; //��ʾ�����
            this.listView1.View = View.Details;//��ʾ���ϸ��
            this.listView1.FullRowSelect = true;//�Ƿ����ѡ����
            ImageList image = new ImageList();
            image.ImageSize = new Size(1, 25);
            this.listView1.SmallImageList = image;
            //��ӱ�ͷ
            this.listView1.Columns.Add("�������", 200, HorizontalAlignment.Center);
            this.listView1.Columns.Add("���߱��", 200, HorizontalAlignment.Center);
            this.listView1.Columns.Add("����", 180, HorizontalAlignment.Center);
            this.listView1.Columns.Add("������", 250, HorizontalAlignment.Center);
            this.listView1.Columns.Add("����", 250, HorizontalAlignment.Center);
            this.listView1.Columns.Add("���ں�", 120, HorizontalAlignment.Center);

            this.listView2.GridLines = true; //��ʾ�����
            this.listView2.View = View.Details;//��ʾ���ϸ��
            this.listView2.FullRowSelect = true;//�Ƿ����ѡ����
            this.listView2.SmallImageList = image;
            //��ӱ�ͷ
            this.listView2.Columns.Add("���", 70, HorizontalAlignment.Center);
            this.listView2.Columns.Add("ҩƷ����", 200, HorizontalAlignment.Left);
            this.listView2.Columns.Add("ҩƷ����", 250, HorizontalAlignment.Left);
            this.listView2.Columns.Add("���", 200, HorizontalAlignment.Left);
            this.listView2.Columns.Add("����", 200, HorizontalAlignment.Left);
            this.listView2.Columns.Add("����", 100, HorizontalAlignment.Center);
            this.listView2.Columns.Add("��λ", 180, HorizontalAlignment.Center);

            if (!INIOperationClass.INIGetStringValue(".\\config.ini", "params", "printername", "").Equals(""))
            {
          //      Report.Printer.PrinterName = INIOperationClass.INIGetStringValue(".\\config.ini", "params", "printername", "");
            }
         //   Report.LoadFromFile(".\\op.grf");
        }

        private void btnQuery_Click(object sender, EventArgs e)
        {
            QueryOrder(0);
        }

        private void btnQueryAll_Click(object sender, EventArgs e)
        {
            QueryOrder(1);
        }

        private void btnPrintS_Click(object sender, EventArgs e)
        {
            if (listView1.SelectedIndices != null && listView1.SelectedIndices.Count > 0)
            {
                string hzbh = listView1.SelectedItems[0].SubItems[1].Text;
                string cfxh = listView1.SelectedItems[0].SubItems[3].Text;

//                Report.DetailGrid.Recordset.QuerySQL = @"select a.PatientID,a.PatientName,Sex,Age,a.PrescriptionNo,Diagnosis,DeptName,Drug_Name,Drug_Spec,Manufactory,StorageLoc,Quantity,PrescriptionUnit,UseFrequency,UseDosage,UseRoute,Taboo
//                        from prescriptionlist a left join prescriptiondetail b on a.PatientID=b.PatientID and a.PrescriptionNo=b.PrescriptionNo left join drug_list c on b.DrugCode=c.Drug_Code 
//                        where a.ProcCode='" + cfxh + "' order by a.PrescriptionNo";
//                Report.ParameterByName("HZBH").AsString = hzbh;
//                Report.ParameterByName("CFXH").AsString = cfxh;
//                Report.Print(false);
            }
            else
            {
                MessageBox.Show("��ѡ��Ҫ��ӡ�ļ�¼��");
            }
        }

        private void QueryOrder(int flag)
        {
            if (textBox1.Text.Trim().Length == 0)
            {
                MessageBox.Show("������Ҫ�����Ļ������������߱�Ż򴦷��ţ�");
                return;
            }
            while (listView1.Items.Count > 0)
            {
                listView1.Items.RemoveAt(0);
            }
            while (listView2.Items.Count > 0)
            {
                listView2.Items.RemoveAt(0);
            }
            
            string sql = "";
            if (flag == 0)
            {
                sql = "select ProcCode,PatientID,PatientName,PrescriptionNo,PrescriptionDate,FetchWindow from prescriptionlist where PatientID like '%" + textBox1.Text + "%' or PatientName like '%" + textBox1.Text + "%' or PrescriptionNo like '%" + textBox1.Text + "%'";
            } 
            else
            {
                sql = "select ProcCode,PatientID,PatientName,PrescriptionNo,PrescriptionDate,FetchWindow from prescriptionlist_b where PatientID like '%" + textBox1.Text + "%' or PatientName like '%" + textBox1.Text + "%' or PrescriptionNo like '%" + textBox1.Text + "%'";
            }
            
            MySQLPool pool = MySQLPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                MySqlCommand cmd = new MySqlCommand(sql, conn);
                MySqlDataReader dr = cmd.ExecuteReader();
                while (dr.Read())
                {
                    ListViewItem[] p = new ListViewItem[1];
                    p[0] = new ListViewItem(new string[] { dr.IsDBNull(0) ? "" : dr.GetString(0), dr.IsDBNull(1) ? "" : dr.GetString(1), dr.IsDBNull(2) ? "" : dr.GetString(2), dr.IsDBNull(3) ? "" : dr.GetString(3), dr.IsDBNull(4) ? "" : dr.GetString(4), dr.IsDBNull(5) ? "" : dr.GetString(5) });
                    this.listView1.Items.AddRange(p);
                }
                dr.Close();
            }
            catch (System.Exception ex)
            {
            	//
            }
            finally
            {
                pool.releaseConnection(conn);
            }
        }

        private void listView1_Click(object sender, EventArgs e)
        {
            while (listView2.Items.Count > 0)
            {
                listView2.Items.RemoveAt(0);
            }

            if (listView1.SelectedIndices != null && listView1.SelectedIndices.Count > 0)
            {
                string hzbh = listView1.SelectedItems[0].SubItems[1].Text;
                string cfxh = listView1.SelectedItems[0].SubItems[3].Text;

                string sql = "select a.DrugCode,Drug_Name,Drug_Spec,Manufactory,concat(Quantity,ifnull(PrescriptionUnit,'')) as qty,StorageLoc from prescriptiondetail a left join drug_list b on a.DrugCode=b.drug_Code where patientid='" + hzbh + "' and prescriptionno='" + cfxh + "'";
                
                MySQLPool pool = MySQLPool.getInstance();
                MySqlConnection conn = null;
                try
                {
                    conn = pool.getConnection();
                    MySqlCommand cmd = new MySqlCommand(sql, conn);
                    MySqlDataReader dr = cmd.ExecuteReader();
                    while (dr.Read())
                    {
                        ListViewItem[] p = new ListViewItem[1];
                        p[0] = new ListViewItem(new string[] { "", dr.IsDBNull(0) ? "" : dr.GetString(0), dr.IsDBNull(1) ? "" : dr.GetString(1), dr.IsDBNull(2) ? "" : dr.GetString(2), dr.IsDBNull(3) ? "" : dr.GetString(3), dr.IsDBNull(4) ? "" : dr.GetString(4), dr.IsDBNull(5) ? "" : dr.GetString(5) });
                        this.listView2.Items.AddRange(p);
                    }
                    dr.Close();
                }
                catch (System.Exception ex)
                {
                	//
                }
                finally
                {
                    pool.releaseConnection(conn);
                }
            }
        }
    }
}