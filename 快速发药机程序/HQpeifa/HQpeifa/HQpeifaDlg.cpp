// HQpeifaDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "HQpeifa.h"
#include "HQpeifaDlg.h"
#include "afxmt.h"
#include <time.h>
CCriticalSection g_clsWriteLog;
CCriticalSection g_clsLock;
#ifdef _DEBUG
#define new DEBUG_NEW
#endif


CString G_LIGHT_A[7][5] = {{"","","","",""},
						{"","01050020FF00","01050021FF00","01050022FF00","01050023FF00"},
						{"","01050028FF00","01050029FF00","0105002AFF00","0105002BFF00"},
						{"","02050020FF00","02050021FF00","02050022FF00","02050023FF00"},
						{"","02050028FF00","02050029FF00","0205002AFF00","0205002BFF00"},
						{"","03050020FF00","03050021FF00","03050022FF00","03050023FF00"},
						{"","03050028FF00","03050029FF00","0305002AFF00","0305002BFF00"}};
CString G_CLOSE_A[7][5] = {{"","","","",""},
						{"","010500200000","010500210000","010500220000","010500230000"},
						{"","010500280000","010500290000","0105002A0000","0105002B0000"},
						{"","020500200000","020500210000","020500220000","020500230000"},
						{"","020500280000","020500290000","0205002A0000","0205002B0000"},
						{"","030500200000","030500210000","030500220000","030500230000"},
						{"","030500280000","030500290000","0305002A0000","0305002B0000"}};
int G_LIGHT_STATE_A[7][5] = {{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};

CString G_LIGHT_B[7][5] = {{"","","","",""},
						{"","01050024FF00","01050025FF00","01050026FF00","01050027FF00"},
						{"","0105002CFF00","0105002DFF00","0105002EFF00","0105002FFF00"},
						{"","02050024FF00","02050025FF00","02050026FF00","02050027FF00"},
						{"","0205002CFF00","0205002DFF00","0205002EFF00","0205002FFF00"},
						{"","03050024FF00","03050025FF00","03050026FF00","03050027FF00"},
						{"","0305002CFF00","0305002DFF00","0305002EFF00","0305002FFF00"}};
CString G_CLOSE_B[7][5] = {{"","","","",""},
						{"","010500240000","010500250000","010500260000","010500270000"},
						{"","0105002C0000","0105002D0000","0105002E0000","0105002F0000"},
						{"","020500240000","020500250000","020500260000","020500270000"},
						{"","0205002C0000","0205002D0000","0205002E0000","0205002F0000"},
						{"","030500240000","030500250000","030500260000","030500270000"},
						{"","0305002C0000","0305002D0000","0305002E0000","0305002F0000"}};
int G_LIGHT_STATE_B[7][5] = {{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};

// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// 对话框数据
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
END_MESSAGE_MAP()


// CHQpeifaDlg 对话框
CHQpeifaDlg::CHQpeifaDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CHQpeifaDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CHQpeifaDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_MSCOMM1, mscomm1);
	DDX_Control(pDX, IDC_MSCOMM2, mscomm2);
	DDX_Control(pDX, IDC_LIST1, listbox);
	DDX_Control(pDX, IDC_LIST2, listCtrl);
}

BEGIN_MESSAGE_MAP(CHQpeifaDlg, CDialog)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_MESSAGE(WM_USER_TRAY_NOTIFICATION, OnTrayNotification)
	//}}AFX_MSG_MAP
	ON_COMMAND(ID_MENU_EXIT, &CHQpeifaDlg::OnMenuExit)
	ON_WM_TIMER()
	ON_BN_CLICKED(IDC_BUTTON1, &CHQpeifaDlg::OnBnClickedButton1)
	ON_BN_CLICKED(IDC_BUTTON2, &CHQpeifaDlg::OnBnClickedButton2)
	ON_NOTIFY(NM_DBLCLK, IDC_LIST2, &CHQpeifaDlg::OnNMDblclkList2)
	ON_BN_CLICKED(IDC_BUTTON3, &CHQpeifaDlg::OnBnClickedButton3)
	ON_MESSAGE(WM_TASKBARNOTIFIERCLICKED,OnTaskbarNotifierClicked)
	ON_NOTIFY(NM_RCLICK, IDC_LIST2, &CHQpeifaDlg::OnNMRclickList2)
END_MESSAGE_MAP()

void LogWrite(CString s)
{
	g_clsWriteLog.Lock();

	if(s.Trim().GetLength()>0)
	{
		CTime t_now1 = CTime::GetCurrentTime();
		CString temp_time1 = t_now1.Format("%Y-%m-%d %H:%M:%S");
		CString filename = ".\\log\\log_" + t_now1.Format("%Y%m%d") + ".txt";
		FILE* file;
		file = fopen(filename, "a+");
		//	fflush(file);
		if (file!=NULL)
		{
			s = temp_time1 + " " + s;
			_ftprintf(file, _T("%s"), s);
			_ftprintf(file, _T("%s"), "\n");
			//	fflush(file);
			fclose(file);
		}
		else
		{
			//	CString str;
			//	int i = GetLastError();
			//	str.Format("%d",i);
			//	AfxMessageBox(str+"打开失败"+filename);
		}
	}

	g_clsWriteLog.Unlock();
}

CString GetCRC(CString code)
{
	CString result,tem;
	int h,len=code.GetLength();
	int crc = 0;
	for(int i=0;i<len;i++)
	{
		char b = code.GetAt(i);
		crc += (int)b;
		tem.Format("%2X",b);
		result=result+tem;   
	}
	result.Format("%02X", crc);
	return result.Right(2);
}

