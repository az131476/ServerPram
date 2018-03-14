#include "StdAfx.h"
#include "CycQueue.h"
#include "afxmt.h"

CCriticalSection g_cls0;

CCycQueue::CCycQueue(void)
{
}

CCycQueue::~CCycQueue(void)
{
}

void CCycQueue::PushQueue(CString code)
{
	g_cls0.Lock();
	queue[back] = code;
	back++;
	if (back == QUEUE_SIZE)
	{
		back = 0;
	}
	g_cls0.Unlock();
}

CString CCycQueue::PopQueue()
{
	g_cls0.Lock();
	CString value = "";
	if (!queue[key].IsEmpty() && queue[key] != "")
	{
		value = queue[key];
		queue[key] = "";
		key++;
		if (key == QUEUE_SIZE)
		{
			key = 0;
		}
	}
	g_cls0.Unlock();
	return value;
}

