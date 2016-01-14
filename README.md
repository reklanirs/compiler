# compiler

beta2.2 版本,暂时修改了所有已发现错误.

数据类型支持int, unsigned int, short, unsigned short, char, unsigned char

支持指针

## 懒人版文档:

假如没有时间看[详细文档](https://github.com/reklanirs/compiler/blob/master/%E7%BC%96%E8%AF%91%E5%99%A8%E7%9A%84%E8%82%B2%E6%88%90%E6%89%8B%E5%86%8C.md), 这里是这个编译器如何使用的简单说明.



主程序为compiler.py, (compiler_alpha.py顾名思义是alpha版, 现在已经不能使用.)

输入为工作目录(windows下就是同目录)下的input.c,输入文件的对应目标语言为MiniC,简单来说是阉割版C语言,具体需要看yqs在群里上传的[最新版](https://github.com/reklanirs/compiler/blob/master/Mini%20C%E7%9A%84%E6%9C%89%E5%85%B3%E8%A7%84%E5%AE%9A%EF%BC%8815%E5%B9%B410%E6%9C%88%E4%BF%AE%E8%AE%A2%E7%89%88%EF%BC%89.pdf)文档. 

编译器首先读入代码并格式化(包括去单行/多行注释, 加{}号, 去;号, 分行, 替换变量格式标识符,替换一元右结合运算符号), 格式化的结果输出为code1.txt

之后利用格式化过的代码进行翻译成汇编代码, 输出为code.asm. 中间检测到的错误会被抛出, 即error.txt记录了所有**格式化过的**错误代码行. 所以如果要定位错误行, 需要将 *源代码 — 格式化后的代码 — 错误代码行* 三者匹配.