.stack
.data
Void .word 1
Void_a .word 0

delay .word 10
delay_a .word 0 0 0 0 0 0 0 0 0 0

main .word 0

.code
START:
addi $ra,$zero,END
j main

delay_begin:
ori $v1,$zero,30000
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
ori $v0,$s0,0
addi $v0,$v0,1
ori $v1,$v0,0
PUSH $v1
ori $v1,$zero,5
POP $v1
add $v1,$v1,$v1
ori $v1,$v1,0
PUSH $v1
POP $v1
delay_while_start_0:
ori $v1,$zero,0
ori $v0,$s0,0
sub $t0,$v0,$v1
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,delay_while_end_0
ori $v1,$zero,1
ori $v0,$s0,0
sub $v1,$v0,$v1
PUSH $v1
POP $v1
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
j delay_while_start_0
delay_while_end_0:

ori $v1,$zero,3
POP $a0
lw $v0,Void_a(%a0)
add $v1,$v0,$v1
PUSH $v1
POP $v1
ori $v0,$s1,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
ori $v0,$zero,0
jr $ra
main_begin:
ori $v1,$zero,0
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
main_while_start_0:
POP $t0
beq $t0,$zero,main_while_end_0
ori $v1,$zero,1
ori $v0,$s0,0
add $v1,$v0,$v1
PUSH $v1
POP $v1
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
ori $v1,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
main_if_start_1:
ori $v1,$zero,100
ori $v0,$s0,0
sub $t0,$v0,$v1
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,main_if_end_1
ori $v1,$zero,0
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
j main_ifelse_end_1
main_if_end_1:

main_else_start_1:
main_if_start_2:
ori $v1,$zero,10
ori $v0,$s0,0
sub $t0,$v0,$v1
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,main_if_end_2
ori $v1,$zero,1
ori $v0,$s0,0
ori $v0,$v1,0
ori $v1,$v0,0
PUSH $v1
POP $v1
j main_ifelse_end_2
main_if_end_2:

main_else_start_2:
ori $v1,$zero,3
ori $v0,$s0,0
sub $v0,$v0,$v1
ori $v1,$v0,0
PUSH $v1
POP $v1
main_else_end_2:
main_ifelse_end_2:

main_else_end_1:
main_ifelse_end_1:

POP $v1
j main_while_start_0
main_while_end_0:

main_if_start_1:
ori $v1,$zero,3
ori $v0,$s1,0
sub $t0,$v0,$v1
ori $t0,$zero,1
bgtz $t0,1
ori $t0,$zero,0
PUSH $t0
POP $t0
beq $t0,$zero,main_if_end_1
POP $v1
j main_ifelse_end_1
main_if_end_1:

main_ifelse_end_1:

ori $v0,$zero,0
jr $ra
END:
ENDSTART
