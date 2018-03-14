#pragma once

class CConfig
{
public:
	CConfig(void);
public:
	~CConfig(void);
public:
	//////////////////////////////////////
	CString GetParam(CString rootKey,CString subKey);
	BOOL	SetParam(CString rootKey,CString subKey,CString values);
	CString	configPath;

};
