// AlertInfo.cpp : ʵ���ļ�
//

#include "stdafx.h"
#include "HQpeifa.h"
#include "AlertInfo.h"


// CAlertInfo �Ի���

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


// CAlertInfo ��Ϣ�������
BOOL CAlertInfo::OnInitDialog()
{
	CDialog::OnInitDialog();

	// TODO: �ڴ���Ӷ���ĳ�ʼ������
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
		get_str = "��ʾ����";
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
	m_btnset.SetTooltipText(_T("����"));
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
	ModifyStyleEx(0,WS_EX_TOOLWINDOW|WS_EX_NOACTIVATE);//����������ʾ

	SetTimer(155, showtime, NULL);

	return TRUE;  // ���ǽ��������õ��ؼ������򷵻� TRUE
}

void CAlertInfo::OnPaint()
{
	CPaintDC dc(this); // device context for painting
	// TODO: �ڴ˴������Ϣ����������
	// ��Ϊ��ͼ��Ϣ���� CDialog::OnPaint()
	CBitmap   bitmap;
	bitmap.LoadBitmap(IDB_BITMAP1);    //���IDB_BITMAP1Ҫ�Լ����
	CBrush   brush;
	brush.CreatePatternBrush(&bitmap);
	CBrush*   pOldBrush   =   dc.SelectObject(&brush);
	dc.Rectangle(0,0,303,213);   // ��Щ�������Ե���ͼƬ���λ�úʹ�С
	dc.SelectObject(pOldBrush);
}

void CAlertInfo::OnTimer(UINT_PTR nIDEvent)
{
	// TODO: �ڴ������Ϣ�����������/�����Ĭ��ֵ
	if (nIDEvent == 155)
	{
		KillTimer(155);
		CDialog::OnCancel();
	}
	CDialog::OnTimer(nIDEvent);
}