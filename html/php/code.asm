.stack
.data
Global .word 0
sum .word 0

main .word 8
main_c .word 0 0


.code
START:
addi $ra,$zero,END
j main_begin

sum_begin:
ori $a3,$s1,0
ori $s2,$a3,0
ori $v1,$a3,0
PUSH $v1
ori $v1,$s0,0
ori $v0,$zero,0
jr $ra
ori $v0,$zero,0
jr $ra
main_begin:
ori $a3,$zero,1
