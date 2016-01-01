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
j main_begin

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
ori $t0,$zero,1
beq $t0,$zero,main_while_end_0
ori $a3,$zero,1
ori $a2,$s1,0
ori $v1,$zero,0
PUSH $v1
ori $a3,$s2,0
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
ori $a3,$zero,3
main_else_end_2:
main_ifelse_end_2:

main_else_end_1:
main_ifelse_end_1:

PUSHA ##main
ori $a3,$zero,5
ori $a0,$zero,2
sll $a0,$a0,2
lw $a0,main_k($a0)
jal delay
POPA ##main
ori $v0,$v1,0
j main_while_start_0
main_while_end_0:

ori $v0,$zero,0
jr $ra
END:
ENDSTART