const unsigned char m_auchCRCHi[]=
{
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,
	0x00,0xC1,0x81,0x40,0x00,0xC1,0x81,0x40,0x01,0xC0,
	0x80,0x41,0x01,0xC0,0x80,0x41,0x00,0xC1,0x81,0x40,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x00,0xC1,
	0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,0x80,0x41,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x00,0xC1,
	0x81,0x40,0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x00,0xC1,0x81,0x40,
	0x01,0xC0,0x80,0x41,0x01,0xC0,0x80,0x41,0x00,0xC1,
	0x81,0x40,0x01,0xC0,0x80,0x41,0x00,0xC1,0x81,0x40,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x00,0xC1,0x81,0x40,
	0x01,0xC0,0x80,0x41,0x00,0xC1,0x81,0x40,0x01,0xC0,
	0x80,0x41,0x01,0xC0,0x80,0x41,0x00,0xC1,0x81,0x40,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,
	0x00,0xC1,0x81,0x40,0x00,0xC1,0x81,0x40,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,
	0x01,0xC0,0x80,0x41,0x00,0xC1,0x81,0x40,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40,0x00,0xC1,0x81,0x40,
	0x01,0xC0,0x80,0x41,0x01,0xC0,0x80,0x41,0x00,0xC1,
	0x81,0x40,0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,
	0x00,0xC1,0x81,0x40,0x01,0xC0,0x80,0x41,0x01,0xC0,
	0x80,0x41,0x00,0xC1,0x81,0x40 
};

const unsigned char m_auchCRCLo[]=
{
	0x00,0xC0,0xC1,0x01,0xC3,0x03,0x02,0xC2,0xC6,0x06,
	0x07,0xC7,0x05,0xC5,0xC4,0x04,0xCC,0x0C,0x0D,0xCD,
	0x0F,0xCF,0xCE,0x0E,0x0A,0xCA,0xCB,0x0B,0xC9,0x09,
	0x08,0xC8,0xD8,0x18,0x19,0xD9,0x1B,0xDB,0xDA,0x1A,
	0x1E,0xDE,0xDF,0x1F,0xDD,0x1D,0x1C,0xDC,0x14,0xD4,
	0xD5,0x15,0xD7,0x17,0x16,0xD6,0xD2,0x12,0x13,0xD3,
	0x11,0xD1,0xD0,0x10,0xF0,0x30,0x31,0xF1,0x33,0xF3,
	0xF2,0x32,0x36,0xF6,0xF7,0x37,0xF5,0x35,0x34,0xF4,
	0x3C,0xFC,0xFD,0x3D,0xFF,0x3F,0x3E,0xFE,0xFA,0x3A,
	0x3B,0xFB,0x39,0xF9,0xF8,0x38,0x28,0xE8,0xE9,0x29,
	0xEB,0x2B,0x2A,0xEA,0xEE,0x2E,0x2F,0xEF,0x2D,0xED,
	0xEC,0x2C,0xE4,0x24,0x25,0xE5,0x27,0xE7,0xE6,0x26,
	0x22,0xE2,0xE3,0x23,0xE1,0x21,0x20,0xE0,0xA0,0x60,
	0x61,0xA1,0x63,0xA3,0xA2,0x62,0x66,0xA6,0xA7,0x67,
	0xA5,0x65,0x64,0xA4,0x6C,0xAC,0xAD,0x6D,0xAF,0x6F,
	0x6E,0xAE,0xAA,0x6A,0x6B,0xAB,0x69,0xA9,0xA8,0x68,
	0x78,0xB8,0xB9,0x79,0xBB,0x7B,0x7A,0xBA,0xBE,0x7E,
	0x7F,0xBF,0x7D,0xBD,0xBC,0x7C,0xB4,0x74,0x75,0xB5,
	0x77,0xB7,0xB6,0x76,0x72,0xB2,0xB3,0x73,0xB1,0x71,
	0x70,0xB0,0x50,0x90,0x91,0x51,0x93,0x53,0x52,0x92,
	0x96,0x56,0x57,0x97,0x55,0x95,0x94,0x54,0x9C,0x5C,
	0x5D,0x9D,0x5F,0x9F,0x9E,0x5E,0x5A,0x9A,0x9B,0x5B,
	0x99,0x59,0x58,0x98,0x88,0x48,0x49,0x89,0x4B,0x8B,
	0x8A,0x4A,0x4E,0x8E,0x8F,0x4F,0x8D,0x4D,0x4C,0x8C,
	0x44,0x84,0x85,0x45,0x87,0x47,0x46,0x86,0x82,0x42,
	0x43,0x83,0x41,0x81,0x80,0x40 
};

unsigned short CalcCrcFast(unsigned char*puchMsg,unsigned short usDataLen)
{
	unsigned char uchCRCHi=0xFF ;
	unsigned char uchCRCLo=0xFF ;
	unsigned short uIndex ;

	while(usDataLen--)
	{
		uIndex=uchCRCHi^*puchMsg++;
		uchCRCHi=uchCRCLo^m_auchCRCHi[uIndex];
		uchCRCLo=m_auchCRCLo[uIndex];
	}
	return(uchCRCHi<<8|uchCRCLo); 
}

CString crcCode(CString code)
{
	CString data;
	char *str=(LPSTR)(LPCTSTR)code;
	char  lin[2];  //临时用来装字符的数组，
	unsigned char buffer[256];
	memset(buffer, 0x00, 255);
	int i;
	for(i=0;i<6;i++)
	{
		lin[0] = str[i*2];
		lin[1] = str[i*2+1];//这两条我也不解释了，一看就明白
		sscanf(lin,"%x",&buffer[i]);//这条语句是整个程序的核心，sscanf和sprintf很类似，不明白看书去。
	}

	unsigned int nb = CalcCrcFast(buffer, 6);
	data.Format("%x", nb);

	while (data.GetLength()<4)
	{
		data = "0" + data; 
	}
	return data.MakeUpper();
}

