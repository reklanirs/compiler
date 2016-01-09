.stack
.data
Global .word 13243444H
delay .word FFFFFFFEH
main .word 31201000H
one .word 00000001H
allone .word FFFFFFFFH
zeroone .half 0101H
fono .half F101H
onezero .byte F0H,10H,EEH

.code
START:
	addi $ra,$zero,test
	lw $t0,one($zero) #$t0:1H
	add $t0,$t0,$t0
	lhu $t1,fono($zero) #$t1:F101H
	lh $t2,fono($zero) #$t2:FFFFF101H
	lb $t3,onezero($zero) #$t3:FFFFFFF0H
	lbu $t4,onezero($zero) #$t4:F0H
	lw $t5,delay($zero) #t5:FFFFFFFEH
	add $s0,$t0,$t5 #s0:FFFFFFFFH
	addu $s1,$t2,$t2 #yichu
	addu $s1,$s1,$t0
	sub $s3,$t3,$t2
	subu $s4,$t2,$t3
	lw $t6,onezero($t0)
	add $t0,$t0,$t0 #t0:4H
	mult $t2,$t3
	mflo $s5	#s5:
	mfhi $s6	#s6:
	sw $t6,FC60H($zero)
	sw $s5,one($zero)
	add $s5,$s5,$s6
	lw $3,one($zero)
	
	mthi $s5
	mfhi $s6
	add $s5,$s5,$s6
	multu $t2,$t3
	mflo $s5	
	mfhi $s6	
	div $t2,$t3
	mflo $s5	
	mfhi $s6
	divu $t2,$t3
	mflo $s5	
	mfhi $s6
test:	
	or $s7,$t2,$t3
	xor $s6,$t2,$t3
	nor $s5,$t2,$t3
	slt $s4,$t2,$t3
	sll $s4,$s4,5
	sltu $s5,$t2,$t3
	sllv $s4,$s4,$t0
	sra $s4,$t3,2
	srav $s4,$t3,$t0
	srl $s4,$t3,2
	srlv $s4,$t3,$t0
	addiu $s2,$t2,3837
	andi $s3,$t1,257
	ori $s1,$t1,255
	xori $s1,$t1,255
	lui $s5,1023
	and $s7,$t3,$t4
	beq $s7,$t4,1
	add $s7,$s7,$t0
	bne $s7,$t4,1
	bne $t3,$t4,1
	add $s7,$s7,$t0
	bgez $t0,1
	add $s3,$t2,$t0
	bgtz $t2,1
	add $s3,$t2,$t0
	bltz $t2,1
	add $s3,$t2,$t0
	blez $zero,1
	add $s3,$t2,$t0
	blez $t1,1
	bltz $t1,1
	bgezal $t0,5
	slti $s3,$t1,62000
	sltiu $s3,$t1,62000
	add $s3,$t2,$t0
	blez $t1,1
	bltz $t1,1
	jr $ra


END:
END START
