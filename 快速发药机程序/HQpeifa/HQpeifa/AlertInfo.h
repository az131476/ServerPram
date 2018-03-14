#pragma once
#include "Config.h"
#include "Label.h"

// CAlertInfo 对话框

class CAlertInfo : public CDialog
{
	DECLARE_DYNAMIC(CAlertInfo)

public:
	CAlertInfo(CWnd* pParent = NULL);   // 标准构造函数
	virtual ~CAlertInfo();

// 对话框数据
	enum { IDD = IDD_DIALOG1 };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持
	virtual BOOL OnInitDialog();
	afx_msg void OnPaint();
	DECLARE_MESSAGE_MAP()
public:
	CConfig cConfig;
	CString info;
	CLabel m_sta_l;
public:
	BOOL cursorC;
	HCURSOR hCurA;
	HCURSOR hCurH;
public:
	afx_msg void OnTimer(UINT_PTR nIDEvent);
};