int hexstrtoint(CString s)
{
	int i = 0;
	sscanf(s, "%x", &i);
	return i;
}

// CHQpeifaDlg 消息处理程序
BOOL CHQpeifaDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// 将“关于...”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// 设置此对话框的图标。当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码
	LogWrite("系统启动");
	LogWrite("清理过期记录文件");

	codeDelay = atoi(config.GetParam("param", "codedelay")==""?"100":config.GetParam("param", "codedelay"));

	OpenMscomm1();

	OpenMscomm2();
	CreateThread(NULL, 0, LightThread2, LPVOID(this), 0, NULL);
	
	m_trayIcon.Create(this, WM_USER_TRAY_NOTIFICATION, "正在运行", m_hIcon, IDR_MENU1);//在这里实现了当鼠标放在托盘上时

	delayTime = atoi(config.GetParam("param", "ycsj"));
	delayTime = delayTime < 3000 ? 3000 : delayTime;

	timespan = atoi(config.GetParam("param", "timespan")) < 5 ? "5" : config.GetParam("param", "timespan");
	
	G_CKH = config.GetParam("param", "ckh");

	ALERT_NEEDOPERCODE = config.GetParam("param", "needopercode");
	ALERT_NOEMPTY = config.GetParam("param", "noempty");
	ALERT_NOORDER = config.GetParam("param", "noorder");
	ALERT_ORDERCANCEL = config.GetParam("param", "ordercancel");
	ALERT_ORDERFINISH = config.GetParam("param", "orderfinish");
	ALERT_WINDOWERROR = config.GetParam("param", "windowerror");

	CString mactype = config.GetParam("param", "machinetype");
	rowCount = atoi(mactype.Mid(0, mactype.Find('*')));
	colCount = atoi(mactype.Mid(mactype.Find('*')+1));
	listCtrl.SetExtendedStyle(listCtrl.GetExtendedStyle()|LVS_EX_GRIDLINES|LVS_EX_FULLROWSELECT);
	listCtrl.SetBkColor(RGB(0xFD, 0xFC, 0xFD));        //设置背景色FDFCFB
	listCtrl.SetRowHeigt(55*3/rowCount);               //设置行高度
	listCtrl.SetHeaderHeight(1.2);          //设置头部高度
	listCtrl.SetHeaderFontHW(16, 0);         //设置头部字体高度,和宽度,0表示缺省，自适应 
	listCtrl.SetHeaderTextColor(RGB(0, 0, 0)); //设置头部字体颜色
	listCtrl.SetTextColor(RGB(0, 0, 0));  //设置文本颜色
	listCtrl.SetHeaderBKColor(0xEA, 0xEE, 0xF1, 0); //设置头部背景色
	listCtrl.SetFontHW(18, 0);			//设置字体高度，和宽度,0表示缺省宽度
	listCtrl.InsertColumn(0, "0", LVCFMT_LEFT, 0);
	int colWidth = 105*5/colCount;
	for (int i=1;i<=colCount;i++)
	{
		CString str;
		str.Format("%d", i);
		listCtrl.InsertColumn(i, str, LVCFMT_CENTER, colWidth);
	}

	mysql_init(&mysql);
	if(!mysql_real_connect(&mysql, config.GetParam("param", "sjkdz"), config.GetParam("param", "sjkyhm"), config.GetParam("param", "sjkmm"),config.GetParam("param", "sjkmc"),3306,NULL,0))
	{
		MessageBox("数据库连接失败");
	}
	mysql_query(&mysql,"SET NAMES 'GBK'");
	

	SetTimer(10, 500, NULL);

	SetTimer(9, 1000, NULL);

	m_wndTaskbarNotifier3.Create(this);
	m_wndTaskbarNotifier3.SetSkin(IDB_SKIN_BOARD,255,0,255);
	m_wndTaskbarNotifier3.SetTextFont("Arial Black",180,TN_TEXT_NORMAL,TN_TEXT_UNDERLINE | TN_TEXT_BOLD);
	m_wndTaskbarNotifier3.SetTextColor(RGB(255,255,255), RGB(200,0,0));
	m_wndTaskbarNotifier3.SetTextRect(CRect(10,20,m_wndTaskbarNotifier3.m_nSkinWidth-10,m_wndTaskbarNotifier3.m_nSkinHeight-20));
	
	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
}

void CHQpeifaDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CHQpeifaDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标显示。
//
HCURSOR CHQpeifaDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

LONG CHQpeifaDlg::OnTrayNotification(WPARAM wparam, LPARAM lparam)
{   
	switch (lparam)
	{
	case WM_RBUTTONDOWN:
		{
			// 用户在托盘图标上单击鼠标右键，弹出上下文菜单隐藏/显示对话框。
			CMenu oMenu;
			if (oMenu.LoadMenu(IDR_MENU1))
			{
				CMenu* pPopup = oMenu.GetSubMenu(0);
				ASSERT(pPopup != NULL);
				CPoint oPoint;
				// 确定鼠标位置以便在该位置附近显示菜单
				GetCursorPos( &oPoint );
				SetForegroundWindow();
				pPopup->TrackPopupMenu(TPM_LEFTALIGN | TPM_RIGHTBUTTON, oPoint.x, oPoint.y, this); 
			}
		}
		break;
		// 单击/双击鼠标左键均显示出对话框
	case WM_LBUTTONDBLCLK:
	case WM_LBUTTONDOWN:
		ShowDialog();
		break;
	}
	return 0;
}

void CHQpeifaDlg::ShowDialog() 
{
	ShowWindow(SW_SHOW);
	SetForegroundWindow();
	GetStockList();
	SetTimer(140, 5000, NULL);
}

