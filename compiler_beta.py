#!/usr/bin/python2.7
# -*- coding: utf-8 -*- import requests
import os
import re
import sys
import math
import time
from random import choice

reload(sys)
codes = []
raw = []
error_strings = []
outputcode = open('code.asm','w')
errorlog = open('error.txt','w')

variable_types = ['int']
types = ['int', 'void']
control_word = ['for', 'while', 'break', 'continue', 'if', 'else', 'return']
arithmetic_operator = ['+', '-', '*', '/', '%']
relation_operator = ['>', '<', '<=', '>=', '==', '!=']
logical_operator = ['&&', '||', '!']
bit_operator = ['&', '|', '^', '~']
memory_operator = ['$']
reserved_word = types + control_word
all_word = [types, control_word, arithmetic_operator,
				 relation_operator, logical_operator, bit_operator, memory_operator]

priority = {
'#':10000,
'(':1, ')':1, '[':1, ']':1,
'$':2,
'!':2, '~':2, '++':2, '--':2, '`':2,
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
'=':15, '+=':15, '-=':15, '*=':15, '/=':15, '%=':15
}
operation_units = {
'#':0,
'(':1, ')':1, '[':1, ']':1,
'$':1,
'!':1, '~':1, '++':1, '--':1, '`':1,
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
'=':2, '+=':2, '-=':2, '*=':2, '/=':2, '/=':2
}


prefix_global = 'Void_'		
globalVarList = []
globalVarDict = {}
globalArray = []
globalCodes = []

functionNameList = []		
functions = []



def swap(a,b):
	return b,a

def throw_error(s):
	global error_strings
	error_strings.append(s)
	return

def output(s):
	outputcode.write(s)
	outputcode.flush()

def outputln(s):
	outputcode.write(str(s))
	outputcode.write('\n')
	outputcode.flush()

def extract_a_part(ss, ret):
	if ss[0] == '{' or ss[0] == '}':
		ret.append(ss[0])
		ss = ss[1:].strip()
		return ss

	if ss[:3] == 'for':
		ss = ss[3:].strip()
		tmp = get_parenthesis_content(ss)
		ret.append('for' + tmp[0])
		ss = tmp[1]
		if ss[0] != '{':
			ret.append('{')
			ss = extract_a_part(ss, ret)
			ret.append('}')
		return ss
	if ss[:5] == 'while':
		ss = ss[5:].strip()
		tmp = get_parenthesis_content(ss)
		ret.append('while' + tmp[0])
		ss = tmp[1]
		if ss[0] != '{':
			ret.append('{')
			ss = extract_a_part(ss, ret)
			ret.append('}')
		return ss
	elif ss[:2] == 'if':
		ss = ss[2:].strip()
		tmp = get_parenthesis_content(ss)
		ret.append('if' + tmp[0])
		ss = tmp[1]
		if ss[0] != '{':
			ret.append('{')
			ss = extract_a_part(ss, ret)
			ret.append('}')

		if re.match('else[ \\t]+if',ss):
			tmp = ss.find('if')
			ss = ss[tmp:]
			ret.append('else')
			ret.append('{')
			ss = extract_a_part(ss,ret)
			ret.append('}')
		elif ss[:4] == 'else':
			ss = ss[4:].strip()
			ret.append('else')
			if ss[0] != '{':
				ret.append('{')
				ss = extract_a_part(ss, ret)
				ret.append('}')
		return ss

	i = 0
	for c in ss:
		if c == ';':
			if i:
				ret.append(ss[:i])
			ss = ss[i + 1:]
			break
		elif c == '{' or c == '}':
			if i:
				ret.append(ss[:i])
			ret.append(ss[i])
			ss = ss[i + 1:]
			break
		i += 1
	return ss


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

	while len(ss) > 0:
		# for while if else
		ss = extract_a_part(ss, ret)
	return ret




