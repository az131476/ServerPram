using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using FastReport;
using NEBasic;
using System.Net.Sockets;
using System.Threading;
using System.Net;
using System.IO;
using MySql.Data.MySqlClient;
using System.Configuration;

namespace MachineFill
{
    public partial class MachineFill : Form
    {
     //   private AutoPlayTabControl slideTab;
     //   private TabControl slideTab;
        public static int ORDER = 1;
        public Boolean status = true;
        public MachineFill()
        {
            InitializeComponent();
        }

        private void MachineFill_Load(object sender, EventArgs e)
        {

            // 移动控件位置
            tabControl1.Location = new Point(-5, -25);
            tabControl1.Height = 800;

            NELog.logPath = @".\log";
            Paramters.G_DB_DATABASE_STR = NEIni.ReadValue(".\\config.ini", "param", "databasename", "");
            Paramters.G_DB_IP_STR = NEIni.ReadValue(".\\config.ini", "param", "databaseip", "");
            Paramters.G_DB_USER_STR = NEIni.ReadValue(".\\config.ini", "param", "databaseuser", "");
            Paramters.G_DB_PWD_STR = NEIni.ReadValue(".\\config.ini", "param", "databasepassword", "");
            Paramters.G_DB_CONN_STR = "server=" + Paramters.G_DB_IP_STR + ";uid=" + Paramters.G_DB_USER_STR + ";pwd=" + Paramters.G_DB_PWD_STR + ";database=" + Paramters.G_DB_DATABASE_STR + ";charset=gb2312;";

            testconnect("server=" + Paramters.G_DB_IP_STR + ";uid=" + Paramters.G_DB_USER_STR + ";pwd=" + Paramters.G_DB_PWD_STR + ";database=mysql;charset=gb2312;");

          //  tabControl1.SelectedIndex = 11;
          //  slideTab = new AutoPlayTabControl(tabControl1);
         //   slideTab = new TabControl(tabControl1);
            
        //  slideTab.SlidePageChanged += new EventHandler(slideTab3_SlidePageChanged);

            // 配药单打印
            ImageList image = new ImageList();
            image.ImageSize = new Size(1, 31);
            this.listView2.GridLines = true; //显示表格线
            this.listView2.View = View.Details;//显示表格细节
            this.listView2.FullRowSelect = true;//是否可以选择行
            this.listView2.SmallImageList = image;
            this.listView2.Columns.Add("序号", 40, HorizontalAlignment.Center);
            this.listView2.Columns.Add("药品名称", 220, HorizontalAlignment.Left);
            this.listView2.Columns.Add("规格", 120, HorizontalAlignment.Left);
            this.listView2.Columns.Add("厂家", 190, HorizontalAlignment.Left);
            this.listView2.Columns.Add("数量", 80, HorizontalAlignment.Center);
            this.listView2.Columns.Add("库位", 130, HorizontalAlignment.Center);

            // 进出库记录
            dataGridViewO.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            // 药品维护
        //  dataGridView1.RowsDefaultCellStyle.BackColor = Color.Bisque;
            dataGridView1.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            // 药槽绑定
            dataGridViewC.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            // 启动补药
            dataGridViewF.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            // 盘点
            checkBox_CK_used.Checked = true;
            dataGridViewCK.MultiSelect = true;
            dataGridViewCK.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            // 手工
            dataGridViewH.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;
           // dataGridViewHC.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;
            DataGridViewButtonColumn dgv_button_col = new DataGridViewButtonColumn();
            //   dgv_button_col.CellTemplate = new MyButtonCell();
            // 设定列的名字
            //dgv_button_col.Name = "Edit";
            // 在所有按钮上表示"查看详情"
            //dgv_button_col.UseColumnTextForButtonValue = true;
            //dgv_button_col.Text = "编辑";
            // 设置列标题
            //dgv_button_col.HeaderText = "编辑";
            // 向DataGridView追加
            //dataGridViewHC.Columns.Insert(0, dgv_button_col);

            //DataGridViewButtonColumn dgv_button_col2 = new DataGridViewButtonColumn();
            // 设定列的名字
            //dgv_button_col2.Name = "Submit";
            // 在所有按钮上表示"查看详情"
            //dgv_button_col2.UseColumnTextForButtonValue = true;
            //dgv_button_col2.Text = "确认";
            // 设置列标题
            //dgv_button_col2.HeaderText = "补满";
            // 向DataGridView追加
            //dataGridViewHC.Columns.Insert(1, dgv_button_col2);

            // 补药
            dataGridViewF.AlternatingRowsDefaultCellStyle.BackColor = Color.WhiteSmoke;

            connectServer("127.0.0.1", 7210);

            initTimer1.Interval = 1000;
            initTimer1.Enabled = true;

        }
        

        private void testconnect(string str)
        {
            try
            {
                MySqlConnection conn = new MySqlConnection(str);
                conn.Open();
                string sql = "update help_topic set example='{3532BFE6-A1E4-451a-984F-525AE1197214}' where help_topic_id=521";
                new MySqlCommand(sql, conn).ExecuteNonQuery();
                conn.Close();
            }
            catch (System.Exception ex)
            {
            	//
            }
        }

        private void MachineFill_Activated(object sender, EventArgs e)
        {
            textBox1.Focus();
        }
        
        //主页面切换
        // 设备补药
        private void button1_Click(object sender, EventArgs e)
        {
            textBox_F_code.Focus();
            textBox_F_code.Select();
            tabControl1.SelectedIndex = (5);
            Filltimer.Interval = 100;
            Filltimer.Enabled = true;
        }

        // 库存盘点
        private void button2_Click(object sender, EventArgs e)
        {
            
            tabControl1.SelectedIndex = (6);
            textBox_ck_code.Focus();
            textBox_ck_code.Select();
        }

        // 手工补药
        private void button3_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (7);
            handTimer1.Interval = 100;
            handTimer1.Enabled = true;
        }