void CHQpeifaDlg::OnMenuExit()
{
	// TODO: 在此添加命令处理程序代码
	CDialog::OnCancel();
}

void CHQpeifaDlg::OnOK()
{
	KillTimer(140);
	theApp.HideApplication();
}

void CHQpeifaDlg::OnCancel()
{
	KillTimer(140);
	theApp.HideApplication();
}

BEGIN_EVENTSINK_MAP(CHQpeifaDlg, CDialog)
	ON_EVENT(CHQpeifaDlg, IDC_MSCOMM1, 1, CHQpeifaDlg::OnCommMscomm1, VTS_NONE)
	ON_EVENT(CHQpeifaDlg, IDC_MSCOMM2, 1, CHQpeifaDlg::OnCommMscomm2, VTS_NONE)
END_EVENTSINK_MAP()


void CHQpeifaDlg::OnCommMscomm1()
{
	// TODO: 在此处添加消息处理程序代码
	try
	{
		int resEvent = mscomm1.get_CommEvent();
		if(resEvent == 2)
		{
			Sleep(50);
			VARIANT data;
			data = mscomm1.get_Input();
			CString str(data.bstrVal);
			if (str.Trim() == "")
			{
				return;
			}
			//	listbox.InsertString(0, "接收到条码扫描信息 " + str);
			if (config.GetParam("param", "notework") == "1")
			{
				if (str.Mid(0, 2) == config.GetParam("param", "ghstart"))
				{
					DOC_CODE = str;
					return;
				}
				else if (DOC_CODE == "")
				{
					// 提示刷工号
					// Light(1, 5, 1, 2);
					// SpecialAlert(ALERT_NEEDOPERCODE);
					return;
				}
			}
			str = str.Mid(0, 12);
			LogWrite("分配库位：收到条码信息 "+DOC_CODE+"====="+str);
			DealOrder(DOC_CODE, str);
		//	DOC_CODE = "";
		}
	}
	catch (...)
	{
		LogWrite("未知路径__________________6");
	}
}

CString recvCodeStr = "";
void CHQpeifaDlg::OnCommMscomm2()
{
	// TODO: 在此处添加消息处理程序代码
	VARIANT variant_inp;
	COleSafeArray safearray_inp;
	LONG len,k;
	BYTE rxdata[20];
	CString recd;
	CString strtemp;
	if (mscomm2.get_CommEvent() == 2)
	{
		Sleep(20);
		variant_inp = mscomm2.get_Input();
		safearray_inp = variant_inp;
		len = safearray_inp.GetOneDimSize();
		// 接受数据
		for(k = 0; k < len; k++)
		{
			safearray_inp.GetElement(&k,rxdata + k);
			BYTE bt = *(char*)(rxdata + k);
			strtemp.Format("%02X",bt);
			recd += strtemp;
		}
		recvCodeStr = recd;
	}
}

bool CHQpeifaDlg::OpenMscomm1()
{
	if (FALSE == mscomm1.get_PortOpen())
	{
		try
		{
			mscomm1.put_CommPort(atoi(config.GetParam("param", "com1")));
			mscomm1.put_InBufferSize(1024);
			mscomm1.put_OutBufferSize(1024);
			mscomm1.put_RThreshold(1);
			mscomm1.put_Settings("9600,N,8,1");
			mscomm1.put_PortOpen(TRUE);
			mscomm1.put_InputLen(0);
			mscomm1.get_Input();
		}
		catch (...)
		{
			AfxMessageBox("串口1不存在或已被占用！");
			return FALSE;
		}
	}
	listbox.InsertString(0, "COM1开启成功");
	return TRUE;
}


bool CHQpeifaDlg::OpenMscomm2()
{
	if (FALSE == mscomm2.get_PortOpen())
	{
		try
		{
			if(mscomm2.get_PortOpen())
				mscomm2.put_PortOpen(FALSE);
			mscomm2.put_CommPort(atoi(config.GetParam("param", "com2")));
			mscomm2.put_InputMode(1);
			mscomm2.put_InBufferSize(1024);
			mscomm2.put_OutBufferSize(1024);
			mscomm2.put_Settings(config.GetParam("param", "baudrate")+",N,8,1");
			if(!mscomm2.get_PortOpen())
				mscomm2.put_PortOpen(TRUE);
			mscomm2.put_RThreshold(1);
		}
		catch (...)
		{
			AfxMessageBox("串口2不存在或已被占用！");
			return FALSE;
		}
	}
	listbox.InsertString(0, "COM2开启成功");
	return TRUE;
}

int ilight = 0;
void CHQpeifaDlg::OnTimer(UINT_PTR nIDEvent)
{
	// TODO: 在此添加消息处理程序代码和/或调用默认值
	if (nIDEvent == 10)
	{
		KillTimer(10);
		theApp.HideApplication();
	}
	else if (nIDEvent == 1)
	{
		if (ilight > 59)
		{
			KillTimer(1);
			GetDlgItem(IDC_BUTTON1)->EnableWindow(TRUE);
			return;
		}
		
		CString code;
		if (ilight > 53)
		{
			code.Format("B%d", 90 + (ilight - 53));
		}
		else if (ilight > 47)
		{
			code.Format("A%d", 90 + (ilight - 47));
		}
		else
		{
			int lid = (ilight>23 ? ilight-24 : ilight);
			code.Format("%s%d%d", ilight<24 ? "A" : "B", lid/4+1, ilight%4+1);
		}

		ilight++;
		cycQueue.PushQueue(code);
	}
	else if (nIDEvent == 140)
	{
		GetStockList();
	}
	else if (nIDEvent == 9)
	{
		ReadLight();
	}
	CDialog::OnTimer(nIDEvent);
}

