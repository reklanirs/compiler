.stack
.data
Global .word 4
Global_a .word 0
delay .word 52
delay_a .word 0 0 0 0 0 0 0 0 0 0
delay_e .word 0
delay_s .half 0 0
delay_w .half 0 0

main .word 12
main_k .word 0 0 0


.code
START:
addi $ra,$zero,END
j main

delay_begin:
ori $a3,$zero,1000
POP $a0
lw $a2,Global_a(%a0)
ori ('$a2', 'a'),('$a3', 'sint32'),0
ori $v1,('$a2', 'a'),0
PUSH $v1
POP $v1
delay_while_start_0:
ori $a3,$zero,0
ori $a2,$s0,0
sub $t0,('$a2', 'sint08'),('$a3', 'sint32')
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,delay_while_end_0
ori $a3,$zero,1
ori $a2,$s0,0
sub $v1,('$a2', 'sint08'),('$a3', 'sint32')
PUSH $v1
POP $v1
ori $a2,$s0,0
ori ('$a2', 'sint08'),('$v1', None),0
ori $v1,('$a2', 'sint08'),0
PUSH $v1
POP $v1
j delay_while_start_0
delay_while_end_0:

ori $a3,$zero,3
POP $a0
lw $a2,Global_a(%a0)
add $v1,('$a2', 'a'),('$a3', 'sint32')
PUSH $v1
POP $v1
ori $a2,$s4,0
ori ('$a2', 'sint32'),('$v1', None),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
ori $v0,$zero,0
jr $ra
main_begin:
ori $a3,$zero,0
ori $a2,$s2,0
ori ('$a2', 'sint32'),('$a3', 'sint32'),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
ori $a3,$zero,65535
sll $a3,$a3,16
ori $a3,$zero,64608
ori $a2,$s1,0
ori ('$a2', 'sint32'),('$a3', 'sint32'),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
main_while_start_0:
POP $t0
beq $t0,$zero,main_while_end_0
ori $a3,$zero,1
ori $a2,$s2,0
add $v1,('$a2', 'sint32'),('$a3', 'sint32')
PUSH $v1
POP $v1
ori $a2,$s2,0
ori ('$a2', 'sint32'),('$v1', None),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
ori $a2,$s1,0
ori $zero,$v1,0
PUSH $v1
ori $a3,$s2,0
POP $v1
ori ('$v1', None),('$a3', 'sint32'),0
ori $v1,('$v1', None),0
PUSH $v1
POP $v1
main_if_start_1:
ori $a3,$zero,100
ori $a2,$s2,0
sub $t0,('$a2', 'sint32'),('$a3', 'sint32')
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,main_if_end_1
ori $a3,$zero,0
ori $a2,$s2,0
ori ('$a2', 'sint32'),('$a3', 'sint32'),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
j main_ifelse_end_1
main_if_end_1:

main_else_start_1:
main_if_start_2:
ori $a3,$zero,10
ori $a2,$s2,0
sub $t0,('$a2', 'sint32'),('$a3', 'sint32')
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,main_if_end_2
ori $a3,$zero,1
ori $a2,$s2,0
ori ('$a2', 'sint32'),('$a3', 'sint32'),0
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
j main_ifelse_end_2
main_if_end_2:

main_else_start_2:
ori $a3,$zero,3
ori $a2,$s2,0
sub ('$a2', 'sint32'),('$a2', 'sint32'),('$a3', 'sint32')
ori $v1,('$a2', 'sint32'),0
PUSH $v1
POP $v1
main_else_end_2:
main_ifelse_end_2:

main_else_end_1:
main_ifelse_end_1:

POP $v1
j main_while_start_0
main_while_end_0:

ori $v0,$zero,0
jr $ra
END:
ENDSTART