def scanVarible(codes):
	ret = {}
	variables = []
	arr = []
	l = []
	if len(codes) == 0:
		return ret
	if re.match('int[ \\t]+[A-Za-z][A-Za-z0-9]*\\([\\w ,]*\\)|void[ \\t]+[A-Za-z][A-Za-z0-9]*\\([\\w ,]*\\)',codes[0].strip()) != None:
		l = codes[1:]
		tmp = codes[0].strip()
		names = tmp[tmp.find('(')+1:tmp.rfind(')')].strip()
		if names not in types and names != '':
			for x in names.split(','):
				name = x[x.strip().find(' ')+1:].strip()
				if name.find('[') != -1:
					arr.append(name)
				else:
					variables.append(name)
	else:
		l = codes

	for s in l:
		s = s.strip()
		if s == '' or s == '{':
			continue

		names = s[s.find(' ')+1:].strip()
		if '(' in s or '}' in s or s[:s.find(' ')].strip() not in variable_types:
			break
		if names == '':
			throw_error(s)

		tmp = re.match('[A-Za-z][A-Za-z0-9]*[ \\t]*([ \\t]*,[ \\t]*([A-Za-z][A-Za-z0-9]*))*[ \\t]*;?',names)
		if tmp == None or tmp.span()[1] != len(s):
			if names.find('[') == -1:
				throw_error(s)
		tmp = [x.strip() for x in names.split(',')]
		for v in tmp:
			if v in reserved_word:
				throw_error(s)
				break
			elif v.find('[') == -1:
				variables.append(v)
			else:
				arr.append(v)

	for v in variables:
		ret[v] = 0
		for s in codes:
			s = s.strip()
			tmp = '\\b' + v + '\\b'
			if re.search(tmp,s) != None:
				ret[v] += 1
				#print tmp,s
	return variables,ret,arr


class Variable(object):
	"""docstring for Variable
		type: 0 : register;   1 : RAM,单独变量,占1单元  n(>1):数组,占n单元
	"""
	def __init__(self, name):
		super(Variable, self).__init__()
		self.name = name
		self.type = 0
		self.corname = ''
		self.value = 0

	def printcode(self, outer):
		if self.type == 1:
			outer.write(self.corname + ' .word 0')
		elif self.type > 1:
			outer.write(self.corname + ' .word')
			for i in range(self.type):
				outer.write(' 0')
			outer.write('\n')
		else:
			pass



class Function(object):
	"""docstring for Function"""	
	def __init__(self, codes):
		super(Function, self).__init__()
		self.codes = codes
		tmp = codes[0].strip()
		name = tmp[tmp.find(' ')+1:tmp.find('(')]
		if name == '':
			throw_error(codes[0])
		varlist = tmp[tmp.find('(')+1:tmp.rfind(')')].strip()
		self.arglist = []

		# if varlist != '' and varlist not in types:
		# 	self.arglist = [x.strip().split(' ')[1] for x in varlist.split(',')]

		if varlist != '' and varlist not in types:
			for x in varlist.split(','):
				tmp = x.strip().split(' ')
				if len(tmp) >= 2:
					self.arglist.append(tmp[1])

		self.name = name
		self.prefix = name + '_'
		self.varlist = []
		self.vardict = {}

		self.head = codes[0]
		self.vardeclaration = []
		self.realcode = []
		for i in range(2,len(codes)):
			if re.match('int[ \\t]+[A-Za-z][A-Za-z0-9]*,?',codes[i]):
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
		rassignr('$zero','$v0')
		outputln('jr $ra')





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

def readapart(s):##
	s = s.strip()
	if s == '' or s == '(' or s == ')':
		return s[0:1],s[1:],'parenthesis'
	if re.match('[0-9]+$|0x[0-9a-fA-F]+$',s):
		tmp = re.match('[-+]?[0-9]+$|[-+]?0x[0-9a-fA-F]+$',s).span()[1]
		return s[:tmp],s[tmp:].strip(),'const'
	elif re.match('\\$0x[0-9a-fA-F]+',s):
		tmp = re.match('\\$0x[0-9a-fA-F]+',s).span()[1]
		return s[:tmp],s[tmp:].strip(),'port'
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
						return s[:i+1],s[i+1:].strip(),'function'
		elif tmp < len(s) and s[tmp] == '[':
			num = 1
			for i in range(tmp+1, len(s)):
				if s[i] == '[':
					num += 1
				elif s[i] == ']':
					num -= 1
					if num == 0:
						return s[:i+1],s[i+1:].strip(),'array'
		else:
			return s[:tmp],s[tmp:].strip(),'variable'
	elif s[0] in '+-*/=<>!&|^~':
		i = 0
		while i < len(s) and s[i] in '[+-*/=<>]+!&|^~':
			i += 1
		return s[:i],s[i:].strip(),'symbol'
	else:
		return s,'','NoType'

