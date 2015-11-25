.stack
.data
Global .word 4
Global_a .word 0
delay .word 92
delay_a .word 0 0 0 0 0 0 0 0 0 0
delay_e .word 0
delay_d .word 0 0 0 0 0 0 0 0 0 0
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
delay_while_start_0:
ori $a3,$zero,0
beq $t0,$zero,delay_while_end_0
ori $a3,$zero,1
j delay_while_start_0
delay_while_end_0:

ori $a3,$zero,3
ori $v0,$zero,0
jr $ra
main_begin:
ori $a3,$zero,0
ori $a3,$zero,65535
sll $a3,$a3,16
ori $a3,$zero,64608
main_while_start_0:
POP $t0
beq $t0,$zero,main_while_end_0
ori $a3,$zero,1
ori $s1,$a2,0
ori $v1,$zero,0
PUSH $v1
ori $s2,$a3,0
POP $t0
main_if_start_1:
ori $a3,$zero,100
beq $t0,$zero,main_if_end_1
ori $a3,$zero,0
j main_ifelse_end_1
main_if_end_1:

main_else_start_1:
main_if_start_2:
ori $a3,$zero,10
beq $t0,$zero,main_if_end_2
ori $a3,$zero,1
j main_ifelse_end_2
main_if_end_2:

main_else_start_2:
ori $s2,$a3,0
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
