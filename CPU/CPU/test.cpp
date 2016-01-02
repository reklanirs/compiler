#include <iostream>
#include <bitset>
#include <fstream>
#include <vector>
#include <map>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <iomanip>
#include <stdlib.h>
#include <string>
using namespace std;

ifstream code;					//读入汇编代码
ofstream prog;					//输出rom
ofstream ram;				//输出单ROM单RAM模式下的ram
ofstream ram0;				//输出单ROM4RAM模式下的ram0
ofstream ram1;				//输出单ROM4RAM模式下的ram1
ofstream ram2;				//输出单ROM4RAM模式下的ram2
ofstream ram3;				//输出单ROM4RAM模式下的ram3

int p_line=0;					//代码所在代码段的行号
int d_line=0;					//数据所在数据段的行号
int total_line=0;				//实际汇编代码的行号
int reach_code=0;				//是否到达代码段
int reach_data=0;				//是否到达数据段
int baseaddress = 0;			//记录数据段基地址，因为.data后面可以直接指定数据段开始的地址
string err;
int line_prog = 0;					//记录prog.coe输出行数

struct data_info
{
	string info;				//记录数据段每行的信息
	int line;					//记录该行在代码中的绝对地址用于错误检测与定位
};


struct parameter				//存储一个变量的所有信息
{
	int base;					//该变量所在数据段的段基址
	//int line;					//变量在代码中的绝对行号
    int offset;					//该变量所在数据段中的相对地址
	string name;				//变量名称
	vector<string> p_data;		//存储变量的二进制形式，当该变量存储了多个数据时，一次加进去
	int p_type;					//变量类型
	/*
	p_type=1	.byte		1个字节			8位
	p_type=2	.half		半字			16位
	p_type=3	.word		字				32位
	p_type=4	.ascii		ASCII字符串类型	一个字符占一个字节，用其ascii码值存储
	p_type=5	.asciiz		以'\0'结尾的ASCII字符串类型		
	*/
};



map <string,int> label;				//存所有出现的标号
vector<data_info> dataseg;			//将数据段的每一行记录下来以便分析变量
vector<parameter> para_info;		//存放所有变量的信息
map<string,int>::iterator itofmap;
vector<parameter>::iterator itofpara;
vector<data_info>::iterator itofdatainfo;
vector<string>::iterator it;


void scan_first(char * str);	//第一次扫描
void scan_second(char * str);	//第二次扫描
int ident(string str);			//返回指令str的编号
int ident_reg(string name);		//识别名字为name的寄存器对应的寄存器编号
void store_dataseg();			//数据段的识别与存储
void write_data();				//数据RAM的写入
void outputerror(string err);	//输出error
void check_operand(vector<string> & operand,string check,int id);	//检测操作数，并返回该指令操作数的个数
void no_bracket(string name,vector<string> & operand,int id);	//对于无括号内容的处理
void get_p_data(string base,int radix,parameter * p);	//获取parameter的p_data属性，其中radix为进制
string to32(string & str);		//将str非符号扩展至32位

string filepath[7];

int main()
{
	ifstream paths("d:\\paths.txt");

	char path[100];
	paths.getline(path, sizeof(path));
	filepath[0] = path;
	code.open(path, ios::in);
	paths.getline(path, sizeof(path));
	filepath[1] = path;
	prog.open(path, ios::out);
	paths.getline(path, sizeof(path));
	filepath[2] = path;
	ram.open(path, ios::out);
	paths.getline(path, sizeof(path));
	filepath[3] = path;
	ram0.open(path, ios::out);
	paths.getline(path, sizeof(path));
	filepath[4] = path;
	ram1.open(path, ios::out);
	paths.getline(path, sizeof(path));
	filepath[5] = path;
	ram2.open(path, ios::out);
	paths.getline(path, sizeof(path));
	filepath[6] = path;
	ram3.open(path, ios::out);
	paths.close();

	cout<<"第一次扫描..."<<endl;
	itofdatainfo = dataseg.begin();
	while(!code.eof())
	{
		total_line++;
		/*一行最多可能出现多少个字符？100个是否合适*/
		char str[100];
		code.getline(str,sizeof(str));

		stringstream s(str);
		string temps;
		s>>temps;
		if(temps[0]=='\0')			//去除空行，空行不计入代码段的相对行数
			continue;
		/*下面开始第一遍扫描，把所有变量和标号都取出来记下*/
		scan_first(str);			
		if(reach_code==1)
			p_line++;				//这里记录行数实际是为了定位错误行
		if(reach_data==1)
			d_line++;				
	}
	cout<<"第一次扫描完成！"<<endl<<endl;

	
	//第一次扫描完成后进行数据存储
	store_dataseg();

	cout<<"变量信息："<<endl;
	itofpara=para_info.begin();
	for(;itofpara!=para_info.end();itofpara++)
	{
		cout<<"变量："<<itofpara->name<<"的值为：";
		for(it = (itofpara->p_data).begin();it!= (itofpara->p_data).end();it++)
		{
			cout<<*it;
		}
		cout<<"所在数据段的段基址为："<<itofpara->base<<"在数据段中的相对地址为："<<itofpara->offset<<endl;
	}
	cout<<endl;

	cout<<"标号信息："<<endl;
	for(itofmap=label.begin();itofmap!=label.end();itofmap++)
	{
		bitset<32> label_addr(itofmap->second);
		cout<<"标号："<<itofmap->first<<"在rom中的地址为"<<label_addr<<"("<<itofmap->second<<")"<<endl;
	}
	cout<<endl;

	//第一次扫描后即可进行RAM的写入
	write_data();

	reach_code=0;
	reach_data=0;
	total_line=0;
	p_line=0;

	code.close();
	code.open(filepath[0]);
	//code.seekg(0,ios::beg);
	cout<<"第二次扫描..."<<endl<<endl;
	cout<<"开始写入prog.coe文件："<<endl;

	prog<<"memory_initialization_radix = 2;"<<endl;
	prog<<"memory_initialization_vector ="<<endl;

	while(!code.eof())
	{
		total_line++;
		/*一行最多可能出现多少个字符？100个是否合适*/
		char str[100];
		code.getline(str,sizeof(str));

		stringstream s(str);
		string temps;
		s>>temps;
		if(temps[0]=='\0')			//去除空行，空行不计入代码段的相对行数
			continue;
		/*下面开始第二遍扫描，把所有变量和标号多取出来记下*/
		scan_second(str);			
		if(reach_code==1)
			p_line++;
		if(reach_data==1)
			d_line++;
	}
	for(int i = line_prog;i<pow(2,14)-1;i++)
	{
		prog<<"00000000000000000000000000000000,"<<endl;
	}
	prog << "00000000000000000000000000000000;";


	cout<<"第二次扫描完成！"<<endl<<endl;

	code.close();
	prog.close();
	ram.close();
	ram0.close();
	ram1.close();
	ram2.close();
	ram3.close();

	return 0;
}


