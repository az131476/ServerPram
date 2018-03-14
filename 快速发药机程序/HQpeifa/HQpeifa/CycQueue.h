#pragma once
#define QUEUE_SIZE	1000

static CString queue[QUEUE_SIZE];
static int key = 0;
static int back = 0;

class CCycQueue
{
public:
	CCycQueue(void);
public:
	~CCycQueue(void);
public:
	
	CString PopQueue();
	void	PushQueue(CString code);
};