def toParts(s):
	ret = []
	while len(s) > 0:
		tmp = readapart(s)
		ret.append((tmp[0],tmp[2]))
		s = tmp[1].strip()
	for i in range(len(ret)):
		if ret[i][0] == '-':
			if i == 0 or ret[i-1][0] == '(' or ret[i-1][1] == 'symbol':
				ret[i] = ('`',ret[i][1])
		if ret[i][0] == '+':
			if i == 0 or ret[i-1][0] == '(' or ret[i-1][1] == 'symbol':
				ret = ret[:i] + ret[i+1:]
	return ret



def midToSuffix(l):##
	ret = []
	symbols = [('#','NoType')]
	for i,tp in l:
		if tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
			ret.append((i,tp))
		elif i == '(':
			symbols.append((i,tp))
		elif i == ')':
			tmp = symbols.pop()
			while tmp[0] != '(':
				ret.append(tmp)
				tmp = symbols.pop()
		else:
			if symbols[-1][0] == '(':
				symbols.append((i,tp))
			elif priority[i] <= priority[symbols[-1][0]]:
				symbols.append((i,tp))
			else:
				while priority[i] > priority[symbols[-1][0]]:
					ret.append(symbols.pop())
				symbols.append((i,tp))
	for i in reversed(symbols[1:]):
		ret.append(i)
	return ret


def rassignr(regfrom, regto):
	outputln('ori %s,%s,0'%(regto,regfrom))
	pass

def assignr(x , reg, prefuncname, corvar):
	# x[0] : name x[1] : type
	# tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
	name = x[0]
	tp = x[1]
	if tp == 'function':
		funcName = name[:name.find('(')].strip()
		params = name[name.find('(')+1:name.rfind(')')].strip()
		if params == '':
			params = []
		else:
			params = [x.strip() for x in params.split(',')]
		
		outputln('PUSHA ##'+prefuncname)
		for i in range(len(params)):
			var = functions[funcName].vardict[functions[funcName].varlist[i]]
			if corvar[name].type > 1 and var.type > 1:
				size = min(corvar[name].type, var.type)
				for i in range(1,size+1):
					outputln('ori $t1,$zero,%d'%(i))
					outputln('lw $t0,%s(%s)'%(corvar[name].corname,'$t1'))
					outputln('sw $t0,%s(%s)'%(var.corname,'$t1'))
				pass
			elif corvar[name].type > 1 or var.type > 1:
				throw_error(x[0])
				continue
			else:
				dealExpression(params[i], '$a0', prefuncname, corvar)
				if var.type == 0:
					rassignr('$a0',var.corname)
				elif var.type == 1:
					outputln('sw $a0,%s($zero)'%(var.corname))
					pass
		outputln('jal ' + funcname)
		outputln('POPA ##' + prefuncname)
		rassignr('$v0', reg)
		pass
	elif tp == 'array':
		arrayName = x[0][:name.find('[')].strip()
		param = name[name.find('[')+1:name.rfind(']')].strip()
		dealExpression(param, '$a0', prefuncname, corvar)
		realArrayName = corvar[arrayName].corname
		outputln('lw %s,%s(%s)'%(reg, realArrayName, '$a0' ))
		pass
	elif tp == 'const':
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
		pass
	elif tp == 'variable':
		var = corvar[name]
		if var.type == 0:
			rassignr(var.corname, reg)
		elif var.type == 1:
			outputln('lw %s,%s(%s)'%(reg, var.corname, '$zero'))
		else:
			throw_error(name)
			rassignr('$zero',reg)
		pass
	elif tp == 'port':
		##
		pass
	else:
		throw_error(x[0])
		rassignr('$zero',reg)
	pass




def calc1(reg, oper, savereg):
	if oper == '!':
		outputln('ori %s,$zero,1'%(savereg))
		outputln('beq %s,$zero,1'%(reg))
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '~':
		outputln('xor %s,%s,%s'%(savereg,reg,reg))
	elif oper == '++':
		outputln('addi %s,%s,1'%(reg,reg))
		outputln('ori %s,%s,0'%(savereg,reg))
	elif oper == '--':
		outputln('addi %s,%s,-1'%(reg,reg))
		outputln('ori %s,%s,0'%(savereg,reg))
	elif oper == '`':
		outputln('xor %s,%s,%s'%(savereg,reg,reg))
		outputln('addi %s,%s,1'%(savereg,savereg))
	else:
		rassignr(savereg,'$zero')
	pass