void scan_first(char * str)
{
	vector<string> split_str;		//用于存放按照空格分割后的str
	string tempword;
	stringstream ss(str);
	while(ss>>tempword)
		split_str.push_back(tempword);
	ss.clear();
	it=split_str.begin();

	int idofstr=ident(*it);			//用于记录str的编号


	if(reach_code==0)	//还未到达代码段
	{
		if(reach_data==0)//还未到达数据段（这里可能会有模式的选择）
		{
			if(idofstr!=58)
			{
				err="格式错误！数据段之前不能出现非关键字！";
				outputerror(err);
			}
			else
			{
					//这里可以用于判断所选模式
			}
		}
		else//到达数据段还未到达代码段,则里面全是数据的定义
		{
			data_info temp;
			temp.info = str;		//记录该行数据信息的内容
			temp.line = total_line;	//记录该数据信息在代码中的绝对位置，以便定位错误行
			dataseg.push_back(temp);	
		}
	}


	else//到达代码段
	{
		if(*it==".CODE"||*it==".code")
			p_line--;						//.code是第0行

		else if(idofstr==60)				//第一次扫描记下所有标号
		{
			if(++it!=split_str.end())
			{
				err="格式错误！标号定义只能单独出现在一行，该行后面不能出现代码！";
				outputerror(err);
			}
			--it;						//重新定位到标号值
			label.insert(pair<string ,int >((*it).substr(0,(*it).length()-1),4*p_line));
		}

	}//代码段的一句分析完成
}