void CHQpeifaDlg::SpecialAlert(CString code)
{
	
}

void CHQpeifaDlg::ReadLight()
{
	try
	{
		g_clsLock.Lock();
		CString maxid = config.GetParam("param", "maxid");
		int lightid = 0;
		CString sql = "select id,LightFlg from dispense_insert where areaid="+G_CKH+" and id>="+ maxid +" order by id asc";
		if(mysql_real_query(&mysql, sql, (UINT)strlen(sql))!=0)//查询成功返回0
		{
			MessageBox("读亮灯出错-1");
		}
		MYSQL_RES *result;
		MYSQL_ROW row;
		if(!(result = mysql_use_result(&mysql)))
		{
			MessageBox("读亮灯出错-2");
		}
		while(row = mysql_fetch_row(result))
		{
			maxid = CString(row[0]);
			lightid = atoi(CString(row[1]));
			if (lightid > 0)
			{
				CString code;
				code.Format("B%02d", lightid);
				cycQueue.PushQueue(code);
			}
		}
		mysql_free_result(result);
		
		config.SetParam("param", "maxid", maxid);

		g_clsLock.Unlock();
	}
	catch (...)
	{
		LogWrite("未知路径__________________R");
	}
}

void CHQpeifaDlg::DealOrder(CString gh, CString pyd)
{	
	try
	{
		listbox.InsertString(0, "准备分配库位，单号："+pyd);
		g_clsLock.Lock();

		// 检索分配库位
		CString patientid = "";
		CString patientname = "";
		CString orderno = "";
		CString lineno = ",";
		int state = 0;
		int cancelstate = 0;

		CString sql = "select PatientID,PatientName,ProcFlg,FetchWindow from prescriptionlist where datediff(ProcDate,now())=0 and ProcCode='"+ pyd +"'";
		if(mysql_real_query(&mysql, sql, (UINT)strlen(sql))!=0)//查询成功返回0
		{
			MessageBox("数据库表格出错");
		}
		MYSQL_RES *result;
		MYSQL_ROW row;
		if(!(result = mysql_use_result(&mysql)))
		{
			MessageBox("读取数据集失败");
		}
		while(row = mysql_fetch_row(result))
		{
			patientid = CString(row[0]);
			patientname = CString(row[1]);

			state = atoi(CString(row[2]));
			lineno = CString(row[3]);
		}
		mysql_free_result(result);

		LogWrite("分配库位：检索到处方信息 "+patientid+"====="+patientname);
		
		if (patientid == "")
		{
			LogWrite("分配库位：异常条码");
			// 非法条码信息
		//	Light(1, 15, 1, 1);
		//	SpecialAlert(ALERT_NOORDER);
			cycQueue.PushQueue("A92");
			return;
		}

		// 已确认
		if (state == 7)
		{
			LogWrite("分配库位：已确认");
			//		listbox.InsertString(0, "已确认");
		//	Light(6, 10, 1, 1);
		//	SpecialAlert(ALERT_ORDERFINISH);
			cycQueue.PushQueue("A93");
			return;
		}

		// 退费了
		if (state == 9)
		{
			LogWrite("分配库位：退费处方");
			//		listbox.InsertString(0, "已退费");
		//	Light(6, 15, 1, 2);
		//	SpecialAlert(ALERT_ORDERCANCEL);
			cycQueue.PushQueue("A94");
			return;
		}

		if (lineno != G_CKH)
		{
			LogWrite("分配库位：窗口错误");
			//		listbox.InsertString(0, "错误窗口");
			// 窗口错误
		//	Light(1, 5, 1, 1);
		//	SpecialAlert(ALERT_WINDOWERROR);
			cycQueue.PushQueue("A95");
			return;
		}

		// 查询是否已经分配了
		int lightid = 0;
		{
			CString sql = "select LightFlg from dispense_list where PatientID='"+patientid+"' and AreaID="+G_CKH;
			if(mysql_real_query(&mysql, sql, (UINT)strlen(sql))!=0)//查询成功返回0
			{
				MessageBox("数据库表格出错");
			}
			MYSQL_RES *result;
			MYSQL_ROW row;
			if(!(result = mysql_use_result(&mysql)))
			{
				MessageBox("读取数据集失败");
			}
			while(row = mysql_fetch_row(result))
			{
				lightid = atoi(CString(row[0]));
			}
			mysql_free_result(result);
		}
		
		CString bid, presc = "";
		// 没有已分配的，需要新分配
		if (lightid == 0)
		{
			{
				sql = "select LightFlg,count(1) ct,min(AreaLevel) p from dispense_list where state=1 and AreaID="+G_CKH+" group by LightFlg order by ct desc,p asc";
				if(mysql_real_query(&mysql, sql, (UINT)strlen(sql))!=0)//查询成功返回0
				{
					MessageBox("数据库表格出错");
				}
				MYSQL_RES *result;
				MYSQL_ROW row;
				if(!(result = mysql_use_result(&mysql)))
				{
					MessageBox("读取数据集失败");
				}
				while(row = mysql_fetch_row(result))
				{
					bid = CString(row[0]);
					presc = CString(row[2]);
					break;
				}
				mysql_free_result(result);
			}
		}
		CString lstr;
		lstr.Format("%d", lightid);
		LogWrite("分配库位：库位分配完毕 "+lstr);

		// 亮灯
		if (lightid > 0)
		{
		//	listbox.InsertString(0, "亮灯，back_id："+stockid);
		//	Light(lightid, lightid, 1, 1);
			CString code;
			code.Format("A%02d", lightid);
			cycQueue.PushQueue(code);
			
		} 
		else
		{
			if (bid == "")
			{
				cycQueue.PushQueue("A96");
				LogWrite("分配库位：无空闲位置");
			} 
			else
			{
				CString sql = "update dispense_list set State=2,UseTime=now(),PatientID='"+patientid+"',PatientName='"+patientname+"' where LightFlg="+bid+" and AreaLevel="+presc+" and AreaID="+G_CKH;
			//	LogWrite(sql);
				if(mysql_query(&mysql, sql)!=0)
				{
					//
				}
				cycQueue.PushQueue("A"+bid);
			}
		}

		g_clsLock.Unlock();
	}
	catch (...)
	{
		LogWrite("未知路径__________________3");
	}
}