# operation_units = {
# '#':0,
# '(':1, ')':1, '[':1, ']':1,
# '$':1,
# '!':1, '~':1, '++':1, '--':1, '`':1,
# '*':2, '/':2, '%':2,
# '+':2, '-':2,
# '<<':2, '>>':2,
# '<':2, '<=':2, '>':2, '>=':2,
# '==':2, '!=':2,
# '&':2,
# '^':2,
# '|':2,
# '&&':2,
# '||':2,
# '=':2, '+=':2, '-=':2, '*=':2, '/=':2
# }

def calc2(regl, oper, regr, savereg):
	if oper == '*':
		outputln('mult %s,%s'%(regl,regr))
		outputln('mflo %s'%(savereg))
	elif oper == '/':
		outputln('div %s,%s'%(regl,regr))
		outputln('mflo %s'%(savereg))
	elif oper == '%':
		outputln('div %s,%s'%(regl,regr))
		outputln('mfhi %s'%(savereg))
	elif oper == '+':
		outputln('add %s,%s,%s'%(savereg,regl,regr))
	elif oper == '-':
		outputln('sub %s,%s,%s'%(savereg,regl,regr))
	elif oper == '<<':
		outputln('sllv %s,%s,%s'%(savereg,regl,regr))
	elif oper == '>>':
		outputln('srav %s,%s,%s'%(savereg,regl,regr))
	elif oper == '<':
		outputln('sub $t0,%s,%s'%(regl,regr))
		outputln('ori %s,$zero,1'%(savereg))
		outputln('bltz $t0,1')
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '<=':
		outputln('sub $t0,%s,%s'%(regl,regr))
		outputln('ori %s,$zero,1'%(savereg))
		outputln('blez $t0,1')
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '>':
		outputln('sub $t0,%s,%s'%(regl,regr))
		outputln('ori %s,$zero,1'%(savereg))
		outputln('bgtz $t0,1')
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '>=':
		outputln('sub $t0,%s,%s'%(regl,regr))
		outputln('ori %s,$zero,1'%(savereg))
		outputln('bgez $t0,1')
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '==':
		outputln('ori %s,$zero,1'%(savereg))
		outputln('beq %s,%s,1'%(regl,regr))
		outputln('ori %s,$zero,0'%(savereg))
	elif oper == '!=':
		outputln('ori %s,$zero,1'%(savereg))
		outputln('bne %s,%s,1'%(regl,regr))
		outputln('ori %s,$zero,0'%(savereg))
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
	elif oper == '=':
		outputln('ori %s,%s,0'%(regl,regr))
		outputln('ori %s,%s,0'%(savereg,regl))
	elif oper == '+=':
		outputln('add %s,%s,%s'%(regl,regl,regr))
		outputln('ori %s,%s,0'%(savereg,regl))
	elif oper == '-=':
		outputln('sub %s,%s,%s'%(regl,regl,regr))
		outputln('ori %s,%s,0'%(savereg,regl))
	elif oper == '*=':
		outputln('mult %s,%s'%(regl,regr))
		outputln('mflo ' + regl)
		outputln('ori %s,%s,0'%(savereg,regl))
	elif oper == '/=':
		outputln('div %s,%s'%(regl,regr))
		outputln('mflo ' + regl)
		outputln('ori %s,%s,0'%(savereg,regl))
	elif oper == '%=':
		outputln('div %s,%s'%(regl,regr))
		outputln('mfhi ' + regl)
		outputln('ori %s,%s,0'%(savereg,regl))
	else:
		rassignr(savereg,'$zero')
	pass

def dealExpression(exp, saveto, prefuncname, corvar):
	parts = toParts(exp)
	suffix = []

	# if len(parts) >= 2 and parts[1][0][-1] == '=' and parts[1][0] != '==':
	# 	suffix = midToSuffix(parts[2:])
	# else:
	# 	suffix = midToSuffix(parts)
	suffix = midToSuffix(parts)

	for i in suffix:
		print i[0],
	print ''
	##calc mid and save to saveto

	stack = []
	for i,tp in suffix:
		if tp == 'function' or tp == 'array' or tp == 'const' or tp == 'variable' or tp == 'port':
			stack.append((i,tp))
		elif tp == 'symbol' and operation_units[i] == 2:
			r = stack.pop()
			l = stack.pop()
			rs = '$v1'
			ls = '$v0'
			if r[1] == 'register':
				rs = r[0]
				outputln('POP ' + rs)
			else:
				assignr(r,rs,prefuncname, corvar)
			
			if l[1] == 'register':
				ls = l[0]
				outputln('POP ' + ls)
			else:
				assignr(l,ls,prefuncname, corvar)
			
			calc2(ls,i,rs,saveto)
			stack.append((saveto,'register'))
			outputln('PUSH ' + saveto)
			pass
		elif tp == 'symbol' and operation_units[i] == 1:
			l = stack.pop()
			ls = '$v0'
			if l[1] == 'register':
				ls = l[0]
				outputln('POP ' + ls)
			else:
				assignr(l,ls,prefuncname, corvar)
			calc1(ls,i,saveto)
			stack.append((saveto,'register'))
			outputln('PUSH ' + saveto)

	outputln('POP ' + saveto)
	pass



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