void scan_second(char * str)
{
	vector<string> split_str;		//用于存放按照空格分割后的str
	string tempword;
	stringstream ss(str);
	while(ss>>tempword)
	{
		if(tempword[0]=='#')		//#号后面的是注释
			break;
		split_str.push_back(tempword);
	}
	ss.clear();
	it=split_str.begin();
	
	int idofstr=ident(*it);				//用于记录str的编号
	if(reach_code==1 && idofstr!=58)	//只读取实际代码，不再分析标号
	{
		cout<<str<<"      "<<idofstr<<endl;	//用于测试id是否识别正确
		vector<string> operand;			//用于记录指令中的操作数

		++it;							//定位到第二个字符

		string merge_operand;			//用于存放合并空格后的所有操作数
		//对于可能出现的两个操作数之间不仅有逗号，用户可能还加了空格的情况，先合并操作码后的所有字符，再处理
		for(;it!=split_str.end();it++)
		{
			merge_operand+=(*it);	//merge_operand中仍然有逗号中括号等
		}
		
		check_operand(operand,merge_operand,idofstr);

		string outcode="";
		switch(idofstr)
		{
		case 1://add
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100000";
			break;
			
		case 2://addu
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100001";
			break;

		case 3://sub
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100010";
			break;

		case 4://subu
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100011";
			break;

		case 5://and
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100100";
			break;

		case 6://mult
			outcode+="000000";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="0000000000011000";
			break;

		case 7://multu
			outcode+="000000";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="0000000000011001";
			break;

		case 8://div
			outcode+="000000";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="0000000000011010";
			break;

		case 9://divu
			outcode+="000000";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="0000000000011011";
			break;

		case 10://mfhi
			outcode+="0000000000000000";
			outcode+=operand[0];
			outcode+="00000010000";
			break;

		case 11://mflo
			outcode+="0000000000000000";
			outcode+=operand[0];
			outcode+="00000010010";
			break;

		case 12://mthi
			outcode+="000000";
			outcode+=operand[0];
			outcode+="000000000000000010001";
			break;

		case 13://mtlo
			outcode+="000000";
			outcode+=operand[0];
			outcode+="000000000000000010011";
			break;

		case 14://mfc0
			outcode+="01000000000";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="00000000";
			outcode+=operand[2];
			break;

		case 15://mtc0
			outcode+="01000000100";
			outcode+=operand[0];
			outcode+=operand[1];
			outcode+="00000000";
			outcode+=operand[2];
			break;

		case 16://or
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100101";
			break;
	
		case 17://xor
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100110";
			break;

		case 18://nor
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100111";
			break;

		case 19://slt
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000101010";
			break;

		case 20://sltu
			outcode+="000000";
			outcode+=operand[1];
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+="00000100111";
			break;

		case 21://sll
			outcode="00000000000";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			outcode+="000000";
			break;

		case 22://srl
			outcode="00000000000";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			outcode+="000010";
			break;

		case 23://sra
			outcode="00000000000";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			outcode+="000011";
			break;

		case 24://sllv
			outcode="000000";
			outcode+=operand[2];
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+="00000000100";
			break;

		case 25://srlv
			outcode="000000";
			outcode+=operand[2];
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+="00000000110";
			break;

		case 26://srav
			outcode="000000";
			outcode+=operand[2];
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+="00000000111";
			break;

		case 27://jr
			outcode+="000000";
			outcode+=operand[0];
			outcode+="000000000000000001000";
			break;

		case 28://jalr
			outcode+="000000";
			outcode+=operand[1];
			outcode+="00000";
			outcode+=operand[0];
			outcode+="00000001001";
			break;

		case 29://break
			break;

		case 30://syscall
			break;

		case 31://eret
			outcode="00000010000000000000000000011000";
			break;

		case 32://addi
			outcode+="001000";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 33://addiu
			outcode+="001001";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 34://andi
			outcode+="001100";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 35://ori
			outcode+="001101";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 36://xori
			outcode+="001110";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 37://lui
			outcode+="00111100000";
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 38://lb
			outcode+="100000";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 39://lbu
			outcode+="100100";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 40://lh
			outcode+="100001";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 41://lhu
			outcode+="100101";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 42://sb
			outcode+="101000";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 43://sh
			outcode+="101001";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 44://lw
			outcode+="100011";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 45://sw
			outcode+="101011";
			outcode+=operand[2];
			outcode+=operand[0];
			outcode+=operand[1];
			break;

		case 46://beq
			outcode+="000100";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 47://bne
			outcode+="000101";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 48://bgez
			outcode+="000001";
			outcode+=operand[0];
			outcode+="00001";
			outcode+=operand[1];
			break;

		case 49://bgtz
			outcode+="000111";
			outcode+=operand[0];
			outcode+="00000";
			outcode+=operand[1];
			break;

		case 50://blez
			outcode+="000110";
			outcode+=operand[0];
			outcode+="00000";
			outcode+=operand[1];
			break;

		case 51://bltz
			outcode+="000111";
			outcode+=operand[0];
			outcode+="00000";
			outcode+=operand[1];
			break;

		case 52://bgezal
			outcode+="000001";
			outcode+=operand[0];
			outcode+="10001";
			outcode+=operand[1];
			break;

		case 53://bltzal
			outcode+="000001";
			outcode+=operand[0];
			outcode+="10000";
			outcode+=operand[1];
			break;

		case 54://slti
			outcode+="001010";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 55://sltiu
			outcode+="001011";
			outcode+=operand[1];
			outcode+=operand[0];
			outcode+=operand[2];
			break;

		case 56://j
			outcode+="000010";
			outcode+=operand[0];
			break;

		case 57://jal
			outcode+="000011";
			outcode+=operand[0];
			break;

		case 59:
			cout<<"出错行数："<<total_line<<"行!代码段中不能出现变量定义!"<<endl;
			//prog<<"出错行数："<<total_line<<"行!代码段中不能出现变量定义!"<<endl;
			exit(1);


		case 60://标号出统一翻译成nop
			outcode="00000000000000000000000000000000";
			break;

		case 61:
			outcode="00000000000000000000000000000000";
			break;

		default:
			break;

	}

	outcode+=",";
	prog<<outcode<<endl;
	line_prog++;
	cout<<outcode<<endl;

	}
}



