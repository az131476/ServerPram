// HQpeifa.h : PROJECT_NAME Ӧ�ó������ͷ�ļ�
//

#pragma once

#ifndef __AFXWIN_H__
	#error "�ڰ������ļ�֮ǰ������stdafx.h�������� PCH �ļ�"
#endif

#include "resource.h"		// ������


// CHQpeifaApp:
// �йش����ʵ�֣������ HQpeifa.cpp
//

class CHQpeifaApp : public CWinApp
{
public:
	CHQpeifaApp();

// ��д
	public:
	virtual BOOL InitInstance();

// ʵ��

	DECLARE_MESSAGE_MAP()
};

extern CHQpeifaApp theApp;