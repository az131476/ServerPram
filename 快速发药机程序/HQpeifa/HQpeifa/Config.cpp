#include "stdafx.h"
#include "Config.h"

CConfig::CConfig(void)
{
	configPath = _T(".\\config.ini"); 
}

CConfig::~CConfig(void)
{
}

//////////////////////////////////////////////////////////////////////////
//
CString CConfig::GetParam(CString rootKey,CString subKey)
{
	CString paramStr;
	GetPrivateProfileString(rootKey,subKey,_T(""),paramStr.GetBuffer(MAX_PATH),MAX_PATH,configPath);
	paramStr.ReleaseBuffer();
	return paramStr;
}

BOOL	CConfig::SetParam(CString rootKey,CString subKey,CString values)
{
	return WritePrivateProfileString(rootKey,subKey,values,configPath);
}

//////////////////////////////////////////////////////////////////////////