int ident(string str)//1-57为指令，58为关键字，59为变量，60为标号,61为结束符
{
	if(str=="add" || str=="ADD")
	{

		return 1;
	}

	if(str=="addu" || str=="ADDU")
	{
		return 2;
	}

	if(str=="sub" || str=="SUB")
	{
		return 3;
	}

	if(str=="subu" || str=="SUBU")
	{
		return 4;
	}

	if(str=="and" || str=="AND")
	{
		return 5;
	}

	if(str=="mult" || str=="MULT")
	{
		return 6;
	}

	if(str=="multu" || str=="MULTU")
	{
		return 7;
	}

	if(str=="div" || str=="DIV")
	{
		return 8;
	}

	if(str=="divu" || str=="DIVU")
	{
		return 9;
	}

	if(str=="mfhi" || str=="MFHI")
	{
		return 10;
	}

	if(str=="mflo" || str=="MFLO")
	{
		return 11;
	}

	if(str=="mthi" || str=="MTHI")
	{
		return 12;
	}

	if(str=="mtlo" ||str=="MTLO")
	{
		return 13;
	}

	if(str=="mfc0" || str=="MFC0")
	{
		return 14;
	}

	if(str=="mtc0" || str=="MTC0")
	{
		return 15;
	}

	if(str=="or" || str=="OR")
	{
		return 16;
	}

	if(str=="xor" || str=="XOR")
	{
		return 17;
	}

	if(str=="nor" || str=="NOR")
	{
		return 18;
	}

	if(str=="slt" || str=="SLT")
	{
		return 19;
	}

	if(str=="sltu" || str=="SLTU")
	{
		return 20;
	}

	if(str=="sll" || str=="SLL")
	{
		return 21;
	}

	if(str=="srl" || str=="SRL")
	{
		return 22;
	}

	if(str=="sra" || str=="SRA")
	{
		return 23;
	}

	if(str=="sllv" || str=="SLLV")
	{
		return 24;
	}

	if(str=="srlv" || str=="SRLV")
	{
		return 25;
	}

	if(str=="srav" || str=="SRAV")
	{
		return 26;
	}

	if(str=="jr" || str=="JR")
	{
		return 27;
	}

	if(str=="jalr" || str=="JALR")
	{
		return 28;
	}

	if(str=="break" || str=="BREAK")
	{
		return 29;
	}

	if(str=="syscall" || str=="SYSCALL")
	{
		return 30;
	}

	if(str=="eret" || str=="ERET")
	{
		return 31;
	}

	if(str=="addi" || str=="ADDI")
	{
		return 32;
	}

	if(str=="addiu" || str=="ADDIU")
	{
		return 33;
	}

	if(str=="andi" || str=="ANDI")
	{
		return 34;
	}

	if(str=="ori" || str=="ORI")
	{
		return 35;
	}

	if(str=="xori" || str=="XORI")
	{
		return 36;
	}

	if(str=="lui" || str=="LUI")
	{
		return 37;
	}

	if(str=="lb" || str=="LB")
	{
		return 38;
	}

	if(str=="lbu" || str=="LBU")
	{
		return 39;
	}

	if(str=="lh" || str=="LH")
	{
		return 40;
	}

	if(str=="lhu" || str=="LHU")
	{
		return 41;
	}

	if(str=="sb" || str=="SB")
	{
		return 42;
	}

	if(str=="sh" || str=="SH")
	{
		return 43;
	}

	if(str=="lw" || str=="LW")
	{
		return 44;
	}

	if(str=="sw" || str=="SW")
	{
		return 45;
	}

	if(str=="beq" || str=="BEQ")
	{
		return 46;
	}

	if(str=="bne" || str=="BNE")
	{
		return 47;
	}

	if(str=="bgez" || str=="BGEZ")
	{
		return 48;
	}

	if(str=="bgtz" || str=="BGTZ")
	{
		return 49;
	}

	if(str=="blez" || str=="BLEZ")
	{
		return 50;
	}

	if(str=="bltz" || str=="BLTZ")
	{
		return 51;
	}

	if(str=="bgezal" || str=="BGEZAL")
	{
		return 52;
	}

	if(str=="bltzal" || str=="BLTZAL")
	{
		return 53;
	}

	if(str=="slti" || str=="SLTI")
	{
		return 54;
	}

	if(str=="sltiu" || str=="SLTIU")
	{
		return 55;
	}

	if(str=="j" || str=="J")
	{
		return 56;
	}

	if(str=="jal" || str=="JAL")
	{
		return 57;
	}


	if(str.substr(0,5)==".code"||str.substr(0,5)==".CODE")//以.data开头即说明到达数据段，当然后面可以接指定的地址
	{
		reach_code=1;
		return 58;
	}
	
	if(str.substr(0,5)==".data"||str.substr(0,5)==".DATA")//以.code开头即说明到达代码段，当然后面可以接指定的地址
	{
		reach_data=1;
		return 58;
	}
	if(str==".model"||str==".MODEL"||str==".stack"||str==".STACK")
	{
		return 58;
	}

	if(str=="END" || str=="end")
	{
		return 61;		//必须是程序结束才能用到END
	}

	if(str==".space" || str==".align")
	{
		return 62;		//伪指令
	}

	if(str[0]<65||str[0]>122||(str[0]<=96&&str[0]>=91))
	{
		err="变量或标号不符合规范（应该以字母为开头）!";
		outputerror(err);
	}
	else
	{
		if(str[str.length()-1]==':')//最后一个字符是：表示是标号
			return 60;		//标号
		else
			return 59;		//变量
	}
}


int ident_reg(string name)		//识别名字为name的寄存器对应的寄存器编号
{
	if(name == "0" || name == "zero")
	{
		return 0;
	}
	else if(name == "1" || name == "at")
	{
		return 1;
	}
	else if(name == "2" || name == "v0")
	{
		return 2;
	}
	else if(name == "3" || name == "v1")
	{
		return 3;
	}
	else if(name == "4" || name == "a0")
	{
		return 4;
	}
	else if(name == "5" || name == "a1")
	{
		return 5;
	}
	else if(name == "6" || name == "a2")
	{
		return 6;
	}
	else if(name == "7" || name == "a3")
	{
		return 7;
	}
	else if(name == "8" || name == "t0")
	{
		return 8;
	}
	else if(name == "9" || name == "t1")
	{
		return 9;
	}
	else if(name == "10" || name == "t2")
	{
		return 10;
	}
	else if(name == "11" || name == "t3")
	{
		return 11;
	}
	else if(name == "12" || name == "t4")
	{
		return 12;
	}
	else if(name == "13" || name == "t5")
	{
		return 13;
	}
	else if(name == "14" || name == "t6")
	{
		return 14;
	}
	else if(name == "15" || name == "t7")
	{
		return 15;
	}
	else if(name == "16" || name == "s0")
	{
		return 16;
	}
	else if(name == "17" || name == "s1")
	{
		return 17;
	}
	else if(name == "18" || name == "s2")
	{
		return 18;
	}
	else if(name == "19" || name == "s3")
	{
		return 19;
	}
	else if(name == "20" || name == "s4")
	{
		return 20;
	}
	else if(name == "21" || name == "s5")
	{
		return 21;
	}
	else if(name == "22" || name == "s6")
	{
		return 22;
	}
	else if(name == "23" || name == "s7")
	{
		return 23;
	}
	else if(name == "24" || name == "t8")
	{
		return 24;
	}
	else if(name == "25" || name == "t9")
	{
		return 25;
	}
	else if(name == "26" || name == "k0")
	{
		return 26;
	}
	else if(name == "27" || name == "k1")
	{
		return 27;
	}
	else if(name == "28" || name == "gp")
	{
		return 28;
	}
	else if(name == "29" || name == "sp")
	{
		return 29;
	}
	else if(name == "30" || name == "s8" || name=="fp")
	{
		return 30;
	}
	else if(name == "31" || name == "ra")
	{
		return 31;
	}
	else //未识别的寄存器，报错
	{
		return -1;
	}

}


