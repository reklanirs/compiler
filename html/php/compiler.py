#!/usr/bin/python2.7
# -*- coding: utf-8 -*- import requests
import os
import re
import sys
import math
import time
from random import choice
reload(sys)

sizetotype = {8:'.byte', 16:'.half', 32:'.word'}
variable_types = ['sint08','sint16','sint32','uint08','uint16','uint32']
types = variable_types + ['void00']
control_word = ['for', 'while', 'break', 'continue', 'if', 'else', 'return']
reserved_word = types + control_word
register_name = ['$zero','$at','$gp','$sp','$s8','$fp','$ra']
for i in range(2):
	register_name.append('$v' + str(i))
for i in range(4):
	register_name.append('$a' + str(i))
for i in range(10):
	register_name.append('$t' + str(i))
for i in range(8):
	register_name.append('$s' + str(i))
for i in range(2):
	register_name.append('$k' + str(i))

priority = {
'#':10000,
'!':2, '~':2, '`':2,'$':2,
'*':4, '/':4, '%':4,
'+':5, '-':5,
'<<':6, '>>':6,
'<':7, '<=':7, '>':7, '>=':7,
'==':8, '!=':8,
'&':9,
'^':10,
'|':11,
'&&':12,
'||':13,
'=':15
}
assign_operation = {
'=':15
}
operation_units = {
'#':0,
'!':1, '~':1, '`':1,'$':1,
'*':2, '/':2, '%':2,
'+':2, '-':2,
'<<':2, '>>':2,
'<':2, '<=':2, '>':2, '>=':2,
'==':2, '!=':2,
'&':2,
'^':2,
'|':2,
'&&':2,
'||':2,
'=':2
}

codes = []
raw = []
error_strings = []
outputcode = open('code.asm','w')
errorlog = open('error.txt','w')

prefix_global = 'Global_'		
globalVarList = []
globalVarDict = {}
globalArray = []
globalCodes = []

functionNameList = []
functionDict = {}
functionReturnType = {}	
functions = []

def swap(a,b):
	return b,a

def throw_error(s):
	global error_strings
	error_strings.append(s)
	return 1
def output(s):
	outputcode.write(s)
	outputcode.flush()
def outputln(s):
	outputcode.write(str(s))
	outputcode.write('\n')
	outputcode.flush()



class Variable(object):
	"""docstring for Variable
		type: 0 : register;   1 : RAM,单独变量,占1单元  n(>1):数组,占n单元
	"""
	def __init__(self, name, vartp):
		super(Variable, self).__init__()
		self.name = name #name in function
		self.vtype = vartp  #variable_types = ['sint08','sint16','sint32','uint08','uint16','uint32']
		self.sizeof = int(vartp[4:6])  # 8, 16, 32
		
		self.corname = '' # name in .code or register name
		self.type = 0    # 0 : register;   1 : RAM单独变量  n(>1):数组,占n单元

	def generatecode(self):
		if self.type == 1:
			tmp = 32/self.sizeof
			ret = self.corname + ' ' + sizetotype[self.sizeof]
			for i in range(tmp):
				ret += ' 0'   #32对齐
			return 1,ret
		elif self.type > 1:
			cursize = self.sizeof * self.type
			while cursize % 32 != 0:
				cursize += self.sizeof
			num = cursize / 32
			tmp = cursize / self.sizeof
			ret = self.corname + ' ' + sizetotype[self.sizeof]
			for i in range(tmp):
				ret += ' 0'   #32对齐
			return num,ret
		else:
			return 0,''



class Function(object):
	def __init__(self, codes):
		super(Function, self).__init__()
		self.codes = []
		for i in codes:
			i = i.strip()
			if i != '':
				self.codes.append(i)

		tmp = self.codes[0]
		name = tmp[tmp.find(' ')+1:tmp.find('(')].strip()
		if name == '':
			throw_error(codes[0])
		varstring = tmp[tmp.find('(')+1:tmp.rfind(')')].strip()

		self.name = name
		self.prefix = name + '_'
		self.vtype = self.codes[0][:6]  #variable_types = ['void00','sint08','sint16','sint32','uint08','uint16','uint32']
		self.sizeof = int(self.vtype[4:6])  # 8, 16, 32
		self.params = []

		self.vardict = {}	# varname -> varclass

		if varstring != '' and varstring not in types:
			for x in varstring.split(','):
				x = x.strip()
				tp = x[:6].strip()
				name = x[6:].strip()
				if name.find('[') != -1:
					name = name[:name.find('[')].strip()
				self.params.append((name,tp))	

		self.head = codes[0]
		self.vardeclaration = []
		self.realcode = []
		for i in range(2,len(codes)):
			s = self.codes[i]
			if len(s) >= 8 and s[:6] in variable_types and s[6] == ' ':
				continue
			else:
				self.vardeclaration = codes[2:i]
				self.realcode = codes[i:-1]
				break
	def printcode(self):
		outputln(self.name + '_begin:')
		availableVars = self.vardict.copy()
		for i,j in globalVarDict.items():
			if i not in self.varlist:
				availableVars[i] = j

		dealCodes(self.name, self.realcode, availableVars, 0)
		rassignr('$v0', '$zero')
		outputln('jr $ra')



def get_parenthesis_content(s):
	if s == '':
		return ''
	num = 1
	loc = 1
	for i in s[1:]:
		if i == '(':
			num += 1
		elif i == ')':
			num -= 1
		if num == 0:
			break
		loc += 1
	ret = s[:loc + 1]
	s = s[loc + 1:].strip()
	return (ret, s)



