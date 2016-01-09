.stack
.data
Global .word 0
main .word 0


.code
START:
addi $ra,$zero,END
addi $29,$0,4000H
j main_begin

main_begin:
ori $a3,$zero,0
ori $s0,$a3,0
ori $v1,$a3,0
PUSH $v1
POP $v1
ori $v0,$zero,0
jr $ra
ori $v0,$zero,0
jr $ra

END:
END START
