// AlertInfo.cpp : 实现文件
//

#include "stdafx.h"
#include "HQpeifa.h"
#include "AlertInfo.h"


// CAlertInfo 对话框

IMPLEMENT_DYNAMIC(CAlertInfo, CDialog)

CAlertInfo::CAlertInfo(CWnd* pParent /*=NULL*/)
	: CDialog(CAlertInfo::IDD, pParent)
{

}

CAlertInfo::~CAlertInfo()
{
}

void CAlertInfo::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_STATIC_L, m_sta_l);
}


BEGIN_MESSAGE_MAP(CAlertInfo, CDialog)
	ON_WM_PAINT()
	ON_WM_TIMER()
END_MESSAGE_MAP()


// CAlertInfo 消息处理程序
BOOL CAlertInfo::OnInitDialog()
{
	CDialog::OnInitDialog();

	// TODO: 在此添加额外的初始化代码
	cursorC = FALSE;
	hCurA = LoadCursor( NULL , IDC_ARROW ) ;
	hCurH = LoadCursor( NULL , IDC_HAND ) ;

	int showtime = 5000;
	/*LPSTR lpCmdLine = GetCommandLine(); 
	CString get_str;
	get_str = lpCmdLine;
	if (get_str.Find("/m") > -1)
	{
		CString tmpStr = "";
		tmpStr = get_str.Mid(get_str.Find("/m") + 3);
		get_str = tmpStr.Mid(0, tmpStr.Find("/t"));
		showtime = atoi(tmpStr.Mid(tmpStr.Find("/t") + 3));
	}
	else
	{
		get_str = "显示测试";
	}*/

	if (showtime < 5000)
	{
		showtime = 5000;
	}

	m_sta_l.SetTextColor(RGB(255,0,0))
		.SetFontSize(40)
		.SetBkColor(RGB(244,250,254))
		.SetText(info);

	short	shBtnColor = 255;
	/*m_btnset.SetIcon(IDI_ICON1);
	m_btnset.OffsetColor(CButtonST::BTNST_COLOR_BK_IN, shBtnColor);
	m_btnset.SetTooltipText(_T("设置"));
	m_btnset.DrawTransparent(TRUE);*/

	int cx,cy;
	HDC dc = ::GetDC(NULL);
	cx = GetDeviceCaps(dc,HORZRES);
	cy = GetDeviceCaps(dc,VERTRES);
	CRect dlgRect;
	GetWindowRect(dlgRect);
	SetWindowPos(&this->wndTopMost,cx - dlgRect.right + dlgRect.left-1,cy - dlgRect.bottom + dlgRect.top - 30,\
		dlgRect.right - dlgRect.left,dlgRect.bottom - dlgRect.top,SWP_NOACTIVATE);

	//	ModifyStyleEx(WS_EX_APPWINDOW, 0);
	ModifyStyleEx(0,WS_EX_TOOLWINDOW|WS_EX_NOACTIVATE);//任务栏不显示

	SetTimer(155, showtime, NULL);

	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
}

void CAlertInfo::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: 在此处添加消息处理程序代码
	// 不为绘图消息调用 CDialog::OnPaint()
	CBitmap   bitmap;
	bitmap.LoadBitmap(IDB_BITMAP1);    //这个IDB_BITMAP1要自己添加
	CBrush   brush;
	brush.CreatePatternBrush(&bitmap);
	CBrush*   pOldBrush   =   dc.SelectObject(&brush);
	dc.Rectangle(0,0,303,213);   // 这些参数可以调整图片添加位置和大小
	dc.SelectObject(pOldBrush);
}

void CAlertInfo::OnTimer(UINT_PTR nIDEvent)
{
	// TODO: 在此添加消息处理程序代码和/或调用默认值
	if (nIDEvent == 155)
	{
		KillTimer(155);
		CDialog::OnCancel();
	}
	CDialog::OnTimer(nIDEvent);
}