void get_p_data(string base,int radix,parameter * p)	//获取parameter的p_data属性，其中base为字符串，radix为进制
	//将radix进制的字符串base所对应的二进制值放到p中
{
		string temp ;
		if(p->p_type==1)
		{
			if(base == "?")				//占位符
			{
				temp = "00000000";
			}
			else
			{
				const char *ix=base.c_str();/*一个radix进制字符串*/
				long i8=strtol(ix,NULL,radix);/*读取10进制的值*/
				bitset<8> bb(i8);
				temp=bb.to_string();
			}
		}

		if(p->p_type==2)
		{
			if(base == "?")				//占位符
			{
				temp = "0000000000000000";
			}
			else
			{
				const char *ix=base.c_str();/*一个radix进制字符串*/
				long i16=strtol(ix,NULL,radix);/*读取10进制的值*/
				bitset<16> bb(i16);
				temp=bb.to_string();
			}
		}

		if(p->p_type==3)
		{
			if(base == "?")				//占位符
			{
				temp = "00000000000000000000000000000000";
			}
			else
			{
				const char *ix=base.c_str();/*一个radix进制字符串*/
				long i32=strtol(ix,NULL,radix);/*读取10进制的值*/
				bitset<32> bb(i32);
				temp=bb.to_string();
			}
		}

		//temp += p->p_data;				//将新出现的数据放到p->data的前面
		//p->p_data = temp;
		p->p_data.push_back(temp);

}

