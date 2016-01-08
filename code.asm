.stack
.data
Global .word 0
main .word 0


.code
START:
addi $ra,$zero,END
j main_begin

main_begin:
ori $a3,$zero,1
ori $a2,$zero,1
add $v1,$a2,$a3
PUSH $v1
POP $a3
ori $s0,$a3,0
ori $v1,$a3,0
ori $v0,$s0,0
jr $ra
ori $v0,$zero,0
jr $ra
END:
END START