void CHQpeifaDlg::OnBnClickedButton1()
{
	// TODO: 在此添加控件通知处理程序代码
	GetDlgItem(IDC_BUTTON1)->EnableWindow(FALSE);
	SetTimer(1, 2000, NULL);
}

DWORD CHQpeifaDlg::CodeParseT(LPVOID lpParameter)
{
	CHQpeifaDlg* dialog = (CHQpeifaDlg*)lpParameter;

	while (TRUE)
	{
		CString str = dialog->queue.PopQueue();
		if (str == "")
		{
			Sleep(500);
			continue;
		}
	}

	return 0;
}

void CHQpeifaDlg::OnBnClickedButton2()
{
	// TODO: 在此添加控件通知处理程序代码
	if (IDNO == MessageBox("是否确认清空全部库位？", "提示", MB_ICONWARNING|MB_YESNO))
	{
		return;
	}

	g_clsLock.Lock();
	
	CString sql = "update dispense_list set State=1,UseTime=null,PatientID=null,PatientName=null where State>0 and AreaID="+G_CKH;

	if(mysql_query(&mysql, sql)!=0)
	{
		//
	}

	g_clsLock.Unlock();
}

void CHQpeifaDlg::GetStockList()
{
	try
	{
		g_clsLock.Lock();

		listCtrl.DeleteAllItems();
		for (int i=0; i< rowCount; i++)
		{
			listCtrl.InsertItem(i, "");
		}

		CString sql = "select RowNo,ColNo,PatientName from dispense_list where AreaID="+G_CKH;
		if(mysql_real_query(&mysql, sql, (UINT)strlen(sql))!=0)//查询成功返回0
		{
			MessageBox("数据库表格出错");
		}
		MYSQL_RES *result;
		MYSQL_ROW row;
		if(!(result = mysql_use_result(&mysql)))
		{
			MessageBox("读取数据集失败");
		}
		int i=0;
		CString str = "";
		while(row = mysql_fetch_row(result))
		{
			int rowno = atoi(CString(row[0]));
			int colno = atoi(CString(row[1]));

			CString posNO = "";
			posNO.Format("%d-", (rowno-1) * rowCount + colno);

			CString patientname = CString(row[2]);
			if (patientname != "")
			{
				patientname = (listCtrl.GetItemText(rowno - 1, colno) == "" ? posNO : (listCtrl.GetItemText(rowno-1, colno) + "\n")) + patientname;
				listCtrl.SetItemText(rowno - 1, colno, patientname);
			}
		}
		mysql_free_result(result);
		g_clsLock.Unlock();
	}
	catch (...)
	{
		LogWrite("未知路径__________________2");
	}
}

void CHQpeifaDlg::OnNMDblclkList2(NMHDR *pNMHDR, LRESULT *pResult)
{
	// TODO: 在此添加控件通知处理程序代码
	LPNMITEMACTIVATE temp = (LPNMITEMACTIVATE) pNMHDR;//将传进来的消息转换成LPNMITEMACTIVAT
	int nItem = temp->iItem;//获得行号
	int nSubItem = temp->iSubItem;//获得列号
	CString row, col;
	row.Format("%d", nItem+1);
	col.Format("%d", nSubItem);
	if (row == "0" || col == "0")
	{
		return;
	}
	CString code;
	code.Format("B%s%s", row, col);
	cycQueue.PushQueue(code);
	LogWrite(code);
	
	listCtrl.SetItemText(atoi(row)-1, atoi(col), "");
	g_clsLock.Lock();
	CString sql = "update dispense_list set State=1,UseTime=null,PatientID=null,PatientName=null where State>0 and AreaID="+G_CKH+" and RowNo="+row+" and ColNo="+col;
	//	adoHisConn.Execute(sql);
	if(mysql_query(&mysql, sql)!=0)
	{
		//
	}
	g_clsLock.Unlock();
}

void CHQpeifaDlg::OnBnClickedButton3()
{
	// TODO: 在此添加控件通知处理程序代码
	theApp.HideApplication();
}

LRESULT CHQpeifaDlg::OnTaskbarNotifierClicked(WPARAM wParam,LPARAM lParam)
{
//	MessageBox("A Taskbar Notifier was clicked!!","Hi",MB_OK);
	return 0;
}