void store_dataseg()
{
	//struct data_info				//存储数据段信息
	//{
	//	string info;				//记录数据段每行的信息
	//	int line;					//记录该行在代码中的绝对地址用于错误检测与定位
	//};


	/*vector<parameter>				//para_info中存放所有数据的信息
	struct parameter
	{
		int line;					//数据所在数据段的行号
		int address;				//数据地址,指排序完后的相对地址
		string name;				//变量名称
		string p_data;				//二进制（当然可能是16位或者8位的值）
		int p_type;					//变量类型
	};
	*/


	itofdatainfo = dataseg.begin();
	//for(;itofdatainfo!=dataseg.end();itofdatainfo++)
	//{
	//	cout<<itofdatainfo->info<<"           "<<itofdatainfo->line<<endl;
	//}
	parameter temp_p;					//用于存储一个变量的信息
	int offset;							//记录变量在数据段中的偏移量

	for(itofdatainfo = dataseg.begin();itofdatainfo!=dataseg.end();itofdatainfo++)
	{
		total_line = itofdatainfo->line;//定位到该句话在代码中的具体行数，以便分析错误。

		vector<string> split_str;		//用于存放按照空格分割后的str
		string tempword;
		stringstream ss(itofdatainfo->info);
		while(ss>>tempword)
			split_str.push_back(tempword);
		ss.clear();
		it=split_str.begin();

		int idofstr=ident(*it);			//用于记录str的编号

		if(idofstr==58)		//.DATA指令
		{
			++it;
			if(it!=split_str.end())//.DATA后面还有指定的数据段开始的地址,如.data 0x10000200
			{
				int strlen = (*it).length();
				string a = (*it).substr(2,strlen-2);//获取0x后面的16进制字符串
				if(((*it)[0]=='0')&&((*it)[1]=='x'))//指定的数据段的基址为16进制表示的
				{
					const char *i=a.c_str();/*一个16进制字符串*/
					baseaddress=strtol(i,NULL,16);/*读取10进制的值*/
					cout<<"基址为："<<baseaddress<<endl;
				}
				else//指定的数据段的基址为10进制表示的
				{
					baseaddress=atoi(a.c_str());
				}

			}
			else//.data后面没数据了
			{
				baseaddress = 0;
			}
			offset = 0;								//因为又开始了一个新的数据段，所以偏移量重新置0
		}


		else//非.DATA，则是数据定义或者.spcae;.align，暂不支持一个变量能有多种数据类型
		{
			
			if(*it==".space")		//.space n空出n个字节的空间
			{
				++it;
				int space = atoi((*it).c_str());
				string temp;

				if(space == 1)
				{
					bitset<8> bit(0);
					temp=bit.to_string();
				}
				else if(space == 2)
				{
					bitset<16> bit(0);
					temp=bit.to_string();
				}
				else if(space == 3)
				{
					bitset<24> bit(0);
					temp=bit.to_string();
				}

				//temp += temp_p.p_data;				//将新出现的数据放到p->data的前面
				//temp_p.p_data = temp;
				temp_p.p_data.push_back(temp);

				offset += space;	//更新偏移量
			}

			else if(*it=="align")	//.align n 对下一个定义的数据做2n字节对齐。此处n必须大于1
			{

			}


			///*如果变量定义允许一个变量名存储多种数据类型*/
			//else if(*it==".word")
			//{

			//}
			//
			//else if(*it==".half")
			//{

			//}
			//
			//else if(*it==".byte")
			//{

			//}

			else//现在只能是变量名了
			{
				if(((*it)[0]>='a'&&(*it)[0]<='z')||((*it)[0]>='A'&&(*it)[0]<='Z'))//确实出现新变量名
				{
					
					if(temp_p.p_type==1||temp_p.p_type==2||temp_p.p_type==3)
						para_info.push_back(temp_p);

					//memset(&temp_p, 0, sizeof(parameter));//出现一个新的变量名，则清空原变量的信息
					//temp_p.p_data.clear();

					//清空
					(temp_p.p_data).swap(vector<string>());

					temp_p.base = baseaddress;	//变量所在数据段的段基址
					temp_p.offset = offset;		//变量在该数据段的相对地址
					//temp_p.line=d_line;		//该数据在数据段的相对行号
					it=split_str.begin();
					temp_p.name=*it;


					it++;					//定位到数据类型
					if(*it==".byte")
						temp_p.p_type=1;
					else if(*it==".half")
						temp_p.p_type=2;
					else if(*it==".word")
						temp_p.p_type=3;
					//else if(*it==".ascii")
					//	temp_p.p_type=4;
					//else if(*it==".asciiz")
					//	temp_p.p_type=5;
					else
					{
						err="数据定义格式出错，类型只能为.byte，.half，.word中的一种!";
						outputerror(err);
					}

					it++;					//定位到数据值
					string check = *it;		

					//将数据值中可能出现的逗号置换成空格，便于区分开
					for(int i=0;i < check.length();i++)
					{
						if(check[i]==',')
						{
							check[i]=' ';
							//遇到一个逗号意味着多一个数据，那么偏移量要更新
							if(temp_p.p_type==1)//byte型的数据，一个数据占1个字节
								offset +=1;
							else if(temp_p.p_type==2)//half型的数据，一个数据占2个字节
								offset +=2;
							else//word型的数据，一个数据占4个字节
								offset +=4;
						}
					}
					//最后还有一个数据
					if(temp_p.p_type==1)//byte型的数据，一个数据占1个字节
						offset +=1;
					else if(temp_p.p_type==2)//half型的数据，一个数据占2个字节
						offset +=2;
					else//word型的数据，一个数据占4个字节
						offset +=4;


					vector<string> split_data;		//用于存放按照空格分割后的数据
					string tempword;
					stringstream ss(check);
					while(ss>>tempword)
						split_data.push_back(tempword);
					ss.clear();
					it=split_data.begin();

					for(;it!=split_data.end();it++)
					{
						int radix =10;				//默认该数据是十进制的
						string base = *it;
						if((*it).substr(0,2)=="0x")
						{
							radix = 16;				//0x开头为十六进制
							base = base.substr(2,base.length()-2);
						}
						if((*it)[(*it).length()-1]=='h'||(*it)[(*it).length()-1]=='H')
						{
							radix = 16;				//b结尾是二进制
							base = base.substr(0,base.length()-1);
						}
						if((*it)[(*it).length()-1]=='o'||(*it)[(*it).length()-1]=='O')
						{
							radix = 8;				//b结尾是二进制
							base = base.substr(0,base.length()-1);
						}
						if((*it)[(*it).length()-1]=='b'||(*it)[(*it).length()-1]=='B')
						{
							radix = 2;				//b结尾是二进制
							base = base.substr(0,base.length()-1);
						}
						get_p_data(base,radix,&temp_p);
					}
				}
				else//变量名不以字母开头
				{
						err="数据定义格式出错，变量名只能以字母开头!";
						outputerror(err);
				}

			}//是否为变量名讨论完毕

		}

	}//一行数据段处理完毕
	para_info.push_back(temp_p);//所有数据读完后，最后一个数据在这里存储


}



