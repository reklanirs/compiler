int a;
void delay(int i, int j) {
	int a[10];
	int c;c= 30000;
	i += ++c + 5;
	while(c>0){ c=c - 1;}
	i = a[5] + 3;
}
void main(void)
{
	int i,j,k;
	int key;key=0;
	while(1)
	{
		key=key+1;
		$0xfffffc00=key;

		if(key>100) key=0;
		else if(key>10)key = 1;
		else key-=3;

		delay(a+5);
	}
	for(int i=0; i< 5; i++) if(i>3) printf("hahaha");
	for(;;);
}