def extract_a_part(s, ret):
	if s[0] == '{' or s[0] == '}':
		ret.append(s[0])
		s = s[1:].strip()
		return s
	if s == '':
		return ''
	if re.match('(unsigned )?char |(unsigned )?short |(unsigned )?int ',s):
		replace = ''
		if s[0] == 'u':
			replace += 'u'
			s = s[9:]
		else:
			replace += 's'
		replace += 'int'
		if s[:4] == 'char':
			replace += '08'
			s = s[4:]
		elif s[:5] == 'short':
			replace += '16'
			s = s[5:]
		elif s[:3] == 'int':
			replace += '32'
			s = s[3:]
		s = replace + s
	if re.match('\\bvoid\\b',s):
		s = 'void00' + s[4:]

	if len(s)>=8 and s[:6] in types:
		i = 6
		while i < len(s) and ( s[i] == ' ' or s[i] == '\t' ):
			i += 1
		if i < len(s) and s[i] == '*':
			s = s[:i] + s[i+1:]

	if s[:3] == 'for':
		s = s[3:].strip()
		tmp = get_parenthesis_content(s)
		ret.append('for' + tmp[0])
		s = tmp[1]
		if s[0] != '{':
			ret.append('{')
			s = extract_a_part(s, ret)
			ret.append('}')
		return s
	if s[:5] == 'while':
		s = s[5:].strip()
		tmp = get_parenthesis_content(s)
		ret.append('while' + tmp[0])
		s = tmp[1]
		if s[0] != '{':
			ret.append('{')
			s = extract_a_part(s, ret)
			ret.append('}')
		return s
	elif s[:2] == 'if':
		s = s[2:].strip()
		tmp = get_parenthesis_content(s)
		ret.append('if' + tmp[0])
		s = tmp[1]
		if s[0] != '{':
			ret.append('{')
			s = extract_a_part(s, ret)
			ret.append('}')

		if re.match('else[ \\t]+if',s):
			tmp = s.find('if')
			s = s[tmp:]
			ret.append('else')
			ret.append('{')
			s = extract_a_part(s,ret)
			ret.append('}')
		elif s[:4] == 'else':
			s = s[4:].strip()
			ret.append('else')
			if s[0] != '{':
				ret.append('{')
				s = extract_a_part(s, ret)
				ret.append('}')
		return s

	i = 0
	for c in s:
		if c == ';':
			if i:
				ret.append(s[:i])
			s = s[i + 1:]
			break
		elif c == '{' or c == '}':
			if i:
				ret.append(s[:i])
			ret.append(s[i])
			s = s[i + 1:]
			break
		i += 1
	return s