void check_operand(vector<string> & operand,string check,int id)
{
	int numofoprd=0;				//记录操作数的个数
	int regnum=0;					//用于记录寄存器号

	//将集合后的操作数中的逗号（，）置换成空格，便于区分开
	for(int i=0;i < check.length();i++)
	{
		if(check[i]==',')
		{
			check[i]=' ';
			numofoprd++;				//遇到一个逗号意味着多一个操作数
		}
	}
	numofoprd++;						//最后还有一个操作数


	vector<string> split_oprd;			//用于存放按照空格分割后的操作数
	string tempword;
	stringstream ss(check);
	while(ss>>tempword)
		split_oprd.push_back(tempword);
	ss.clear();
	it=split_oprd.begin();

	for(;it!=split_oprd.end();it++)
	{
		int pos_l=(*it).find('(',0);	//查找该操作数中(的位置
		int pos_r=(*it).find(')',0);	//查找该操作数中)的位置

		if(pos_l==string::npos && pos_r==string::npos)		//没找到'('也没找到')'
		{
			no_bracket(*it,operand,id);	//调用无括号时的处理函数
		}

		else if(pos_l!=string::npos && pos_r>pos_l)//找到了左右括号
		{
			numofoprd++;				//出现相对寻址index[base]，要多出一个操作数
			//int base;					//基址
			int index;					//变址
			if(pos_l==0)//左括号左边没数值
				index=0;
			else//左括号左边有数值，可能是整数或者变量名
			{
				string temp=(*it).substr(0,pos_l);//取出“（”之前的部分
				if(temp[0]>47 && temp[0]<58)//是整数
				{
					index=atoi(temp.c_str());
				}
				else//可能是变量名
				{
					//vector<parameter> para_info;
					itofpara=para_info.begin();
					for(;itofpara!=para_info.end();itofpara++)
					{
						//cout<<"变量："<<itofpara->name<<"的值为："<<itofpara->p_data<<"地址为："<<itofpara->address<<endl;
						if(temp==(itofpara->name))
						{
							//const char *pp=(itofpara->p_data[0]).c_str();
							//index=strtol(pp,NULL,2);
							index = itofpara->base + itofpara->offset;
							break;
						}
					}
					if(itofpara==para_info.end())//不是变量
					{
						err="格式出错！无法识别括号左边的偏移量,既不是立即数也不是变量！";
						outputerror(err);
					}
				}
			}//到此，index的所有可能情况分析完毕

			bitset<16> bb(index);
			operand.push_back(bb.to_string());
			//下面分析base的可能情况

			string l2r=(*it).substr(pos_l+1,pos_r-1-pos_l);//取出“（”之后，“）”之前的部分
			//base基址只能是寄存器，因此只可能以$开头
			//该操作数以$开头，则只需简单判断其寄存器号

			/*操作数以$开头*/
			if(l2r[0]=='$')
			{
				string r=l2r.substr(1,l2r.length()-1);		//去掉$
				regnum=ident_reg(r.c_str());
				if(regnum>31||regnum<0)
				{
					err="寄存器号越界（寄存器号范围是0-31）！";
					outputerror(err);
				}
				else//没有越界，则将该寄存器记录下来
				{
					bitset<5> bb(regnum);
					operand.push_back(bb.to_string());
				}
			}

			else
			{
				err="括号内只能出现寄存器！";
				outputerror(err);
			}

		}
		else
		{
			err="左右括号不匹配！";
			outputerror(err);
		}

	}//一个操作数检测完成

}


void no_bracket(string name,vector<string> & operand,int id)	//代码段无括号处理方式

{
	int regnum;

	/*操作数以$开头，则是寄存器*/
	if(name[0]=='$')
	{
		string r=name.substr(1,name.length()-1);		//去掉$
		regnum=ident_reg(r.c_str());
		if(regnum>31||regnum<0)
		{
			err="寄存器号越界（寄存器号范围是0-31）！";
			outputerror(err);
		}
		else//没有越界，则将该寄存器记录下来
		{
			bitset<5> bb(regnum);
			operand.push_back(bb.to_string());
		}
	}


	//该操作数以数字开头，则可能是imm或者shamt
	//碰到纯数字，直接扩到32位再说
	else if(name[0]>47 && name[0]<58)//是数字
	{
		int j=atoi(name.c_str());

		//mfc0,mtc0指令中的sel字段占3位
		if(id==14 || id==15)
		{
			bitset<3> bb(j);
			operand.push_back(bb.to_string());
		}

		//sll,srl,sra这3个移位指令中的数字为shamt，占5位
		if(id == 21 || id == 22 || id == 23)
		{
			bitset<5> bb(j);
			operand.push_back(bb.to_string());
		}

		//所有I型指令中的数字均为16位
		else if(id>=32 && id<=55)
		{
			bitset<16> bb(j);
			operand.push_back(bb.to_string());
		}

		//其余的如跳转指令，中断指令等的均为26位
		else if(id == 56 || id == 57)
		{
			j/=4;
			bitset<26> bb(j);
			operand.push_back(bb.to_string());
		}
		else//break或者syscall的处理方法
		{

		}
	}

	else//可能是变量名或者标号名
	{
		itofpara=para_info.begin();
		for(;itofpara!=para_info.end();itofpara++)
		{
			if(name==itofpara->name)//找到了变量名，则返回其对应的数值的16位表示
			{
				string temp = itofpara->p_data[0];
				long num = strtol(temp.c_str(),NULL,2);

				if(id == 56 ||id == 57)//j或者jal要返回26位地址
				{
					bitset<26> bb(num/4);
					temp = bb.to_string();
				}
				else
				{
					bitset<16> bb(num);
					temp = bb.to_string();
				}
				operand.push_back(temp);
				break;
			}
		}
		if(itofpara==para_info.end())//不是变量名，可能是标号
		{
			if((itofmap=label.find(name))!=label.end())//是标号
			{
				if(id==56 || id==57)//j或者jal要返回26位地址
				{
					bitset<26> bb((itofmap->second)/4);
					operand.push_back(bb.to_string());//统一返回26位的值
				}
				else if(id >=46 &&id <= 53)		//	beq等指令的offset要单独计算
				{
					int diff = (itofmap->second - p_line*4)/4;
					bitset<16> bb(diff);
					operand.push_back(bb.to_string());
				}
				else//其余用到跳转的地方只要返回16位地址
				{
					bitset<16> bb(itofmap->second);
					operand.push_back(bb.to_string());//统一返回16位的值
				}
			}
			else
			{
				err="操作数的格式有问题！";
				outputerror(err);
			}
		}
	}
}


string to32(string & str)		//将str非符号扩展至32位
{
	string z;
	if(str.length()==0)
		z="00000000000000000000000000000000";
	else if(str.length()==8)
	{
		z="000000000000000000000000";
		z+=str;
	}
	else if(str.length()==16)
	{
		z="0000000000000000";
		z+=str;
	}
	else if(str.length()==24)
	{
		z="00000000";
		z+=str;
	}
	else
	{
		z="";
		z+=str;
	}
	str=z;
	return str;
}