DWORD CHQpeifaDlg::LightThread2(LPVOID lpParamter)
{
	CHQpeifaDlg* h_Dialog = (CHQpeifaDlg*)(lpParamter);

	// 系统启动，全部灭灯
	// 1
	{
		CByteArray OutBuf2;
		COleVariant varOutput2;
		OutBuf2.SetSize(8);

		int iCodeArray[8];
		int checkNum = 0;
		int tmp = 0;
		CString rcode = "010600200000";
		rcode = rcode + crcCode(rcode);
		for (int k = 0; k < rcode.GetLength()/2; k++) 
		{	
			sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
			iCodeArray[k] = tmp;
			checkNum += tmp;
			OutBuf2[k] = tmp;
		}
		varOutput2 = OutBuf2;
		h_Dialog->mscomm2.put_Output(varOutput2);
	}
	Sleep(h_Dialog->codeDelay);
	// 2
	{
		CByteArray OutBuf2;
		COleVariant varOutput2;
		OutBuf2.SetSize(8);

		int iCodeArray[8];
		int checkNum = 0;
		int tmp = 0;
		CString rcode = "020600200000";
		rcode = rcode + crcCode(rcode);
		for (int k = 0; k < rcode.GetLength()/2; k++) 
		{	
			sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
			iCodeArray[k] = tmp;
			checkNum += tmp;
			OutBuf2[k] = tmp;
		}
		varOutput2 = OutBuf2;
		h_Dialog->mscomm2.put_Output(varOutput2);
	}
	Sleep(h_Dialog->codeDelay);
	// 3
	{
		CByteArray OutBuf2;
		COleVariant varOutput2;
		OutBuf2.SetSize(8);

		int iCodeArray[8];
		int checkNum = 0;
		int tmp = 0;
		CString rcode = "030600200000";
		rcode = rcode + crcCode(rcode);
		for (int k = 0; k < rcode.GetLength()/2; k++) 
		{	
			sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
			iCodeArray[k] = tmp;
			checkNum += tmp;
			OutBuf2[k] = tmp;
		}
		varOutput2 = OutBuf2;
		h_Dialog->mscomm2.put_Output(varOutput2);
	}
	Sleep(h_Dialog->codeDelay);
	
	int lightTimer_A = 0;
	int lightTimer_B = 0;
	while (true)
	{
		try
		{
			CString strCode = h_Dialog->cycQueue.PopQueue();
			if (strCode == "")
			{
				Sleep(200);
				if (lightTimer_A > 0)
				{
					lightTimer_A--;
					CString str;
					str.Format("%d",lightTimer_A);
					LogWrite("A--"+str);
					if (lightTimer_A == 0)
					{
						LogWrite("A-----CLOSE");
						// 发送A侧全灭
						for (int i=1;i<7;i++)
						{
							for (int j=1;j<5;j++)
							{
								if (G_LIGHT_STATE_A[i][j]==0)
								{
									continue;
								}
								CByteArray OutBuf2;
								COleVariant varOutput2;
								OutBuf2.SetSize(8);

								int iCodeArray[8];
								int checkNum = 0;
								int tmp = 0;
								CString rcode = G_CLOSE_A[i][j];
								rcode = rcode + crcCode(rcode);
								LogWrite(rcode);
								for (int k = 0; k < rcode.GetLength()/2; k++) 
								{	
									sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
									iCodeArray[k] = tmp;
									checkNum += tmp;
									OutBuf2[k] = tmp;
								}
								varOutput2 = OutBuf2;
								h_Dialog->mscomm2.put_Output(varOutput2);
								Sleep(h_Dialog->codeDelay);
							}
						}
					}
				}
				if (lightTimer_B > 0)
				{
					lightTimer_B--;

					CString str;
					str.Format("%d",lightTimer_B);
					LogWrite("B--"+str);

					if (lightTimer_B == 0)
					{
						LogWrite("B-----CLOSE");
						// 发送B侧全灭
						for (int i=1;i<7;i++)
						{
							for (int j=1;j<5;j++)
							{
								if (G_LIGHT_STATE_B[i][j]==0)
								{
									continue;
								}
								CByteArray OutBuf2;
								COleVariant varOutput2;
								OutBuf2.SetSize(8);

								int iCodeArray[8];
								int checkNum = 0;
								int tmp = 0;
								CString rcode = G_CLOSE_B[i][j];
								rcode = rcode + crcCode(rcode);
								LogWrite(rcode);
								for (int k = 0; k < rcode.GetLength()/2; k++) 
								{	
									sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
									iCodeArray[k] = tmp;
									checkNum += tmp;
									OutBuf2[k] = tmp;
								}
								varOutput2 = OutBuf2;
								h_Dialog->mscomm2.put_Output(varOutput2);
								Sleep(h_Dialog->codeDelay);
							}
						}
					}
				}
			}
			else
			{
				//==============================================
				LogWrite(strCode);
				if (strCode.Mid(0, 1) == "A")
				{
					for (int i=1;i<7;i++)
					{
						for (int j=1;j<5;j++)
						{
							if (G_LIGHT_STATE_A[i][j]==0)
							{
								continue;
							}
							G_LIGHT_STATE_A[i][j] = 0;
							CByteArray OutBuf2;
							COleVariant varOutput2;
							OutBuf2.SetSize(8);

							int iCodeArray[8];
							int checkNum = 0;
							int tmp = 0;
							CString rcode = G_CLOSE_A[i][j];
							rcode = rcode + crcCode(rcode);
							LogWrite("----------->"+rcode);
							for (int k = 0; k < rcode.GetLength()/2; k++) 
							{	
								sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
								iCodeArray[k] = tmp;
								checkNum += tmp;
								OutBuf2[k] = tmp;
							}
							varOutput2 = OutBuf2;
							h_Dialog->mscomm2.put_Output(varOutput2);
							Sleep(h_Dialog->codeDelay);
						}
					}
					// 亮灯
					if (atoi(strCode.Mid(1,2)) > 90)
					{
						for (int j=1;j<5;j++)
						{
							CByteArray OutBuf2;
							COleVariant varOutput2;
							OutBuf2.SetSize(8);

							int iCodeArray[8];
							int checkNum = 0;
							int tmp = 0;
							CString rcode = G_LIGHT_A[atoi(strCode.Mid(1,2))-90][j];
							rcode = rcode + crcCode(rcode);
							G_LIGHT_STATE_A[atoi(strCode.Mid(1,2))-90][j] = 1;
							LogWrite("===========>"+rcode);
							for (int k = 0; k < rcode.GetLength()/2; k++) 
							{	
								sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
								iCodeArray[k] = tmp;
								checkNum += tmp;
								OutBuf2[k] = tmp;
							}
							varOutput2 = OutBuf2;
							h_Dialog->mscomm2.put_Output(varOutput2);
							Sleep(h_Dialog->codeDelay);
						}
					} 
					else
					{
						CByteArray OutBuf2;
						COleVariant varOutput2;
						OutBuf2.SetSize(8);

						int iCodeArray[8];
						int checkNum = 0;
						int tmp = 0;
						CString rcode = G_LIGHT_A[atoi(strCode.Mid(1,1))][atoi(strCode.Mid(2,1))];
						rcode = rcode + crcCode(rcode);
						LogWrite("===========>"+rcode);
						G_LIGHT_STATE_A[atoi(strCode.Mid(1,1))][atoi(strCode.Mid(2,1))] = 1;
						for (int k = 0; k < rcode.GetLength()/2; k++) 
						{	
							sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
							iCodeArray[k] = tmp;
							checkNum += tmp;
							OutBuf2[k] = tmp;
						}
						varOutput2 = OutBuf2;
						h_Dialog->mscomm2.put_Output(varOutput2);
						Sleep(h_Dialog->codeDelay);
					}
					lightTimer_A = 20;
				} 
				else
				{
					for (int i=1;i<7;i++)
					{
						for (int j=1;j<5;j++)
						{
							if (G_LIGHT_STATE_B[i][j]==0)
							{
								continue;
							}
							G_LIGHT_STATE_B[i][j] = 0;
							CByteArray OutBuf2;
							COleVariant varOutput2;
							OutBuf2.SetSize(8);

							int iCodeArray[8];
							int checkNum = 0;
							int tmp = 0;
							CString rcode = G_CLOSE_B[i][j];
							rcode = rcode + crcCode(rcode);
							LogWrite("----------->"+rcode);
							for (int k = 0; k < rcode.GetLength()/2; k++) 
							{	
								sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
								iCodeArray[k] = tmp;
								checkNum += tmp;
								OutBuf2[k] = tmp;
							}
							varOutput2 = OutBuf2;
							h_Dialog->mscomm2.put_Output(varOutput2);
							Sleep(h_Dialog->codeDelay);
						}
					}
					// 亮灯
					if (atoi(strCode.Mid(1,2)) > 90)
					{
						for (int j=1;j<5;j++)
						{
							CByteArray OutBuf2;
							COleVariant varOutput2;
							OutBuf2.SetSize(8);

							int iCodeArray[8];
							int checkNum = 0;
							int tmp = 0;
							CString rcode = G_LIGHT_B[atoi(strCode.Mid(1,2))-90][j];
							rcode = rcode + crcCode(rcode);
							G_LIGHT_STATE_B[atoi(strCode.Mid(1,2))-90][j] = 1;
							LogWrite("===========>"+rcode);
							for (int k = 0; k < rcode.GetLength()/2; k++) 
							{	
								sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
								iCodeArray[k] = tmp;
								checkNum += tmp;
								OutBuf2[k] = tmp;
							}
							varOutput2 = OutBuf2;
							h_Dialog->mscomm2.put_Output(varOutput2);
							Sleep(h_Dialog->codeDelay);
						}
					} 
					else
					{
						CByteArray OutBuf2;
						COleVariant varOutput2;
						OutBuf2.SetSize(8);

						int iCodeArray[8];
						int checkNum = 0;
						int tmp = 0;
						CString rcode = G_LIGHT_B[atoi(strCode.Mid(1,1))][atoi(strCode.Mid(2,1))];
						G_LIGHT_STATE_B[atoi(strCode.Mid(1,1))][atoi(strCode.Mid(2,1))] = 1;
						rcode = rcode + crcCode(rcode);
						LogWrite("===========>"+rcode);
						for (int k = 0; k < rcode.GetLength()/2; k++) 
						{	
							sscanf_s(rcode.Mid(k*2, 2), "%x", &tmp);
							iCodeArray[k] = tmp;
							checkNum += tmp;
							OutBuf2[k] = tmp;
						}
						varOutput2 = OutBuf2;
						h_Dialog->mscomm2.put_Output(varOutput2);
						Sleep(h_Dialog->codeDelay);
					}
					lightTimer_B = 20;
				}
			}
		}
		catch (...)
		{
			LogWrite("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX出错了XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		}
	}

	return 0;
}

void CHQpeifaDlg::OnNMRclickList2(NMHDR *pNMHDR, LRESULT *pResult)
{
	// TODO: 在此添加控件通知处理程序代码
	LPNMITEMACTIVATE temp = (LPNMITEMACTIVATE) pNMHDR;//将传进来的消息转换成LPNMITEMACTIVAT
	int nItem = temp->iItem;//获得行号
	int nSubItem = temp->iSubItem;//获得列号
	CString row, col;
	row.Format("%d", nItem+1);
	col.Format("%d", nSubItem);
	if (row == "0" || col == "0")
	{
		return;
	}
	if (IDYES == MessageBox("是否清空 "+row+"行 "+col+" 列的库位占用记录？", "提示", MB_ICONWARNING|MB_YESNO))
	{
		listCtrl.SetItemText(atoi(row)-1, atoi(col), "");
		g_clsLock.Lock();
		CString sql = "update dispense_list set State=1,UseTime=null,PatientID=null,PatientName=null where State>0 and AreaID="+G_CKH+" and RowNo="+row+" and ColNo="+col;
		//	adoHisConn.Execute(sql);
		if(mysql_query(&mysql, sql)!=0)
		{
			//
		}
		g_clsLock.Unlock();
	}
}