def init_input():
	sys.stdin = open('input.c', 'r')
	ret = []
	global raw
	ss = ''
	while True:
		try:
			s = raw_input().strip()
			raw.append(s)
			loc = s.find('//')
			if loc != -1:
				s = s[:loc].strip()
			ss += s
		except EOFError:
			break

	while True:
		loc = ss.find('/*')
		if loc == -1:
			break
		pre = ss[:loc]
		aft = ss[loc + 2:]
		loc = aft.find('*/')
		if loc != -1:
			aft = aft[loc + 2:]
		else:
			aft = []
		ss = pre + aft

	tmp = re.search('\\bvoid\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'void00' + ss[j:]
		tmp = re.search('\\bvoid\\b',ss)

	tmp = re.search('\\bunsigned char\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'uint08' + ss[j:]
		tmp = re.search('\\bunsigned char\\b',ss)

	tmp = re.search('\\bunsigned short\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'uint16' + ss[j:]
		tmp = re.search('\\bunsigned short\\b',ss)

	tmp = re.search('\\bunsigned int\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'uint32' + ss[j:]
		tmp = re.search('\\bunsigned int\\b',ss)

	tmp = re.search('\\bchar\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'sint08' + ss[j:]
		tmp = re.search('\\bchar\\b',ss)

	tmp = re.search('\\bshort\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'sint16' + ss[j:]
		tmp = re.search('\\bshort\\b',ss)

	tmp = re.search('\\bint\\b',ss)
	while tmp:
		i,j = tmp.span()
		ss = ss[:i] + 'sint32' + ss[j:]
		tmp = re.search('\\bint\\b',ss)

	while len(ss) > 0:
		# for while if else
		ss = extract_a_part(ss, ret)
	return ret



def findCurlyContent(codes):
	start = -1
	ret = []
	for i in range(len(codes)):
		if codes[i].strip() == '{':
			start = i
			break
	if start == -1 or start >= len(codes) - 2:
		return [],0
	num = 1
	end = start + 1
	for s in codes[start+1:]:
		if s.strip() == '{':
			num += 1
		elif s.strip() == '}':
			num -= 1
			if num == 0:
				return codes[start + 1:end],end
		end += 1
	return codes[start + 1:],len(codes) - 1
	pass			



#@call		var_definition_code,  all_code_in_the_function
#@return	variable_list(name,type)  variable_appear_num{(name,type):num}  array_list(name,type)
def scanVarible(codes, allcodes):
	variables = []
	appearNum = {}
	array = []
	
	l = []
	if len(codes) == 0:
		return variables,appearNum,array
	if len(codes[0]) >= 8 and codes[0][:6] in types and re.match('[ \\t]+[A-Za-z][A-Za-z0-9]*\\(.*\\)',codes[0][6:]):
		l = codes[1:]
		tmp = codes[0].strip()
		name = tmp[tmp.find('(')+1:tmp.rfind(')')].strip()
		if name not in types and name != '':
			for s in name.split(','):
				s = s.strip()
				tp = s[:6]
				name = s[6:].strip()
				if name.find('[') != -1:
					array.append((name,tp))
				else:
					variables.append((name,tp))
	else:
		l = codes

	for s in l:
		s = s.strip()
		if s == '' or s == '{':
			continue

		tp = s[:6]
		name = s[6:].strip()

		if '(' in s or '}' in s or s[:s.find(' ')].strip() not in variable_types:
			break
		if name == '':
			throw_error(s)
		tmp = re.match('[A-Za-z][A-Za-z0-9]*[ \\t]*([ \\t]*,[ \\t]*([A-Za-z][A-Za-z0-9]*))*[ \\t]*;?',name)
		if tmp == None or tmp.span()[1] != len(s):
			if name.find('[') == -1:
				throw_error(s)

		tmp = [x.strip() for x in name.split(',')]
		for v in tmp:
			if v in reserved_word:
				throw_error(s)
				break
			elif v.find('[') == -1:
				variables.append((v,tp))
			else:
				array.append((v,tp))

	for name,tp in variables:
		appearNum[(name,tp)] = 0
		for s in codes:
			s = s.strip()
			tmp = '\\b' + name + '\\b'
			if re.search(tmp,s):
				appearNum[(name,tp)] += 1
	return variables,appearNum,array




def dealCodes(funcname, codes, corvar, loopnum):
	dealedLineNum = -1
	for linenum in range(len(codes)):
		if linenum <= dealedLineNum:
			continue
		s = codes[linenum].strip()
		if s == '{' or s == '}' or s == '':
			continue

		if re.match('^return\\b',s):
			tmp = s[6:].strip()
			if tmp != '':
				dealExpression(tmp, '$v0', funcname, corvar)
			outputln('jr $ra')
			pass
		elif re.match('^while(.+)$',s):
			beginLabel = funcname + '_while_start_' + str(loopnum)
			endLabel = funcname + '_while_end_' + str(loopnum)
			loopnum += 1
			state = s[s.find('(')+1:s.rfind(')')].strip()
			body,curnum = findCurlyContent(codes[linenum:])
			dealedLineNum = linenum + curnum
			if state == '':
				throw_error(s)
				continue

			outputln(beginLabel + ':')
			dealExpression(state,'$t0', funcname, corvar)
			outputln('beq $t0,$zero,' + endLabel)

			dealCodes(funcname, body, corvar, loopnum)

			outputln('j ' + beginLabel)
			outputln(endLabel + ':')
			outputln('')
			pass
		elif re.match('^if(.+)$',s):
			ifbeginLabel = funcname + '_if_start_' + str(loopnum)
			ifendLabel = funcname + '_if_end_' + str(loopnum)
			allendLabel = funcname + '_ifelse_end_' + str(loopnum)
			state = s[s.find('(')+1:s.rfind(')')].strip()
			body,curnum = findCurlyContent(codes[linenum:])
			dealedLineNum = linenum + curnum
			if state == '':
				throw_error(s)
				continue

			outputln(ifbeginLabel + ':')
			dealExpression(state,'$t0', funcname, corvar)
			outputln('beq $t0,$zero,' + ifendLabel)

			dealCodes(funcname, body, corvar, loopnum)
			outputln('j ' + allendLabel)
			outputln(ifendLabel + ':')
			outputln('')

			if dealedLineNum + 1 < len(codes) and codes[dealedLineNum + 1].strip() == 'else':
				elsebeginLabel = funcname + '_else_start_' + str(loopnum)
				elseendLabel = funcname + '_else_end_' + str(loopnum)
				body,curnum = findCurlyContent(codes[dealedLineNum + 1:])
				dealedLineNum += curnum
				if state == '':
					throw_error(s)
					continue

				outputln(elsebeginLabel + ':')

				dealCodes(funcname, body, corvar, loopnum + 1)

				outputln(elseendLabel + ':')

			outputln(allendLabel + ':')
			outputln('')
			loopnum += 1
			pass
		elif re.match('^for(.*;.*;.*)$',s):
			pass
		else:# 赋值语句或单条表达式或函数
			dealExpression(s,'$v1', funcname, corvar)
			pass



#@call  string prefuncname, corvar
#@return  (a_part, left_string, part_type,  part_vtype)
def readapart(s, prefuncname, corvar):
	s = s.strip()
	if s == '' or s == '(' or s == ')':
		return s[0:1].strip(),s[1:].strip(),'parenthesis','NoVtype'
	if re.match('[0-9]+\\b|0x[0-9a-fA-F]+\\b',s):
		tmp = re.match('[-+]?[0-9]+\\b|[-+]?0x[0-9a-fA-F]+\\b',s).span()[1]
		return s[:tmp],s[tmp:].strip(),'const','sint32'
	elif re.match('[A-Za-z][A-Za-z0-9]*',s):
		tmp = re.match('[A-Za-z][A-Za-z0-9]*',s).span()[1]
		if tmp < len(s) and s[tmp] == '(':
			num = 1
			for i in range(tmp+1, len(s)):
				if s[i] == '(':
					num += 1
				elif s[i] == ')':
					num -= 1
					if num == 0:
						fname = s[:tmp].strip()
						return s[:i+1],s[i+1:].strip(),'function',functionReturnType[fname]
		elif tmp < len(s) and s[tmp] == '[':
			num = 1
			for i in range(tmp+1, len(s)):
				if s[i] == '[':
					num += 1
				elif s[i] == ']':
					num -= 1
					if num == 0:
						arrayName = s[:tmp].strip()
						return s[:i+1],s[i+1:].strip(),'array',arrayName
		else:
			vname = s[:tmp].strip()
			return vname,s[tmp:].strip(),'variable',corvar[vname].vtype
	elif s[0] in '+-*/=<>!&|^~$':
		i = 0
		while i < len(s) and s[:i+1] in priority:
			i += 1
		return s[:i].strip(),s[i:].strip(),'symbol','NoVtype'
	else:
		return s,'','NoType','NoVtype'


#@call  string, prefuncname, corvar
#@return  [(a_part, left_string, part_type,  part_vtype)]
def toParts(s, prefuncname, corvar):
	ret = []
	while len(s) > 0:
		tmp = readapart(s, prefuncname, corvar)
		ret.append((tmp[0],tmp[2],tmp[3]))
		s = tmp[1].strip()
	for i in range(len(ret)):
		if ret[i][0] == '-':
			if i == 0 or ret[i-1][0] == '(' or ret[i-1][1] == 'symbol':
				ret[i] = ('`',ret[i][1],ret[i][2])
		if ret[i][0] == '+':
			if i == 0 or ret[i-1][0] == '(' or ret[i-1][1] == 'symbol':
				ret = ret[:i] + ret[i+1:]
		if ret[i][0] == '*':
			if i == 0 or ret[i-1][0] == '(' or ret[i-1][1] == 'symbol':
				ret[i] = ('$',ret[i][1],ret[i][2])
	return ret



def midToSuffix(l):##
	ret = []
	symbols = [('#','NoType','NoVtype')]
	for i,tp,vtype in l:
		if tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
			ret.append((i,tp,vtype))
		elif i == '(':
			symbols.append((i,tp,vtype))
		elif i == ')':
			tmp = symbols.pop()
			while tmp[0] != '(':
				ret.append(tmp)
				tmp = symbols.pop()
		else:
			if symbols[-1][0] == '(':
				symbols.append((i,tp,vtype))
			elif priority[i] <= priority[symbols[-1][0]]:
				symbols.append((i,tp,vtype))
			else:
				while priority[i] > priority[symbols[-1][0]]:
					ret.append(symbols.pop())
				symbols.append((i,tp,vtype))
	for tmp in reversed(symbols[1:]):
		ret.append(tmp)
	return ret



def readArrayInData(reg, realArrayName, arrayType, i):
	tmpReg = ''
	if type(i) == int:
		outputln('ori $t0,$zero,%d'%(i))
		tmpReg = '$t0'
	elif type(i) == str and i in register_name:
		tmpReg = i

	if arrayType == 'sint08':
		outputln('lb %s,%s(%s)'%(reg, realArrayName, tmpReg))
	elif arrayType == 'uint08':
		outputln('lbu %s,%s(%s)'%(reg, realArrayName, tmpReg ))
	elif arrayType == 'sint16':
		outputln('sll %s,%s,1'%(tmpReg,tmpReg))
		outputln('lh %s,%s(%s)'%(reg, realArrayName, tmpReg ))
	elif arrayType == 'uint16':
		outputln('sll %s,%s,1'%(tmpReg,tmpReg))
		outputln('lhu %s,%s(%s)'%(reg, realArrayName, tmpReg ))
	else:
		outputln('sll %s,%s,2'%(tmpReg,tmpReg))
		outputln('lw %s,%s(%s)'%(reg, realArrayName, tmpReg ))

def saveToArrayInData(reg, realArrayName, arrayType, i):
	tmpReg = ''
	if type(i) == int:
		if i == 0:
			tmpReg = '$zero'
		else:
			outputln('ori $t0,$zero,%d'%(i))
			tmpReg = '$t0'
	elif types(i) == str and i in register_name:
		tmpReg = i

	if arrayType == 'sint08' or arrayType == 'uint08':
		outputln('sb %s,%s(%s)'%(reg, realArrayName, tmpReg))
	elif arrayType == 'sint16' or arrayType == 'uint16':
		if not (type(i) == int and i == 0):
			outputln('sll %s,%s,1'%(tmpReg,tmpReg))
		outputln('sh %s,%s(%s)'%(reg, realArrayName, tmpReg ))
	else:
		if not (type(i) == int and i == 0):
			outputln('sll %s,%s,2'%(tmpReg,tmpReg))
		outputln('sw %s,%s(%s)'%(reg, realArrayName, tmpReg ))


#@return  successful
def rassignr(regto, regfrom):
	if regto not in register_name or regfrom not in register_name:
		return False
	outputln('ori %s,%s,0'%(regto,regfrom))
	return True


#@return  successful
def rassignrWithStyle(regto, regfrom, toVstyle = 'sint32', fromVstyle = 'sint32'):
	#variable_types = ['sint08','sint16','sint32','uint08','uint16','uint32']
	if regto not in register_name or regfrom not in register_name or toVstyle not in variable_types or fromVstyle not in variable_types:
		return False
	tosize = int(toVstyle[-2:])
	fromsize = int(fromVstyle[-2:])
	if toVstyle == fromVstyle:
		outputln('ori %s,%s,0'%(regto,regfrom))
	elif tosize >= fromsize and toVstyle[0] == fromVstyle[0]:
		outputln('ori %s,%s,0'%(regto,regfrom))
	elif tosize < fromsize and toVstyle[0] == 's' and fromVstyle[0] == 's': ## s<-s, 填充符号位
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('sra %s,%s,%d'%(regto,regto,num))
		pass ##
	elif tosize < fromsize and toVstyle[0] == 'u' and fromVstyle[0] == 'u': #填0	u<-u, 填充0
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('srl %s,%s,%d'%(regto,regto,num))
	elif toVstyle == 's' and fromVstyle[0] == 'u' and toVstyle[-2:] == fromVstyle[-2:]: #s<-u
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('sra %s,%s,%d'%(regto,regto,num))
	elif toVstyle == 'u' and fromVstyle[0] == 's' and toVstyle[-2:] == fromVstyle[-2:]: #u<-s
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('srl %s,%s,%d'%(regto,regto,num))
	elif toVstyle == 's' and fromVstyle[0] == 'u' and int(toVstyle[-2:]) > int(fromVstyle[-2:]): #s<-u
		outputln('ori %s,%s,0'%(regto,regfrom))
	elif toVstyle == 's' and fromVstyle[0] == 'u' and int(toVstyle[-2:]) < int(fromVstyle[-2:]): #s<-u
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('sra %s,%s,%d'%(regto,regto,num))
	elif toVstyle == 'u' and fromVstyle[0] == 's' and toVstyle[-2:] > fromVstyle[-2:]: #u<-s
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('sra %s,%s,%d'%(regto,regto,num))
	elif toVstyle == 'u' and fromVstyle[0] == 's' and toVstyle[-2:] < fromVstyle[-2:]: #u<-s
		outputln('ori %s,%s,0'%(regto,regfrom))
		num = 32 - int(toVstyle[-2:])
		outputln('sll %s,%s,%d'%(regto,regto,num))
		outputln('srl %s,%s,%d'%(regto,regto,num))
	pass



def regFormat(reg, vtype):
	if vtype[0] == 'u':
		num = 32 - int(vtype[-2:])
		outputln('sll %s,%s,%d'%(reg,reg,num))
		outputln('srl %s,%s,%d'%(reg,reg,num))
	elif vtype[0] == 's':
		num = 32 - int(vtype[-2:])
		outputln('sll %s,%s,%d'%(reg,reg,num))
		outputln('sra %s,%s,%d'%(reg,reg,num))


#@return  successful
def assignr(x , reg, prefuncname, corvar):
	# x[0] : name x[1] : type   x[2] : variable_type (['void00','sint08','sint16','sint32','uint08','uint16','uint32'])
	# tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
	name = x[0]
	tp = x[1]
	vtype = x[2]
	print 'assignr ',x,reg
	if tp == 'function':
		##prefuncname 调用  x[0] : funcName
		##corvar      ---  functionDict[funcName].vardict
		##params      传给  functionDict[funcName].params
		funcName = name[:name.find('(')].strip()
		params = name[name.find('(')+1:name.rfind(')')].strip()
		if params == '':
			params = []
		else:
			params = [x.strip() for x in params.split(',')]
		
		if len(params) != len(functionDict[funcName].params):
			throw_error(x)
			return False

		outputln('PUSHA ##'+prefuncname)
		f = functionDict[funcName]
		##开始传参数
		for i in range(len(params)):
			here = params[i]
			aimName, aimVarType = f.params[i]
			aimRealName = f.vardict[aimName].corname
			if here in corvar and corvar[here].type > 1 and f.vardict[aimName].type > 1: #传递整个数组
				hereRealName = corvar[here].corname
				hereVarType = corvar[here].vtype
				size = f.vardict[aimName].type
				for i in range(size):
					readArrayInData('$a0',hereRealName,hereVarType,i)
					saveToArrayInData('$a0',aimRealName,aimVarType,i)
				continue
			elif here in corvar and corvar[here].type > 1 or f.vardict[aimName].type > 1:
				throw_error(x)
				rassignr(reg, '$zero')
				return
			else: ##aim 必定是0 - reg, 1 - 内存的单个变量两种形式;
				ansvtype = dealExpression(here, '$a0', prefuncname, corvar)
				var = f.vardict[aimName]
				if var.type == 0:
					rassignrWithStyle(aimRealName, '$a0', var.vtype, ansvtype)
				elif var.type == 1:
					saveToArrayInData('$a0', aimRealName, aimVarType, 0)
		#函数执行
		outputln('jal ' + funcName)
		#执行结束, 结果存于$v0

		outputln('POPA ##' + prefuncname) #PUSHA, POPA操作不能动$v的值
		rassignr('$v0', reg)
	elif tp == 'array':
		arrayName = x[0][:name.find('[')].strip()
		param = name[name.find('[')+1:name.rfind(']')].strip()
		dealExpression(param, '$a0', prefuncname, corvar)
		realArrayName = corvar[arrayName].corname
		readArrayInData(reg, realArrayName, vtype, '$a0')
	elif tp == 'const': # vtype must be sint32
		exp = x[0]
		realnum = 0
		if exp.find('x') != -1:
			realnum = int(exp,16)
		else:
			realnum = int(exp,10)
		if abs(realnum) <= 65535:
			outputln('ori %s,$zero,%s'%(reg,exp))
		else:
			pre = (realnum >> 16) & ((1 << 16) - 1)
			aft = realnum & ((1 << 16) - 1)
			pres = ''
			afts = ''
			for i in range(16):
				pres += str(pre & 1)
				pre >>= 1
				afts += str(aft & 1)
				aft >>= 1
			pre = int(pres[::-1],2)
			aft = int(afts[::-1],2)
			outputln('ori %s,$zero,%d'%(reg,pre))
			outputln('sll %s,%s,16'%(reg,reg))
			outputln('ori %s,$zero,%d'%(reg,aft))
	elif tp == 'variable':
		var = corvar[name]
		if var.type == 0:
			rassignr(reg, var.corname)
		elif var.type == 1:
			if vtype == 'sint08':
				outputln('lb %s,%s(%s)'%(reg, var.corname, '$zero'))
				pass
			elif vtype == 'uint08':
				outputln('lbu %s,%s(%s)'%(reg, var.corname, '$zero' ))
				pass
			elif vtype == 'sint16':
				outputln('lh %s,%s(%s)'%(reg, var.corname, '$zero' ))
				pass
			elif vtype == 'uint16':
				outputln('lhu %s,%s(%s)'%(reg, var.corname, '$zero' ))
				pass
			elif vtype.strip() == 'sint32' or vtype.strip() == 'uint32':
				outputln('lw %s,%s(%s)'%(reg, var.corname, '$zero'))
			else:
				throw_error(name)
				rassignr(reg, '$zero')
			pass
	elif tp == 'port':
		var = corvar[name]
		if var.type == 0:
			outputln('lw %s,0(%s)'%(reg,var.corname))
		elif var.type == 1:
			outputln('lw $t0,%s($zero)'%(var.corname))
			outputln('lw %s,0(%s)'%(reg, '$t0'))

	else:
		throw_error(x[0])
		rassignr(reg, '$zero')
	pass


#@return  vtype of the ans
def rassign((reg,regvtype), (name, tp, vtype) , prefuncname, corvar):
	#x : (name, tp, vtype)  tp must in ['array'(a[num]) ,  'variable', 'port']
	if tp == 'array':
		arrayName = name[:name.find('[')].strip()
		num = num[name.find('[')+1:name.rfind(']')].strip()
		dealExpression(num, '$a0', prefuncname, corvar)
		saveToArrayInData(reg, corvar[name].corname, vtype, '$a0')
	elif tp == 'variable':
		var = corvar[name]
		realName = var.corname
		if var.type == 0:
			rassignrWithType(var.corname, reg, var.vtype, regvtype)
		elif var.type == 1:
			saveToArrayInData(reg, var.corname, var.vtype, 0)
		else:
			return ''
	elif tp == 'port': ##
		var = corvar[name]
		if var.type == 0:
			outputln('sw %s,0(%s)'%(reg,var.corname))
		elif var.type == 1:
			outputln('lw $t0,%s($zero)'%(var.corname))
			outputln('sw %s,0(%s)'%(reg, '$t0'))
	else:
		return ''
	return vtype


#@return  vtype of the ans
def calc1((reg,vtype), oper, savereg):
	ansvtype = ''
	if oper == '!':
		outputln('ori %s,$zero,1'%(savereg))
		outputln('beq %s,$zero,1'%(reg))
		outputln('ori %s,$zero,0'%(savereg))
		ansvtype = vtype
	elif oper == '~':
		outputln('nor %s,%s,%s'%(savereg,reg,reg))
		regFormat(savereg, vtype)
		ansvtype = vtype
	elif oper == '`':
		ansvtype = 's' + vtype[1:]
		outputln('nor %s,%s,%s'%(savereg,reg,reg))
		outputln('addi %s,%s,1'%(savereg,savereg))
		regFormat(savereg, ansvtype)
	else:
		rassignr(savereg,'$zero')
		ansvtype = ''
	return ansvtype


#@return  vtype of the ans
def calc2((regl,lvtype), oper, (regr,rvtype), savereg):
	size = max(int(lvtype[-2:]), int(rvtype[-2:]))
	ssize = str(size)
	if oper == '*':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('multu %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'uint' + ssize)
			return 'uint' + ssize
		else:
			outputln('mult %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
	elif oper == '/':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('divu %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'uint' + ssize)
			return 'uint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('div %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,5')##
			#s为正
			outputln('divu %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			outputln('blez $zero,12')##
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regl,regl))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('divu %s,%s'%('$t0',regr))
			outputln('mflo %s'%('$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('nor %s,%s,%s'%(savereg,'$t0','$t0'))
			outputln('addi %s,%s,1'%(savereg,savereg))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,5')##
			#s为正
			outputln('divu %s,%s'%(regl,regr))
			outputln('mflo %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			outputln('blez $zero,12')##
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regr,regr))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('divu %s,%s'%(regl, '$t0'))
			outputln('mflo %s'%('$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('nor %s,%s,%s'%(savereg,'$t0','$t0'))
			outputln('addi %s,%s,1'%(savereg,savereg))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
	elif oper == '%':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('divu %s,%s'%(regl,regr))
			outputln('mfhi %s'%(savereg))
			regFormat(savereg, 'uint' + ssize)
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('div %s,%s'%(regl,regr))
			outputln('mfhi %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,5')##
			#s为正
			outputln('divu %s,%s'%(regl,regr))
			outputln('mfhi %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			outputln('blez $zero,9')##
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regl,regl))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('divu %s,%s'%('$t0',regr))
			outputln('mfhi %s'%('$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('subu %s,%s,$t0'%(savereg, regr))
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,5')##
			#s为正
			outputln('divu %s,%s'%(regl,regr))
			outputln('mfhi %s'%(savereg))
			regFormat(savereg, 'sint' + ssize)
			outputln('blez $zero,9')##
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regr,regr))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('divu %s,%s'%(regl, '$t0'))
			outputln('mfhi %s'%('$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('add %s,%s,$t0'%(savereg, regr))
		return rvtype
	elif oper == '+':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('addu %s,%s,%s'%(savereg, regl, regr))
			return 'uint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('add %s,%s,%s'%(savereg, regl, regr))
			return 'sint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,4')
			#s为正
			outputln('addu %s,%s,%s'%(savereg,regl,regr))
			regFormat(savereg, 'sint' + ssize)
			outputln('blez $zero,7')
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regl,regl))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('subu %s,%s,%s'%(savereg,regr,'$t0'))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
		if lvtype[0] == 'u' and rvtype[0] == 's':
			return calc2((regr,rvtype), oper, (regl,lvtype), savereg)
	elif oper == '-':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('subu %s,%s,%s'%(savereg, regl, regr))
			return 'uint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('sub %s,%s,%s'%(savereg, regl, regr))
			return 'sint' + ssize
		if lvtype[0] == 's' and rvtype[0] == 'u':
			calc2((regr,rvtype), oper, (regl,lvtype), savereg)
			outputln('nor %s,%s,%s'%(savereg,savereg,savereg))
			outputln('addi %s,%s,1'%(savereg,savereg))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,4')
			#s为正
			outputln('subu %s,%s,%s'%(savereg, regl, regr))
			regFormat(savereg, 'sint' + ssize)###前提为subu -1输出111111..
			outputln('blez $zero,7')
			#s为负
			outputln('nor %s,%s,%s'%('$t0',regr,regr))
			outputln('addi %s,%s,1'%('$t0','$t0'))
			regFormat('$t0', 'uint' + ssize)

			outputln('addu %s,%s,%s'%(savereg, regl, '$t0'))
			regFormat(savereg, 'sint' + ssize)
			return 'sint' + ssize
	elif oper == '<<':  #左右移不能为负数
		outputln('sllv %s,%s,%s'%(savereg,regl,regr))
		return lvtype
	elif oper == '>>':  #左右移不能为负数
		if lvtype[0] == 'u':
			outputln('srlv %s,%s,%s'%(savereg,regl,regr))
		if lvtype[0] == 's':
			outputln('srav %s,%s,%s'%(savereg,regl,regr))
		return lvtype 
	elif oper == '<':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('sltu %s,%s,%s'%(savereg, regl, regr))
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('slt %s,%s,%s'%(savereg, regl, regr))
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,2')##
			#s>=0
			outputln('sltu %s,%s,%s'%(savereg, regl, regr))
			outputln('blez $zero,1')
			#s为负
			outputln('ori %s,$zero,1'%(savereg))
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,2')##
			#s>=0
			outputln('sltu %s,%s,%s'%(savereg, regl, regr))
			outputln('blez $zero,1')
			#s<0
			outputln('ori %s,$zero,0'%(savereg))
		return 'sint32'	
	elif oper == '>':
		if lvtype[0] == 'u' and rvtype[0] == 'u':
			outputln('sltu %s,%s,%s'%(savereg, regr, regl))
		if lvtype[0] == 's' and rvtype[0] == 's':
			outputln('slt %s,%s,%s'%(savereg, regr, regl))
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,2')##
			#s>=0
			outputln('sltu %s,%s,%s'%(savereg, regr, regl))
			outputln('blez $zero,1')
			#s为负
			outputln('ori %s,$zero,1'%(savereg))
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,2')##
			#s>=0
			outputln('sltu %s,%s,%s'%(savereg, regr, regl))
			outputln('blez $zero,1')
			#s<0
			outputln('ori %s,$zero,0'%(savereg))
		return 'sint32'
	elif oper == '<=':
		calc2((regl,lvtype), '>', (regr,rvtype), savereg)
		outputln('xor %s,%s,1'%(savereg))
		return 'sint32'
	elif oper == '>=':
		calc2((regl,lvtype), '<', (regr,rvtype), savereg)
		outputln('xor %s,%s,1'%(savereg))
		return 'sint32'
	elif oper == '==':
		if lvtype[0] == rvtype[0]:
			outputln('ori %s,$zero,1'%(savereg))
			outputln('beq %s,%s,1'%(regl,regr))
			outputln('ori %s,$zero,0'%(savereg))
		if lvtype[0] == 's' and rvtype[0] == 'u':
			outputln('srl $t0,%s,%d'%(regl, int(lvtype[-2:]) - 1))
			outputln('bne $t0,$zero,4')
			#s为正
			outputln('ori %s,$zero,1'%(savereg))
			outputln('beq %s,%s,1'%(regl,regr))
			outputln('ori %s,$zero,0'%(savereg))
			outputln('blez $zero,1')
			#s为负
			outputln('ori %s,$zero,0'%(savereg))
		if lvtype[0] == 'u' and rvtype[0] == 's':
			outputln('srl $t0,%s,%d'%(regr, int(rvtype[-2:]) - 1))
			outputln('bne $t0,$zero,4')
			#s为正
			outputln('ori %s,$zero,1'%(savereg))
			outputln('beq %s,%s,1'%(regl,regr))
			outputln('ori %s,$zero,0'%(savereg))
			outputln('blez $zero,1')
			#s为负
			outputln('ori %s,$zero,0'%(savereg))
		return 'sint32'		
	elif oper == '!=':
		calc2((regl,lvtype), '==', (regr,rvtype), savereg)
		outputln('xor %s,%s,1'%(savereg))
		return 'sint32'
	elif oper =='&':
		outputln('and %s,%s,%s'%(savereg,regl,regr))
	elif oper == '^':
		outputln('xor %s,%s,%s'%(savereg,regl,regr))
	elif oper =='|':
		outputln('or %s,%s,%s'%(savereg,regl,regr))
	elif oper == '&&':
		outputln('ori %s,$zero,0'%(savereg))
		outputln('beq %s,$zero,2'%(regl))
		outputln('beq %s,$zero,1'%(regr))
		outputln('ori %s,$zero,1'%(savereg))
	elif oper == '||':
		outputln('ori %s,$zero,1'%(savereg))
		outputln('bne %s,$zero,2'%(regl))
		outputln('bne %s,$zero,1'%(regr))
		outputln('ori %s,$zero,0'%(savereg))
	else:
		rassignr(savereg,'$zero')
	pass



def dealExpression(exp, saveto, prefuncname, corvar):
	parts = toParts(exp, prefuncname, corvar)
	
	suffix = midToSuffix(parts)
	ansvtype = ''

	for i in suffix:
		print i[0],'#',i[1],'#',i[2]
	print '\n'
	
	##calc mid and save to saveto

	stack = []
	failFlag = 0
	for i,tp,vtype in suffix:
		if tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
			stack.append((i,tp,vtype))
		elif tp == 'symbol' and operation_units[i] == 2:
			r = stack.pop()
			rs = '$a3'
			if r[1] == 'register': ##if the tp is register, it must be a value calculated and pushed before
									##but the value in reg may be replaced, so we must pop from stack to get the value
				outputln('POP ' + rs)
			else:
				if not assignr(r,rs,prefuncname, corvar):
					failFlag = throw_error(exp)
					break

			if i in assign_operation:
				ansvtype = rassign((rs, r[2]), l, prefuncname, corvar)
				if ansvtype == '':
					failFlag = throw_error(exp)
					break
				if not rassignrWithStyle(saveto, rs, l[2], r[2]):
					failFlag = throw_error(exp)
					break
			else:
				l = stack.pop()
				ls = '$a2'
				if l[1] == 'register':
					outputln('POP ' + ls)
				else:
					if not assignr(l,ls,prefuncname, corvar):
						failFlag = throw_error(exp)
						break
				ansvtype = calc2((ls,l[2]),i,(rs,r[2]),saveto) ##这样是无法写回数据的. 只能把右值assign给寄存器. 左值不行
			
			stack.append((saveto,'register',ansvtype))
			outputln('PUSH ' + saveto)
		elif tp == 'symbol' and operation_units[i] == 1:
			l = stack.pop()
			ls = '$a2'
			if l[1] == 'register':
				outputln('POP ' + ls)
			else:
				assignr(l,ls,prefuncname, corvar)
			ansvtype = calc1((ls,l[2]),i,saveto)
			stack.append((saveto,'register',ansvtype))
			outputln('PUSH ' + saveto)
		else:
			failFlag = throw_error(exp)
			break

	
	if not failFlag:
		i,tp,vtype = stack[0]
		if tp == 'register':
			outputln('POP ' + saveto)
		else:
			assignr(stack[0], saveto, prefuncname, corvar)
	else:
		for i in stack:
			if i[1] == 'register':
				outputln('POP $t0')
		return '$zero','NoType','NoVtype'
















































""""""""""""""""""""" 读入数据并格式化 """""""""""""""""""""
codes = init_input()
sys.stdout = open('code1.txt', 'w')
for i in codes:
	print i

sys.stdout = open('output.txt', 'w')




""""""""""""""""""""""""""""""""" 分块,初始化function类 开始 """""""""""""""""""""""""""""""""

i = 0
for i in range(len(codes)):
	if codes[i].find('(') != -1:
		globalCodes = codes[:i]
		codes = codes[i:]
		break

while len(codes) > 0:
	block = codes[0:2]
	codes = codes[2:]
	num = 1
	while num != 0 and len(codes)>=1:
		tmp = codes[0].strip()
		if tmp == '':
			continue
		block.append(tmp)
		if tmp == '{':
			num += 1
		elif tmp == '}':
			num -= 1
		codes = codes[1:]
	func = Function(block)
	functions.append(func)
	while len(codes)>0 and codes[0].strip == '':
		codes = codes[1:]

for i in range(len(functions)):
	if functions[i].name == '':
		functions[i],functions[len(functions)-1] = swap(functions[i],functions[len(functions)-1])
		functions = functions[:-1]
	else:
		functionDict[functions[i].name] = functions[i]
	if i >= len(functions)-1:
		break

if functions[len(functions)-1].name != 'main':
	for i in range(len(functions)):
		if functions[i].name == 'name':
			functions[i],functions[len(functions)-1] = swap(functions[i],functions[len(functions)-1])
			break
for f in functions:
	functionNameList.append(f.name)
	functionReturnType[f.name] = f.vtype
functionReturnType['while'] = 'sint32'
functionReturnType['for'] = 'sint32'
functionReturnType['if'] = 'sint32'

for f in functions:
	print f.name
	for i,j in f.vardict.items():
		print '\t',j.name

""""""""""""""""""""""""""""""""" 分块,初始化function类 结束 """""""""""""""""""""""""""""""""
##此时var还未填充



""""""""""""""""""""""""""""""""" .DATA输出开始 """""""""""""""""""""""""""""""""
outputln('.stack')
outputln('.data')


globalVarList,nouse,globalArray = scanVarible(globalCodes, globalCodes)
for name,tp in globalVarList:
	tmp = Variable(name,tp)
	tmp.corname = prefix_global + name
	tmp.type = 1
	globalVarDict[name] = tmp
for array,tp in globalArray:
	name = array[:array.find('[')].strip()
	num = int(array[ array.find('[') + 1 : array.rfind(']') ].strip())
	tmp = Variable(name, tp)
	tmp.corname = prefix_global + name
	tmp.type = num
	globalVarDict[name] = tmp
tmp = []
num = 0
for i,j in globalVarDict.items():
	print '\t',j.name,j.vtype,j.sizeof,j.type
	tmpnum,tmps = j.generatecode()
	if tmpnum == 0:
		continue
	num += tmpnum
	tmp.append(tmps)
outputln('Global .word ' + str(num * 4))
for s in tmp:
	outputln(s)

""""""""""""""""""""" 全局变量输出完毕 """""""""""""""""""""

for f in functions:
	f.varlist,numdict,arraylist = scanVarible([f.head] + f.vardeclaration, f.realcode)

	for array,tp in arraylist:
		name = array[:array.find('[')].strip()
		num = int(array[array.find('[') + 1:array.rfind(']')].strip())
		tmp = Variable(name, tp)
		tmp.corname = f.prefix + name
		tmp.type = num
		f.vardict[name] = tmp

	numlist = sorted(numdict.items(), key = lambda x:x[1], reverse = True)
	if len(numlist)<= 8:
		for i in range(len(numlist)):
			name,tp = numlist[i][0]
			tmp = Variable(name,tp)
			tmp.corname = '$s' + str(i)
			tmp.type = 0
			f.vardict[name] = tmp
	else:
		for i in range(8):
			name,tp = numlist[i][0]
			tmp = Variable(name, tp)
			tmp.corname = '$s' + str(i)
			tmp.type = 0
			f.vardict[name] = tmp
		for (name,tp),num in numlist[8:]:
			tmp = Variable(name, tp)
			tmp.corname = f.prefix + name
			tmp.type = 1
			f.vardict[name] = tmp
	tmp = []
	num = 0
	for i,j in f.vardict.items():
		tmpnum,tmps = j.generatecode()
		if tmpnum == 0:
			continue
		num += tmpnum
		tmp.append(tmps)
	outputln(f.name + ' .word ' + str(num * 4))
	for s in tmp:
		outputln(s)
	outputln('')

""""""""""""""""""""""""""""""""" .DATA输出结束 """""""""""""""""""""""""""""""""




""""""""""""""""""""""""""""""""" .CODE输出开始 """""""""""""""""""""""""""""""""
outputln('\n.code')
outputln('START:')
outputln('addi $ra,$zero,END')
outputln('j main_begin\n')

for f in functions:
	f.printcode()
	print ''

outputln('END:')
outputln('ENDSTART')

for f in functions:
	print f.name
	for i,j in f.params:
		print '\t',i,j,
		cor = ''
		if i.find('[') != -1:
			i = i[:i.find('[')].strip()
		var = f.vardict[i]
		print var.corname,var.type,var.vtype