void write_data()
{
		//struct data_info				//存储数据段信息
	//{
	//	string info;				//记录数据段每行的信息
	//	int line;					//记录该行在代码中的绝对地址用于错误检测与定位
	//};


	/*vector<parameter>				//para_info中存放所有数据的信息
	struct parameter
	{
		int line;					//数据所在数据段的行号
		int address;				//数据地址,指排序完后的相对地址
		string name;				//变量名称
		vector<string> p_data;		//存储变量的二进制形式，当该变量存储了多个数据时，一次加进去
		int p_type;					//变量类型
	};

	vector<data_info> dataseg;			//将数据段的每一行记录下来以便分析变量
	vector<parameter> para_info;		//存放所有变量的信息
	*/

	cout<<"开始写入RAM..."<<endl;

	ram<<"memory_initialization_radix = 2;"<<endl;
	ram<<"memory_initialization_vector ="<<endl;
	ram0<<"memory_initialization_radix = 2;"<<endl;
	ram0<<"memory_initialization_vector ="<<endl;
	ram1<<"memory_initialization_radix = 2;"<<endl;
	ram1<<"memory_initialization_vector ="<<endl;
	ram2<<"memory_initialization_radix = 2;"<<endl;
	ram2<<"memory_initialization_vector ="<<endl;
	ram3<<"memory_initialization_radix = 2;"<<endl;
	ram3<<"memory_initialization_vector ="<<endl;

	int line_ram = 0;				//记录ram的行数
	int line_ram0 = 0;
	int line_ram1 = 0;
	int line_ram2 = 0;
	int line_ram3 = 0;

	int mem = 0;					//单ROM，4RAM中用于记录当前写入第几个RAM
	//int row = 0;					//根据变量的基址和offset可得其所在RAM中的行号
	int column = 0;					//根据变量的基址和offset可得其所在RAM中的列号，这里我把一行（32位）按照8位一列分成4列
	string line = "";				//单RAM模式中，每行有4个字节的数据，将其存到line中，若满32位，则写入一行
	int size = 0;					//一个数据所占的字节数
	for(itofpara = para_info.begin();itofpara != para_info.end();itofpara++)
	{//对于每一个变量，逐个遍历存储
		if(itofpara->p_type == 1)
		{
			size = 1;
		}
		if(itofpara->p_type == 2)
		{
			size = 2;
		}
		if(itofpara->p_type == 3)
		{
			size = 4;
		}
		for(it = itofpara->p_data.begin();it != itofpara->p_data.end();it++)
		{//根据数据类型对数据进行分解，一个字节一个字节的取出来并存储到coe中，注意最低字节在itofpara->p_data的最右侧
			for(int i = 0; i < size; i++)
			{
				int start_pos = (*it).length()-8*(i+1);//第i个字节开始的位置
				string temp = (*it).substr(start_pos,8);//取出第i个字节


				/*先写入4RAM的*/
				if(mem == 0)
				{
					ram0<<temp<<","<<endl;					//写入第0号RAM
					line_ram0++;
				}
				if(mem == 1)
				{
					ram1<<temp<<","<<endl;					//写入第1号RAM
					line_ram1++;
				}
				if(mem == 2)
				{
					ram2<<temp<<","<<endl;					//写入第2号RAM
					line_ram2++;
				}
				if(mem == 3)
				{
					ram3<<temp<<","<<endl;					//写入第3号RAM
					line_ram3++;
				}

				mem++;									//写完一个RAM，将mem指向下一个
				if(mem == 4)
					mem = 0;

				/*下面写入单RAM的*/
				if(column < 3)							//该行数据加上temp也不足32位
				{
					temp = temp+line;					//新出现的字节要存在高位
					line = temp;
					column ++;
				}
				else//column = 3 意味着该行数据加上temp达到32位，因此可以输出一行
				{
					temp = temp+line;						//新出现的字节要存在高位
					line = temp;
					line = line+",";
					ram<<line<<endl;
					line_ram++;

					line = "";								//输出后将line清空以待下一行数据写入
					column = 0;
				}


			}//数据内的一个字节处理完毕

		}//变量内的一个数据处理完毕

	}//一个变量处理完毕

	int ramline = pow(2,15);


	for(int i = line_ram0;i<ramline-1;i++)
		ram0<<"00000000,"<<endl;
	ram0 << "00000000;";

	for(int i = line_ram1;i<ramline-1;i++)
		ram1<<"00000000,"<<endl;
	ram1 << "00000000;";

	for(int i = line_ram2;i<ramline-1;i++)
		ram2<<"00000000,"<<endl;
	ram2 << "00000000;";

	for(int i = line_ram3;i<ramline-1;i++)
		ram3<<"00000000,"<<endl;
	ram3 << "00000000;";

	//最后一个数据处理完后，单RAM最后一行可能因为不足32位有未输出的数据
	if(line.length()==0)					//上一行正好输出完，没有残留的数据要输出
	{
	}
	else//还有残留的一行数据要输出
	{
		line = to32(line);
		ram<<line<<","<<endl;
		line_ram++;
	}
	
	for(int i = line_ram;i<ramline-1;i++)
		ram<<"00000000000000000000000000000000,"<<endl;
	ram << "00000000000000000000000000000000;";

	cout<<"哦也！RAM写入完成!"<<endl<<endl;

}



void outputerror(string err)
{
	cout<<"出错行数："<<total_line<<"行!"<<err<<endl;
	//prog<<"出错行数："<<total_line<<"行!"<<err<<endl;
	exit(1);
}