""""""""""""""""""""" 读入数据并格式化 """""""""""""""""""""
codes = init_input()
sys.stdout = open('output.txt', 'w')
for i in codes:
	print i

l,t = findCurlyContent(codes)
print 'codelen:',len(codes)
for i in l:
	print i
print 'tmp:',t


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
	if i >= len(functions)-1:
		break

if functions[len(functions)-1].name != 'main':
	for i in range(len(functions)):
		if functions[i].name == 'name':
			functions[i],functions[len(functions)-1] = swap(functions[i],functions[len(functions)-1])
			break
for f in functions:
	functionNameList.append(f.name)

""""""""""""""""""""""""""""""""" 分块,初始化function类 结束 """""""""""""""""""""""""""""""""







""""""""""""""""""""""""""""""""" .DATA输出开始 """""""""""""""""""""""""""""""""

outputln('.stack')
outputln('.data')
output('Void .word ')

globalVarList,tmp,globalArray = scanVarible(globalCodes)
outputln(len(globalVarList))

for i in globalVarList:
	tmp = Variable(i)
	tmp.corname = prefix_global + i
	tmp.type = 1
	tmp.value = 0
	globalVarDict[i] = tmp
for i in globalArray:
	name = i[:i.find('[')].strip()
	num = int(i[i.find('[') + 1:i.rfind(']')].strip())
	tmp = Variable(name)
	tmp.corname = prefix_global + name
	tmp.type = num
	globalVarDict[name] = tmp
for i,j in globalVarDict.items():
	j.printcode(outputcode)

outputln('\n')

for f in functions:
	f.varlist,numdict,arrlist = scanVarible(f.codes)
	sumlen = 0
	for i in arrlist:
		name = i[:i.find('[')].strip()
		num = int(i[i.find('[') + 1:i.rfind(']')].strip())
		sumlen += num
		tmp = Variable(name)
		tmp.corname = f.prefix + name
		tmp.type = num
		f.vardict[name] = tmp

	numlist = sorted(numdict.items(), key = lambda x:x[1], reverse = True)
	if len(numlist)<= 8:
		for i in range(len(numlist)):
			name = numlist[i][0]
			tmp = Variable(name)
			tmp.corname = '$s' + str(i)
			tmp.type = 0
			tmp.value = 0
			f.vardict[name] = tmp
	else:
		sumlen += len(numlist) - 8
		for i in range(8):
			name = numlist[i][0]
			tmp = Variable(name)
			tmp.corname = '$s' + str(i)
			tmp.type = 0
			tmp.value = 0
			f.vardict[name] = tmp
		for name,num in numlist[8:]:
			tmp = Variable(name)
			tmp.corname = f.prefix + name
			tmp.type = 1
			tmp.value = 0
			f.vardict[name] = tmp

	outputln(f.name + ' .word ' + str(sumlen))
	for i,j in f.vardict.items():
		j.printcode(outputcode)
	outputln('')

""""""""""""""""""""""""""""""""" .DATA输出结束 """""""""""""""""""""""""""""""""


""""""""""""""""""""""""""""""""" .CODE输出开始 """""""""""""""""""""""""""""""""
outputln('.code')
outputln('START:')
outputln('addi $ra,$zero,END')
outputln('j main\n')

print '"""""""""""""""""""""""""""""""""'
for f in functions:
	f.printcode()
	print ''

outputln('END:')
outputln('ENDSTART')


print '"""""""""""""""""""""""""""""""""'
for f in functions:
	print f.name
	for i,j in f.vardict.items():
		print '\t', j.name, ' ', j.corname
	print ''