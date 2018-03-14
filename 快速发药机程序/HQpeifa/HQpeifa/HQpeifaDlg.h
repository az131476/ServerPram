// HQpeifaDlg.h : ͷ�ļ�
//

#pragma once
#include <WinSock.h>
#include "Config.h"
#include "SystemTray.h"
#include "mscomm1.h"
#include "afxwin.h"
#include "CycQueue.h"
#include "afxcmn.h"
#include "ListCtrlCl.h"
#include "TaskbarNotifier.h"
#include "AlertInfo.h"
#include "mysql.h"
#pragma comment(lib, "libmysql.lib")
#include "CycQueue.h"

#define WM_USER_TRAY_NOTIFICATION (WM_USER + 0x121)

// CHQpeifaDlg �Ի���
class CHQpeifaDlg : public CDialog
{
// ����
public:
	CHQpeifaDlg(CWnd* pParent = NULL);	// ��׼���캯��

// �Ի�������
	enum { IDD = IDD_HQPEIFA_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV ֧��

// ʵ��
protected:
	HICON m_hIcon;

	// ���ɵ���Ϣӳ�亯��
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	virtual void OnOK();
	virtual void OnCancel();
	DECLARE_MESSAGE_MAP()
public:
	CSystemTray m_trayIcon;
	afx_msg LONG OnTrayNotification( WPARAM wparam, LPARAM lparam );
	afx_msg void ShowDialog();
public:
	afx_msg void OnMenuExit();
public:
	CConfig config;
public:
	CMscomm1 mscomm1;
public:
	CMscomm1 mscomm2;
public:
	DECLARE_EVENTSINK_MAP()
public:
	void OnCommMscomm1();
public:
	void OnCommMscomm2();
public:
	bool OpenMscomm1();
	bool OpenMscomm2();
public:
	afx_msg void OnTimer(UINT_PTR nIDEvent);
public:
	CListBox listbox;
public:
	CString timespan;
	// �����ϼ�
	void DealOrder(CString gh, CString pyd);
	// ������ʱ
	int delayTime;
	CString G_LINENO;
	CString G_CKH;
	
	int cleardelayTime;
	void GetStockList();
public:
	CCycQueue queue;
	static DWORD WINAPI CodeParseT(LPVOID lpParameter);
public:
	afx_msg void OnBnClickedButton1();
public:
	CListCtrlCl listCtrl;
	int rowCount;
	int colCount;
public:
	afx_msg void OnBnClickedButton2();
public:
	afx_msg void OnNMDblclkList2(NMHDR *pNMHDR, LRESULT *pResult);
public:
	afx_msg void OnBnClickedButton3();
public:
	CTaskbarNotifier m_wndTaskbarNotifier3;
	afx_msg LRESULT OnTaskbarNotifierClicked(WPARAM wParam,LPARAM lParam);
public:
	CString DOC_CODE;//ҩʦ����
public:
	CString ALERT_NOORDER;
	CString ALERT_NOEMPTY;
	CString ALERT_ORDERCANCEL;
	CString ALERT_ORDERFINISH;
	CString ALERT_WINDOWERROR;
	CString ALERT_NEEDOPERCODE;
	void SpecialAlert(CString code);
public:
	MYSQL mysql;
	static DWORD WINAPI LightThread2(LPVOID lpParamter);
	CCycQueue cycQueue;
public:
	int codeDelay;
public:
	void ReadLight();
public:
	afx_msg void OnNMRclickList2(NMHDR *pNMHDR, LRESULT *pResult);
};
