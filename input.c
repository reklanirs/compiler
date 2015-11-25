int a;
void delay(int i, int j, int d[10]) {
	int a[10];
	short s;
	char c;
	unsigned char uc;
	unsigned short us;
	unsigned int ui;
	char q;
	short w;
	int e;
	int *p;

	a[1] = 1000;
	while(c>0){ c=c - 1;}
	i = a[5] + 3;
}
int main(void)
{
	int i,j,k[3];
	int key;
	int *LED;
	key=0;
	LED = 0xfffffc60;
	while(1)
	{
		key=key+1;
		*LED=key;
		if(key>100) key=0;
		else if(key>10)key = 1;
		else key-=3;

		delay(a+5, k[2]);
	}
}