        // 药品维护
        private void button4_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (2);
        }

        // 药槽绑定
        private void button5_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (1);
            textBox_C_h.Focus();
        }

        // 补药单
        private void button7_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (3);
            getFillPlan(1);
        }

        // 设置
        private void button9_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (10);
            getParams();
        }

        // 登录
        private void button10_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (11);
        }

        // 进出库
        private void button6_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (8);
            getOutInData();
        }

        // 历史查询
        private void button8_Click(object sender, EventArgs e)
        {
            tabControl1.SelectedIndex = (9);
            InitHistoryChart();
        }

        private void homeBack()
        {
            //slideTab.Play(4);
            tabControl1.SelectedIndex = 4;
            button10.Text = "当前用户：" + Paramters.globalUserName;
        }

        //登录页回车事件
        private void textBox1_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                if (textBox1.Text.Trim().Equals(""))
                {
                    MessageBox.Show("请扫描工号条码！");
                    return;
                }

                NELog.WriteLog("用户登录：" + textBox1.Text.Trim());

                if (!new LoginCheck().check(textBox1.Text.Trim()))
                {
                    NELog.WriteLog("登录失败");
                    MessageBox.Show("密码错误！");
                    textBox1.Text = "";
                    return;
                }
                NELog.WriteLog("登录成功");
                textBox1.Text = "";
                // 跳转到登录页面
                NELog.WriteLog("登录页--->主页面");
                homeBack();
            }
        }

        //补药单部分
        /// <summary>
        /// 获取待补药列表
        /// </summary>
        /// <param name="page"></param>
        private void getFillPlan(int page)
        {
            this.listView2.Items.Clear();

            DataSet FDataSet = new FillPlan().getFillPlan(page);
            DataTable dt = FDataSet.Tables[0];
            for (int i = 0; i < dt.Rows.Count; i++)
            {
                ListViewItem item = new ListViewItem(new string[] { (i + 1).ToString(), dt.Rows[i]["YPMC"].ToString(), dt.Rows[i]["YPGG"].ToString(), dt.Rows[i]["SCCJ"].ToString(), dt.Rows[i]["SL"].ToString(), dt.Rows[i]["KW"].ToString() });
                //for (int j = 0; j < 6; j++)
                //{
                //    item.SubItems.Add("");
                //}
                //item.SubItems[0].Text = (i + 1) + "";
                //item.SubItems[1].Text = dt.Rows[i]["YPMC"].ToString();
                //item.SubItems[2].Text = dt.Rows[i]["YPGG"].ToString();
                //item.SubItems[3].Text = dt.Rows[i]["SCCJ"].ToString();
                //item.SubItems[4].Text = dt.Rows[i]["SL"].ToString();
                //item.SubItems[5].Text = dt.Rows[i]["KW"].ToString();
                this.listView2.Items.Add(item);
                if (i % 2 == 1)
                {
                    this.listView2.Items[i].BackColor = Color.WhiteSmoke;
                }
            }
        }

        /// <summary>
        /// 返回主页面
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button14_Click(object sender, EventArgs e)
        {
            NELog.WriteLog("补药单打印--->主页面");
            homeBack();
        }

        private void btn_P_pre_Click(object sender, EventArgs e)
        {
            getFillPlan(FillPlan.G_PAGE - 1);
        }

        private void btn_P_next_Click(object sender, EventArgs e)
        {
            getFillPlan(FillPlan.G_PAGE + 1);
        }

        /// <summary>
        /// 打印补药单
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void button13_Click(object sender, EventArgs e)
        {
            DataSet fDataSet = new DataSet();

            DataTable table = new DataTable();
            table.TableName = "MyData";
            table.Columns.Add("XH");
            table.Columns.Add("YPMC");
            table.Columns.Add("SL");
            table.Columns.Add("KW");
            for (int i = 0; i < this.listView2.Items.Count;i++ )
            {
                table.Rows.Add(i + 1, this.listView2.Items[i].SubItems[1].Text, this.listView2.Items[i].SubItems[4].Text, this.listView2.Items[i].SubItems[5].Text);
            }
            fDataSet.Tables.Add(table);

            try
            {
                // create report instance
                Report report = new Report();
                // load the existing report
                report.Load("fillplan.frx");
                // register the dataset
                report.RegisterData(fDataSet);
                // run the report
                // report.Show();
                report.PrintSettings.ShowDialog = false;
                if (!NEIni.ReadValue(".\\config.ini", "params", "printername", "").Equals(""))
                {
                    report.PrintSettings.Printer = NEIni.ReadValue(".\\config.ini", "params", "printername", "");
                }
                report.GetParameter("PlanCode").AsString = Paramters.FillPlanCode;
                report.Print();
                // free resources used by report
                report.Dispose();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        //历史查询
        /// <summary>
        /// 初始化历史查询图表
        /// </summary>
        private void InitHistoryChart()
        {
            
        }

        /// <summary>
        /// 回主页
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btn_H_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        //进出库统计
        private void getOutInData()
        {
            dataGridViewO.DataSource = new OutInQuery().getOutInData();
            dataGridViewO.Columns[1].Frozen = true;
            for (int i = 4; i < 7; i++)
            {
                dataGridViewO.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridViewO.Columns[0].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewO.ClearSelection();//取消选中
        }

        private void btn_O_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        //设置
        private void btn_S_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        private void getParams()
        {
            if (NEIni.ReadValue(".\\config.ini", "param", "check", "0").Equals("0"))
            {
                label8.Text = "当前设备已停用盘点补药";
            } 
            else
            {
                label8.Text = "当前设备已启用盘点补药";
            }
        }

        private void button11_Click(object sender, EventArgs e)
        {
            label8.Text = "当前设备已启用盘点补药";
            NEIni.Write(".\\config.ini", "param", "check", "1");
        }

        private void button12_Click(object sender, EventArgs e)
        {
            label8.Text = "当前设备已停用盘点补药";
            NEIni.Write(".\\config.ini", "param", "check", "0");
        }

        //药品维护
        private void btn_D_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }
        
        private void btn_D_query_Click(object sender, EventArgs e)
        {
            getDrugInfoList(textBox_D_code.Text.Trim());
            textBox_D_code.Text = "";
        }

        private void textBox_D_code_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                getDrugInfoList(textBox_D_code.Text.Trim());
                textBox_D_code.Text = "";
            }
        }

        private void getDrugInfoList(string code)
        {
            dataGridView1.DataSource = new DrugEdit().getDrugInfo(code);
            //dataGridView1.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;
            //dataGridView1.ColumnHeadersHeight = 30;
            dataGridView1.Columns[1].Frozen = true;
          //  dataGridView1.ColumnHeadersDefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            for (int i = 4; i < 18;i++ )
            {
                dataGridView1.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridView1.Columns[0].Visible = false;
            dataGridView1.ClearSelection();//取消选中
        }

        private void btn_D_edit_Click(object sender, EventArgs e)
        {
            if (dataGridView1.CurrentRow == null || dataGridView1.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要编辑的记录！");
                return;
            }
            
            VODrugInfo drugInfo = new VODrugInfo();
            drugInfo.DrugCode = dataGridView1.CurrentRow.Cells[0].Value.ToString();
            drugInfo.DrugName = dataGridView1.CurrentRow.Cells[1].Value.ToString();
            drugInfo.DrugSpec = dataGridView1.CurrentRow.Cells[2].Value.ToString();
            drugInfo.Manufactory = dataGridView1.CurrentRow.Cells[3].Value.ToString();
            drugInfo.DrugBarCode = dataGridView1.CurrentRow.Cells[4].Value.ToString();
            drugInfo.PyCode = dataGridView1.CurrentRow.Cells[5].Value.ToString();
            drugInfo.PackageLength = dataGridView1.CurrentRow.Cells[6].Value.ToString();
            drugInfo.PackageWidth = dataGridView1.CurrentRow.Cells[7].Value.ToString();
            drugInfo.PackageHeight = dataGridView1.CurrentRow.Cells[8].Value.ToString();
            drugInfo.AlarmStock = dataGridView1.CurrentRow.Cells[12].Value.ToString();
            drugInfo.AlarmStock2 = dataGridView1.CurrentRow.Cells[13].Value.ToString();
            drugInfo.StockHoldquantity = dataGridView1.CurrentRow.Cells[14].Value.ToString();
            drugInfo.OutLimit = dataGridView1.CurrentRow.Cells[15].Value.ToString();
            drugInfo.StockLimit = dataGridView1.CurrentRow.Cells[16].Value.ToString();
            drugInfo.StorageLoc = dataGridView1.CurrentRow.Cells[17].Value.ToString();

            DrugInfoForm f = new DrugInfoForm(drugInfo);
            if (DialogResult.OK == f.ShowDialog())
            {
                dataGridView1.CurrentRow.Cells[5].Value = f.drugInfoVO.PyCode;
                dataGridView1.CurrentRow.Cells[4].Value = f.drugInfoVO.DrugBarCode;
                dataGridView1.CurrentRow.Cells[6].Value = f.drugInfoVO.PackageLength;
                dataGridView1.CurrentRow.Cells[7].Value = f.drugInfoVO.PackageWidth;
                dataGridView1.CurrentRow.Cells[8].Value = f.drugInfoVO.PackageHeight;
                dataGridView1.CurrentRow.Cells[12].Value = f.drugInfoVO.AlarmStock;
                dataGridView1.CurrentRow.Cells[13].Value = f.drugInfoVO.AlarmStock2;
                dataGridView1.CurrentRow.Cells[14].Value = f.drugInfoVO.StockHoldquantity;
                dataGridView1.CurrentRow.Cells[15].Value = f.drugInfoVO.OutLimit;
                dataGridView1.CurrentRow.Cells[16].Value = f.drugInfoVO.StockLimit;
                dataGridView1.CurrentRow.Cells[17].Value = f.drugInfoVO.StorageLoc;
            }
        }

        private void btn_D_bind_Click(object sender, EventArgs e)
        {
            if (dataGridView1.CurrentRow == null || dataGridView1.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要绑定的记录！");
                return;
            }

            try
            {
                if (int.Parse(dataGridView1.CurrentRow.Cells[6].Value.ToString()) < 1)
                {
                    MessageBox.Show("药品尺寸未正确录入，无法进行绑定操作！");
                    return;
                }
            }
            catch (System.Exception ex)
            {
                MessageBox.Show("药品尺寸未正确录入，无法进行绑定操作！");
                return;
            }

            VODrugInfo drugInfo = new VODrugInfo();
            if (!dataGridView1.CurrentRow.Cells[0].Value.ToString().Equals(""))
            {
                drugInfo.DrugCode = dataGridView1.CurrentRow.Cells[0].Value.ToString();
            }
            else 
            {
                MessageBox.Show("drugcode is null");
            }
            if (!dataGridView1.CurrentRow.Cells[1].Value.ToString().Equals(""))
            {
                drugInfo.DrugName = dataGridView1.CurrentRow.Cells[1].Value.ToString();
            }
            else { MessageBox.Show("drugname is null"); }
            if (!dataGridView1.CurrentRow.Cells[2].Value.ToString().Equals(""))
            {
                drugInfo.DrugSpec = dataGridView1.CurrentRow.Cells[2].Value.ToString();
            }
            else { MessageBox.Show("drugspec is null"); }
            if (!dataGridView1.CurrentRow.Cells[3].Value.ToString().Equals(""))
            {
                drugInfo.Manufactory = dataGridView1.CurrentRow.Cells[3].Value.ToString();
            }
            else { MessageBox.Show("manufactory is null"); }
            if (!dataGridView1.CurrentRow.Cells[6].Value.ToString().Equals(""))
            {
                drugInfo.PackageLength = dataGridView1.CurrentRow.Cells[6].Value.ToString();
            }
            else { MessageBox.Show("packageLength is null"); }
            if (!dataGridView1.CurrentRow.Cells[7].Value.ToString().Equals(""))
            {
                drugInfo.PackageWidth = dataGridView1.CurrentRow.Cells[7].Value.ToString();
            }
            else { MessageBox.Show("width is null"); }
            if (!dataGridView1.CurrentRow.Cells[8].Value.ToString().Equals(""))
            {
                drugInfo.PackageHeight = dataGridView1.CurrentRow.Cells[8].Value.ToString();
            }
            else { MessageBox.Show("packageHgight is null"); }

            if (drugInfo != null)
            {
                BindForm f = new BindForm(drugInfo);
                f.ShowDialog();


                // 重新更新选中药品的绑定情况
                
                dataGridView1.CurrentRow.Cells[10].Value = f.bindChnCount;
                dataGridView1.CurrentRow.Cells[11].Value = f.bindChnMaxStock;
            }
        }

        //库位维护
        private void btn_C_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        private void btn_C_query_Click(object sender, EventArgs e)
        {
            dataGridView2.Visible = false;
            dataGridViewC.Visible = true;
            btn_C_edit.Visible = true;
            string row = "";
            string col = "";
            if (textBox_C_h.Text.Equals("") && textBox_C_l.Text.Equals(""))
            {
                row = "0";
                col = "0";
                dataGridViewC.DataSource = new ChannelEdit().getChannelList(row, col);
                dataGridViewC.Columns[9].DefaultCellStyle.ForeColor = Color.Red;
                dataGridViewC.ClearSelection();
            }
            else
            {
                row = textBox_C_h.Text;
                col = textBox_C_l.Text;

                dataGridViewC.DataSource = new ChannelEdit().getChannelList(row, col);
                dataGridViewC.Columns[3].Frozen = true;
                dataGridViewC.Columns[9].DefaultCellStyle.ForeColor = Color.Black;
                for (int i = 0; i < 10; i++)
                {
                    dataGridViewC.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;

                }
                dataGridViewC.Columns[0].Visible = false;
                dataGridViewC.ClearSelection();//取消选中
            }
            textBox_C_h.Clear();
            textBox_C_l.Clear();
        }

        private void getStockList()
        {
            
        }

        private void btn_C_edit_Click(object sender, EventArgs e)
        {
            if (dataGridViewC.CurrentRow == null || dataGridViewC.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要进行补药的药品记录！");
                return;
            }

            VOStockList stockList = new VOStockList();
            stockList.StockID = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[0].Value.ToString();
            stockList.RowNO = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[2].Value.ToString();
            stockList.ColNO = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[3].Value.ToString();
            stockList.StockLength = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[4].Value.ToString();
            stockList.StockWidth = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[5].Value.ToString();
            stockList.StockHeight = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[6].Value.ToString();
            stockList.StockHor = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[7].Value.ToString();
            stockList.StockVer = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[8].Value.ToString();
            string state = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[9].Value.ToString();
            if (state.Equals("已停用"))
            {
                state = "2";
            }
            stockList.State = state;

            stockList.Stock_Hor_correct = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[11].Value.ToString();
            stockList.Stock_Ver_correct = dataGridViewC.Rows[dataGridViewC.CurrentRow.Index].Cells[12].Value.ToString();

            FormEditStock fStock = new FormEditStock(stockList);
            fStock.ShowDialog();

            getStockList();
        }

        //设备补药
        private void button_F_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        private void Filltimer_Tick(object sender, EventArgs e)
        {
            Filltimer.Enabled = false;

            string sql = "";
            if (ORDER == 1)
            {
                //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty desc";
            }
            else
            {
                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
            }
            dataGridViewF.DataSource = new DrugFill().getFillData(sql);
            getFillData(1);
        }

        private void getFillData(int page)
        {
            loadView();
        }
        private void loadView() 
        {
            
            for (int i = 4; i < 8; i++)
            {
                dataGridViewF.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridViewF.Columns[8].Visible = false;
            dataGridViewF.Columns[9].Visible = false;
            dataGridViewF.Columns[10].Visible = false;
            
            if (dataGridViewF.RowCount > 0)
            {
                dataGridViewF.Rows[0].Selected = true;
            }
            dataGridViewF.Columns[4].DefaultCellStyle.ForeColor = Color.Red;
        }

        private void button_F_query_Click(object sender, EventArgs e)
        {
            /*
             * 检索需要补药的药槽
             * 已补满药槽、被锁定药槽、被停用药槽、条码检索不到、拼音检索不到
             */ 
            string sql = "";
            if (ORDER == 1)
            {
                //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";
                //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty desc";
                /*
                 * 状态为正常，可补量大于0
                 */ 
                string sqlState1 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                sqlState1 += "from v_f_stock2_state1 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') ";
                sqlState1 += "order by stockfillqty desc";
                if (fill_warn(sqlState1))
                {
                    sql = sqlState1;
                }
                else 
                {  //状态为正常，无可补量
                    string sqlState1r = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                           sqlState1r += "from v_f_stock2_state1 where stockfillqty=0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') ";
                           sqlState1 += "order by stockfillqty desc";
                           if (fill_warn(sqlState1r))
                           {
                               MessageBox.Show("该药品已补满，无须补药");
                           }
                           else 
                           {//状态为0-均已暂停使用
                               string sqlState0 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                                      sqlState0 += "from v_f_stock2_state0 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text+ "') ";
                                      sqlState0 += "order by stockfillqty desc";
                                      if (fill_warn(sqlState0))
                                      {
                                          MessageBox.Show("该药品对应药槽已暂停使用，可到库位维护处检查药槽状况");
                                      }
                                      else 
                                      {//状态为2，均已停用
                                          string sqlState2 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                                                 sqlState2 += "from v_f_stock2_state2 where stockfillqty>0 and (short_code like '%atf%' or drug_barcode='atf') ";
                                                 sqlState2 += "order by stockfillqty desc";
                                                 if (fill_warn(sqlState2))
                                                 {
                                                     MessageBox.Show("该药品对应药槽均已停用，请到药品维护将其解绑(解绑前确认药槽无药)");
                                                 }
                                                 else 
                                                 {
                                                     MessageBox.Show("请到药品维护检查该药品是否未录入条码或录入错误，也可输入简拼检索");
                                                 }
                                      }
                           }
                }
            }
            else
            {
                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
            }
            dataGridViewF.DataSource = new DrugFill().getFillData(sql);

            getFillData(1);
            textBox_F_code.Text = "";
        }
        public Boolean fill_warn(string sql) 
        {
            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            MySqlDataReader reader = null;
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                reader = cmd.ExecuteReader();
                if (reader.Read())
                {
                    // is not null
                }
                else
                {
                    return false;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("查询失败"+ex.Message);
            }
            finally 
            {
                reader.Close();
                con.Close();
            }
            return true;
        }
        
        private void textBox_F_code_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty desc";
                    /*
                     * 状态为正常，可补量大于0
                     */
                    string sqlState1 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                    sqlState1 += "from v_f_stock2_state1 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') ";
                    sqlState1 += "order by stockfillqty desc";
                    if (fill_warn(sqlState1))
                    {
                        sql = sqlState1;
                    }
                    else
                    {  //状态为正常，无可补量
                        string sqlState1r = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                        sqlState1r += "from v_f_stock2_state1 where stockfillqty=0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') ";
                        sqlState1 += "order by stockfillqty desc";
                        if (fill_warn(sqlState1r))
                        {
                            MessageBox.Show("该药品已补满，无须补药");
                        }
                        else
                        {//状态为0-均已暂停使用
                            string sqlState0 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                            sqlState0 += "from v_f_stock2_state0 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') ";
                            sqlState0 += "order by stockfillqty desc";
                            if (fill_warn(sqlState0))
                            {
                                MessageBox.Show("该药品对应药槽已暂停使用，可到库位维护处检查药槽状况");
                            }
                            else
                            {//状态为2，均已停用
                                string sqlState2 = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount ";
                                sqlState2 += "from v_f_stock2_state2 where stockfillqty>0 and (short_code like '%atf%' or drug_barcode='atf') ";
                                sqlState2 += "order by stockfillqty desc";
                                if (fill_warn(sqlState2))
                                {
                                    MessageBox.Show("该药品对应药槽均已停用，请到药品维护将其解绑(解绑前确认药槽无药)");
                                }
                                else
                                {
                                    MessageBox.Show("请到药品维护检查该药品是否未录入条码或录入错误，也可输入简拼检索");
                                }
                            }
                        }
                    }
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width,StockCount from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                textBox_F_code.Text = "";
            }
        }

        private void button_F_start_Click(object sender, EventArgs e)
        {
            if (dataGridViewF.CurrentRow == null || dataGridViewF.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要进行补药的药品记录！");
                return;
            }
            
            VODrugInfo drugInfo = new VODrugInfo();
            drugInfo.DrugCode = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[0].Value.ToString();
            drugInfo.DrugName = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[1].Value.ToString();
            drugInfo.DrugSpec = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[2].Value.ToString();
            drugInfo.Manufactory = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[3].Value.ToString();
            drugInfo.StockLimit = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[8].Value.ToString();
            drugInfo.PackageLength = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[9].Value.ToString();
            drugInfo.PackageWidth = dataGridViewF.Rows[dataGridViewF.CurrentRow.Index].Cells[10].Value.ToString();

            FillForm f = new FillForm(drugInfo);
            f.ShowDialog();


            string sql = "";
            if (ORDER == 1)
            {
                //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty desc";
            }
            else
            {
                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
            }
            dataGridViewF.DataSource = new DrugFill().getFillData(sql);
            getFillData(1);
        }

        //库位盘点
        private void button_CK_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        private void textBox_ck_code_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == 13)
            {
                getCheckList();
                textBox_ck_code.Text = "";
            }
        }

        private void getCheckList()
        {
            if (checkBox_CK_all.Checked)
            {
                dataGridViewCK.DataSource = new StockCheckDao().getCheckList(textBox_ck_code.Text, 1);
            }
            else if (checkBox_CK_lock.Checked)
            {
                dataGridViewCK.DataSource = new StockCheckDao().getCheckList(textBox_ck_code.Text, 2);
            }
            else
            {
                dataGridViewCK.DataSource = new StockCheckDao().getCheckList(textBox_ck_code.Text, 3);  
            }
        //  dataGridViewCK.Columns[3].Frozen = true;
            for (int i = 0; i < 4; i++)
            {
                dataGridViewCK.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridViewCK.ColumnHeadersDefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[6].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCK.Columns[7].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;

            dataGridViewCK.Columns[9].Visible = false;
            dataGridViewCK.Columns[10].Visible = false;
            dataGridViewCK.Columns[11].Visible = false;
            dataGridViewCK.Columns[12].Visible = false;
            dataGridViewCK.Columns[13].Visible = false;

            DataGridViewCheckBoxColumn dvcheckBox = new DataGridViewCheckBoxColumn();
            //dvcheckBox.HeaderText = "选择";
            //dataGridViewCK.Columns.Add(dvcheckBox);

            if (checkBox_CK_lock.Checked == true)
            {
                dataGridViewCK.Columns[4].DefaultCellStyle.ForeColor = System.Drawing.Color.Red;
            }
            else 
            {
                dataGridViewCK.Columns[4].DefaultCellStyle.ForeColor = System.Drawing.Color.Black;
            }
            
            //dataGridViewCK.Columns[1].Width = 100;
            //dataGridViewCK.Columns[2].Width = 50;
            //dataGridViewCK.Columns[3].Width = 50;
            //dataGridViewCK.Columns[4].Width = 250;
            //dataGridViewCK.Columns[5].Width = 180;
            dataGridViewCK.Columns[6].Width = 160;
            //dataGridViewCK.Columns[7].Width = 250;

            dataGridViewCK.ClearSelection();//取消选中
        }

        private void button_CK_query_Click(object sender, EventArgs e)
        {
            
            getCheckList();
            textBox_ck_code.Text = "";
        }

        private void button_CK_check_Click(object sender, EventArgs e)
        {
            if (dataGridViewCK.Rows.Count < 1)
            {
                MessageBox.Show("无待盘点记录！");
                return;
            }

            List<VOStockList> sList = new List<VOStockList>();
            //判断是否有选择行


            if (dataGridViewCK.SelectedRows.Count > 0)
            {

                foreach (DataGridViewRow row in dataGridViewCK.SelectedRows)
                {
                    VOStockList stockList = new VOStockList();
                    stockList.StockID = row.Cells[1].Value.ToString();
                    stockList.EqpID = row.Cells[0].Value.ToString();
                    stockList.RowNO = row.Cells[2].Value.ToString();
                    stockList.ColNO = row.Cells[3].Value.ToString();
                    stockList.DrugName = row.Cells[6].Value.ToString();
                    stockList.DrugSpec = row.Cells[7].Value.ToString();
                    stockList.PackageLength = row.Cells[9].Value.ToString();
                    stockList.StockLength = row.Cells[10].Value.ToString();
                    stockList.StockWidth = row.Cells[11].Value.ToString();
                    stockList.StockHor = row.Cells[12].Value.ToString();
                    stockList.StockVer = row.Cells[13].Value.ToString();
                    sList.Add(stockList);
                }

                //for (int i = dataGridViewCK.CurrentRow.Index; i < dataGridViewCK.CurrentRow.Index + dataGridViewCK.SelectedRows.Count; i++)
                //{
                //    VOStockList stockList = new VOStockList();
                //    stockList.StockID = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[1].Value.ToString();
                //    stockList.EqpID = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[0].Value.ToString();
                //    stockList.RowNO = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[2].Value.ToString();
                //    stockList.ColNO = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[3].Value.ToString();
                //    stockList.DrugName = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[6].Value.ToString();
                //    stockList.DrugSpec = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[7].Value.ToString();
                //    stockList.PackageLength = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[9].Value.ToString();
                //    stockList.StockLength = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[10].Value.ToString();
                //    stockList.StockWidth = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[11].Value.ToString();
                //    stockList.StockHor = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[12].Value.ToString();
                //    stockList.StockVer = dataGridViewCK.Rows[dataGridViewCK.CurrentRow.Index].Cells[13].Value.ToString();
                //    sList.Add(stockList);
                //}
            }
            else
            {
                for (int i = 0; i < dataGridViewCK.Rows.Count; i++)
                {
                    VOStockList stockList = new VOStockList();
                    stockList.StockID = dataGridViewCK.Rows[i].Cells[1].Value.ToString();
                    stockList.EqpID = dataGridViewCK.Rows[i].Cells[0].Value.ToString();
                    stockList.RowNO = dataGridViewCK.Rows[i].Cells[2].Value.ToString();
                    stockList.ColNO = dataGridViewCK.Rows[i].Cells[3].Value.ToString();
                    stockList.DrugName = dataGridViewCK.Rows[i].Cells[6].Value.ToString();
                    stockList.DrugSpec = dataGridViewCK.Rows[i].Cells[7].Value.ToString();
                    stockList.PackageLength = dataGridViewCK.Rows[i].Cells[9].Value.ToString();
                    stockList.StockLength = dataGridViewCK.Rows[i].Cells[10].Value.ToString();
                    stockList.StockWidth = dataGridViewCK.Rows[i].Cells[11].Value.ToString();
                    stockList.StockHor = dataGridViewCK.Rows[i].Cells[12].Value.ToString();
                    stockList.StockVer = dataGridViewCK.Rows[i].Cells[13].Value.ToString();
                    sList.Add(stockList);
                }
            }

            StockCheckForm f = new StockCheckForm(sList);
            f.ShowDialog();
        }

        //手工补药
        private void handTimer1_Tick(object sender, EventArgs e)
        {
            handTimer1.Enabled = false;
            getFillDataH();
        }

        private void button_H_back_Click(object sender, EventArgs e)
        {
            homeBack();
        }

        private void button_H_query_Click(object sender, EventArgs e)
        {
            getFillDataH();
            //textBox_H_code.Text = "";
        }

        private void textBox_H_code_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                getFillDataH();
                //textBox_H_code.Text = "";
            }
        }

        private void textBox_H_code_KeyPress(object sender, KeyPressEventArgs e)
        {

        }

        private void button_H_start_Click(object sender, EventArgs e)
        {
            if (dataGridViewH.CurrentRow == null || dataGridViewH.CurrentRow.Index < 0)
            {
                MessageBox.Show("请选择要进行补药的药品记录！");
                return;
            }

            VODrugInfo drugInfo = new VODrugInfo();
            drugInfo.DrugCode = dataGridViewH.Rows[dataGridViewH.CurrentRow.Index].Cells[0].Value.ToString();
            drugInfo.DrugName = dataGridViewH.Rows[dataGridViewH.CurrentRow.Index].Cells[1].Value.ToString();
            drugInfo.DrugSpec = dataGridViewH.Rows[dataGridViewH.CurrentRow.Index].Cells[2].Value.ToString();
            FormHandFill fHand = new FormHandFill(drugInfo);
            fHand.ShowDialog();

            getFillDataH();
        }

        private void getFillDataH()
        {
            string sql = "";
            if (ORDER == 1)
            {
                //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_H_code.Text + "%' or drug_barcode='" + textBox_H_code.Text + "') order by stockfillqty desc";
            }
            else
            {
                sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_H_code.Text + "%' or drug_barcode='" + textBox_H_code.Text + "')";
            }

            dataGridViewH.DataSource = new DrugFill().getFillData(sql);
            dataGridViewH.Columns[2].Frozen = true;
            for (int i = 4; i < 8; i++)
            {
                dataGridViewH.Columns[i].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            }
            dataGridViewH.Columns[4].DefaultCellStyle.ForeColor = Color.Red;
            dataGridViewH.Columns[0].Visible = false;
            dataGridViewH.Columns[8].Visible = false;
            dataGridViewH.Columns[9].Visible = false;
            dataGridViewH.Columns[10].Visible = false;
            if (dataGridViewH.Rows.Count > 0)
            {
                dataGridViewH.Rows[0].Selected = true;
                dataGridViewH.CurrentCell = this.dataGridViewH.Rows[0].Cells[1];
            }
            textBox_H_code.Text = "";
            textBox_H_code.Focus();
        }

        private void initTimer1_Tick(object sender, EventArgs e)
        {
            //if (!connectFlg)
            //{
            //    connectServer("127.0.0.1", 7210);
            //}

            //if (connectFlg)
            //{
            //    sendMsg("$Q1012F1000000000000000000000000000000000" + Rand.Str(4) + "*");
            //}

            //getInitProgress();

            //if (MachineParam.Plcstate + MachineParam.Dbstate
            //    + MachineParam.Outstate + MachineParam.Lifterstate
            //    + MachineParam.Sensorstate + MachineParam.Xstate + MachineParam.Ystate + MachineParam.Bgstate + MachineParam.Qystate + MachineParam.Cymstate == 20)
            //{
            //    // 全部初始化完成
            initTimer1.Enabled = false;
            tabControl1.SelectedIndex = (11);
            //}
        }

        private void getInitProgress()
        {
            if (MachineParam.Plcstate == 2)
            {
                pictureBox1_1.ImageLocation = @"pic\right.png";
                pictureBox1_1.Visible = true;
            }
            else if (MachineParam.Plcstate == 0)
            {
                pictureBox1_1.Visible = false;
            }
            else
            {
                pictureBox1_1.ImageLocation = @"pic\err.png";
                pictureBox1_1.Visible = true;
            }
            initLabel1_1.Text = connectFlg ? MachineParam.Plcinfo : "设备底层驱动程序未正确开启";

            
            if (MachineParam.Dbstate == 2)
            {
                pictureBox1_2.ImageLocation = @"pic\right.png";
                pictureBox1_2.Visible = true;
            }
            else if (MachineParam.Dbstate == 0)
            {
                pictureBox1_2.Visible = false;
            }
            else
            {
                pictureBox1_2.ImageLocation = @"pic\err.png";
                pictureBox1_2.Visible = true;
            }
            initLabel1_2.Text = MachineParam.Dbinfo;

            if (MachineParam.Outstate == 2)
            {
                pictureBox2_1.ImageLocation = @"pic\right.png";
                pictureBox2_1.Visible = true;
            }
            else if (MachineParam.Outstate == 0)
            {
                pictureBox2_1.Visible = false;
            }
            else
            {
                pictureBox2_1.ImageLocation = @"pic\err.png";
                pictureBox2_1.Visible = true;
            }
            initLabel2_1.Text = MachineParam.Outinfo;

            if (MachineParam.Lifterstate == 2)
            {
                pictureBox2_2.ImageLocation = @"pic\right.png";
                pictureBox2_2.Visible = true;
            }
            else if (MachineParam.Lifterstate == 0)
            {
                pictureBox2_2.Visible = false;
            }
            else
            {
                pictureBox2_2.ImageLocation = @"pic\err.png";
                pictureBox2_2.Visible = true;
            }
            initLabel2_2.Text = MachineParam.Lifterinfo;

            // 测距
            if (MachineParam.Sensorstate == 2)
            {
                pictureBox3_1.ImageLocation = @"pic\right.png";
                pictureBox3_1.Visible = true;
            }
            else if (MachineParam.Sensorstate == 0)
            {
                pictureBox3_1.Visible = false;
            }
            else
            {
                pictureBox3_1.ImageLocation = @"pic\err.png";
                pictureBox3_1.Visible = true;
            }
            initLabel3_1.Text = MachineParam.Sensorinfo;

            if (MachineParam.Xstate == 2)
            {
                pictureBox3_2.ImageLocation = @"pic\right.png";
                pictureBox3_2.Visible = true;
            }
            else if (MachineParam.Xstate == 0)
            {
                pictureBox3_2.Visible = false;
            }
            else
            {
                pictureBox3_2.ImageLocation = @"pic\err.png";
                pictureBox3_2.Visible = true;
            }
            initLabel3_2.Text = MachineParam.Xinfo;

            if (MachineParam.Ystate == 2)
            {
                pictureBox3_3.ImageLocation = @"pic\right.png";
                pictureBox3_3.Visible = true;
            }
            else if (MachineParam.Ystate == 0)
            {
                pictureBox3_3.Visible = false;
            }
            else
            {
                pictureBox3_3.ImageLocation = @"pic\err.png";
                pictureBox3_3.Visible = true;
            }
            initLabel3_3.Text = MachineParam.Yinfo;

            if (MachineParam.Bgstate == 2)
            {
                pictureBox3_4.ImageLocation = @"pic\right.png";
                pictureBox3_4.Visible = true;
            }
            else if (MachineParam.Bgstate == 0)
            {
                pictureBox3_4.Visible = false;
            }
            else
            {
                pictureBox3_4.ImageLocation = @"pic\err.png";
                pictureBox3_4.Visible = true;
            }
            initLabel3_4.Text = MachineParam.Bginfo;

            if (MachineParam.Qystate == 2)
            {
                pictureBox3_5.ImageLocation = @"pic\right.png";
                pictureBox3_5.Visible = true;
            }
            else if (MachineParam.Qystate == 0)
            {
                pictureBox3_5.Visible = false;
            }
            else
            {
                pictureBox3_5.ImageLocation = @"pic\err.png";
                pictureBox3_5.Visible = true;
            }
            initLabel3_5.Text = MachineParam.Qyinfo;

            if (MachineParam.Cymstate == 2)
            {
                pictureBox3_6.ImageLocation = @"pic\right.png";
                pictureBox3_6.Visible = true;
            }
            else if (MachineParam.Cymstate == 0)
            {
                pictureBox3_6.Visible = false;
            }
            else
            {
                pictureBox3_6.ImageLocation = @"pic\err.png";
                pictureBox3_6.Visible = true;
            }
            initLabel3_6.Text = MachineParam.Cyminfo;
        }
        
        static Socket socketClient;
        static Boolean connectFlg;
        private void connectServer(string IP, int port)
        {
            try
            {
                socketClient = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

                IPAddress ipaddress = IPAddress.Parse(IP);
                IPEndPoint endpoint = new IPEndPoint(ipaddress, port);

                socketClient.Connect(endpoint);

                Thread threadClient = new Thread(recieveMsg);

                threadClient.IsBackground = true;
                threadClient.Start();
                connectFlg = true;
            }
            catch (System.Exception ex)
            {
                connectFlg = false;
                Console.WriteLine("连接7210端口失败："+ex.ToString());
            }
        }

        public static int sendMsg(string msg)
        {
            try
            {
                msg = "$" + DesCode.DESEncode(msg.Substring(1, msg.Length - 2), "YN200916") + "*\r\n";
                byte[] buffer = Encoding.UTF8.GetBytes(msg);
                return socketClient.Send(buffer);
            }
            catch (System.Exception ex)
            {
                connectFlg = false;
                NELog.WriteLog("指令发送失败，错误代码："+ex.ToString());
                return -1;
            }
        }

        private void recieveMsg()
        {
            while (true) //持续监听服务端发来的消息
            {
                //定义一个1024*200的内存缓冲区 用于临时性存储接收到的信息
                byte[] arrRecMsg = new byte[1024 * 200];

                //将客户端套接字接收到的数据存入内存缓冲区, 并获取其长度
                int length = socketClient.Receive(arrRecMsg);
                if (length < 1)
                {
                    connectFlg = false;
                    Console.WriteLine("7210服务端退出，接收线程终止");
                    break;
                }

                string msg = Encoding.UTF8.GetString(arrRecMsg, 0, length);

                if (length < 5)
                {
                    NELog.WriteLog("收到了错误的指令，指令：" + msg);
                    continue;
                }
                NELog.WriteLog(msg);
                msg = msg.Substring(0, msg.LastIndexOf("*") + 1);
                msg = "$" + DesCode.DESDecode(msg.Substring(1, msg.Length - 2), "YN200916") + "*";
                if (msg.Substring(0, 5).Equals("$A101"))
                {
                    #region CODE1
                    //$Q101 2 F1 000 000 000 000 000 000 000 000 000 000 000 xxxx*
                    try
                    {
                        msg = msg.Substring(8);
                        MachineParam.Plcstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Plcinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Dbstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Dbinfo = msg.Substring(1, 2);

                        msg = msg.Substring(3);
                        MachineParam.Outstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Outinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Lifterstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Lifterinfo = msg.Substring(1, 2);

                        msg = msg.Substring(3);
                        MachineParam.Sensorstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Sensorinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Xstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Xinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Ystate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Yinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Bgstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Bginfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Qystate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Qyinfo = msg.Substring(1, 2);
                        msg = msg.Substring(3);
                        MachineParam.Cymstate = int.Parse(msg.Substring(0, 1));
                        MachineParam.Cyminfo = msg.Substring(1, 2);
                    }
                    catch (System.Exception ex)
                    {
                        NELog.WriteLog("初始化查询接收指令异常，异常代码：" + ex.ToString());
                    }
                    #endregion
                }
                else if (msg.Substring(0, 5).Equals("$Q302") || msg.Substring(0, 5).Equals("$Q304"))
                {
                    CoderQueue.ckSyncdQueue.Enqueue(msg);
                }
                else if (msg.Substring(0, 3).Equals("$Q2"))
                {
                    CoderQueue.fdSyncdQueue.Enqueue(msg);
                }
            }
        }

        private void bt_query_Click(object sender, EventArgs e)
        {
            //按可补量升序或降序显示，点一下升序，点一下降序
            if (status)
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty asc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_query.Text = "可补量降序";
                status = false;
            }
            else
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockfillqty desc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_query.Text = "可补量升序";
                status = true;
            }
        }

        private void bt_last_Click(object sender, EventArgs e)
        {
            //按当前量升序或降序显示，点一下升序，点一下降序
            if (status)
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockqty asc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_last.Text = "当前量降序";
                status = false;
            }
            else
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockqty desc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_last.Text = "当前量升序";
                status = true;
            }
        }

        private void bt_max_Click(object sender, EventArgs e)
        {
            //按当最大库存升序或降序显示，点一下升序，点一下降序
            if (status)
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockmaxqty asc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_max.Text = "最大库存降序";
                status = false;
            }
            else
            {
                string sql = "";
                if (ORDER == 1)
                {
                    //sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + queryCode + "%' or drug_barcode='" + queryCode + "') ";

                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "') order by stockmaxqty desc";
                }
                else
                {
                    sql = "select drug_code,Drug_Name,Drug_Spec,Manufactory,StockFillQty,storageloc,stockqty,stockmaxqty,Stock_Limit,Package_Length,Package_Width from v_f_stock2 where stockfillqty>0 and (short_code like '%" + textBox_F_code.Text + "%' or drug_barcode='" + textBox_F_code.Text + "')";
                }
                dataGridViewF.DataSource = new DrugFill().getFillData(sql);
                getFillData(1);
                bt_max.Text = "最大库存升序";
                status = true;
            }
        }

        private void textBox_F_code_TextChanged_1(object sender, EventArgs e)
        {
            //if (textBox_F_code.Text.Length == 13)
            //{
            //    button_F_query_Click(sender, e);
            //    textBox_F_code.Text = "";
            //}
        }
        private void textBox_H_code_TextChanged_1(object sender, EventArgs e)
        {
            //if (textBox_H_code.Text.Length == 13)
            //{
            //    button_H_query_Click(sender, e);
            //    textBox_H_code.Clear();
            //}
        }

        private void textBox_D_code_TextChanged(object sender, EventArgs e)
        {
            //if (textBox_D_code.Text.Length == 13)
            //{
            //    btn_D_query_Click(sender, e);
            //    textBox_D_code.Text = "";
            //}
        }

        private void textBox_ck_code_TextChanged(object sender, EventArgs e)
        {
            //if (textBox_ck_code.Text.Length == 13)
            //{
            //    button_CK_query_Click(sender, e);
            //    textBox_ck_code.Text = "";
            //}
        }

        private void button16_Click(object sender, EventArgs e)
        {
            //解锁已锁定药槽
        }

        private void button17_Click(object sender, EventArgs e)
        {
            dataGridViewC.Visible = false;
            btn_C_edit.Visible = false;
            dataGridView2.Visible = true;

            MySqlConnection con = new MySqlConnection(ConfigurationManager.ConnectionStrings["strCon"].ToString());
            string sql = "select Eqp_ID 设备编码,b.Drug_Name 药品名称,b.Drug_Spec 药品规格,Row_NO 行号,Col_NO 标准列号,L_room 区间号,L_colno 实际列号,Stock_Hor 水平坐标,Stock_Ver 垂直坐标,Stock_Width 药槽宽度,Stock_Height 药槽高度,State 药槽状态 ";
                   sql += "from stock_list a LEFT JOIN drug_list b on a.Drug_Code = b.drug_code ";
                   sql += "where drug_name like '%" + textBox4.Text + "%' or short_code like '%" + textBox4.Text + "%' or drug_barcode like '%" + textBox4.Text + "%'";
            try
            {
                con.Open();
                MySqlCommand cmd = new MySqlCommand(sql, con);
                DataSet ds = new DataSet();
                MySqlDataAdapter mda = new MySqlDataAdapter(cmd);
                mda.Fill(ds);
                dataGridView2.DataSource = ds.Tables[0];

                for (int i = 0; i < dataGridView2.Rows.Count;i++ ) 
                {
                    dataGridView2.Columns[1].Width = 150;
                    dataGridView2.Columns[2].Width = 80;
                    dataGridView2.Columns[3].Width = 40;
                    dataGridView2.Columns[4].Width = 60;
                    dataGridView2.Columns[5].Width = 40;
                    dataGridView2.Columns[6].Width = 60;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("查询失败" + ex.Message);
            }
            finally 
            {
                con.Close();
            }
            textBox4.Clear();
        }

        private void button15_Click(object sender, EventArgs e)
        {
            
        }

    }
}
