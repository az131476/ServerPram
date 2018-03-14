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
    public partial class FormView3 : Form
    {
        public FormView3()
        {
            InitializeComponent();
        }

        private void FormView3_Load(object sender, EventArgs e)
        {
            this.WindowState = FormWindowState.Maximized;

            ImageList image = new ImageList();
            image.ImageSize = new Size(1, 30);

            this.listView2.GridLines = true; //��ʾ�����
            this.listView2.View = View.Details;//��ʾ���ϸ��
            this.listView2.FullRowSelect = true;//�Ƿ����ѡ����
            this.listView2.SmallImageList = image;
            //��ӱ�ͷ
            this.listView2.Columns.Add("ҩƷ����", 200, HorizontalAlignment.Center);
            this.listView2.Columns.Add("ҩƷ����", 300, HorizontalAlignment.Center);
            this.listView2.Columns.Add("ҩƷ���", 300, HorizontalAlignment.Center);
            this.listView2.Columns.Add("����", 250, HorizontalAlignment.Center);
            this.listView2.Columns.Add("����", 150, HorizontalAlignment.Center);

            this.listView1.GridLines = true; //��ʾ�����
            this.listView1.View = View.Details;//��ʾ���ϸ��
            this.listView1.FullRowSelect = true;//�Ƿ����ѡ����
            this.listView1.SmallImageList = image;
            //��ӱ�ͷ
            this.listView1.Columns.Add("ҩƷ����", 200, HorizontalAlignment.Center);
            this.listView1.Columns.Add("ҩƷ����", 300, HorizontalAlignment.Center);
            this.listView1.Columns.Add("ҩƷ���", 300, HorizontalAlignment.Center);
            this.listView1.Columns.Add("����", 400, HorizontalAlignment.Center);
        }

        // ����ҩƷ
        private void button1_Click(object sender, EventArgs e)
        {
            if (textBox1.Text.Trim().Length == 0)
            {
                MessageBox.Show("������Ҫ������ҩƷ���롢ҩƷ�����ҩƷ���ƣ�");
                return;
            }
            while (listView1.Items.Count > 0)
            {
                listView1.Items.RemoveAt(0);
            }

            string sql = sql = "select drug_Code,Drug_Name,Drug_Spec,Manufactory from drug_list where drug_name like '%" + textBox1.Text + "%' or short_code like '%" + textBox1.Text + "%' or drug_barcode like '%" + textBox1.Text + "%' limit 0,50";

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
                    p[0] = new ListViewItem(new string[] { dr.IsDBNull(0) ? "" : dr.GetString(0), dr.IsDBNull(1) ? "" : dr.GetString(1), dr.IsDBNull(2) ? "" : dr.GetString(2), dr.IsDBNull(3) ? "" : dr.GetString(3)});
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

        // ����
        private void button2_Click(object sender, EventArgs e)
        {
            if (listView1.SelectedIndices != null && listView1.SelectedIndices.Count > 0)
            {
                FormNum f = new FormNum();
                f.ShowDialog();

                ListViewItem[] p = new ListViewItem[1];
                p[0] = new ListViewItem(new string[] { listView1.SelectedItems[0].SubItems[0].Text, listView1.SelectedItems[0].SubItems[1].Text, listView1.SelectedItems[0].SubItems[2].Text, listView1.SelectedItems[0].SubItems[3].Text, f.quantity });
                this.listView2.Items.AddRange(p);
            }
            else
            {
                MessageBox.Show("��ѡ��Ҫ�����ֹ���ҩ��ҩƷ��¼��");
            }
        }

        // ȡ��
        private void button4_Click(object sender, EventArgs e)
        {
            if (listView2.SelectedIndices != null && listView2.SelectedIndices.Count > 0)
            {
                this.listView2.SelectedItems[0].Remove();
            }
            else
            {
                MessageBox.Show("��ѡ��Ҫȡ���ֹ���ҩ��ҩƷ��¼��");
            }
        }

        // ������ϸ
        private void button3_Click(object sender, EventArgs e)
        {
            FormWindow f = new FormWindow();
            f.ShowDialog();

            string PrescriptionNo = DateTime.Now.ToString("OyyyyMMddHHmmss");

            MySQLPool pool = MySQLPool.getInstance();
            MySqlConnection conn = null;
            try
            {
                conn = pool.getConnection();
                string sql = "";
                for (int i = 0; i < listView2.Items.Count;i++ )
                {
                    sql = "insert into prescriptiondetail(PrescriptionNo,PatientID,PatientName,DrugCode,Quantity) values('" + PrescriptionNo + "','" + PrescriptionNo + "','����','" + listView2.Items[i].SubItems[0].Text + "','" + listView2.Items[i].SubItems[4].Text + "')";
                    new MySqlCommand(sql, conn).ExecuteNonQuery();
                }

                sql = "insert into prescriptionlist(PrescriptionNo,PatientID,PatientName,PrescriptionDate,FetchWindow) values('" + PrescriptionNo + "','" + PrescriptionNo + "','����',now(),'" + f.window + "')";
                new MySqlCommand(sql, conn).ExecuteNonQuery();

                listView1.Items.Clear();
                listView2.Items.Clear();
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