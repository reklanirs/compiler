.stack
.data
Global .word 0
main .word 0


.code
START:
addi $ra,$zero,END
j main_begin

main_begin:
ori $a3,$zero,0
ori $v0,$zero,0
jr $ra
ori $v0,$zero,0
jr $ra
END:
END START
