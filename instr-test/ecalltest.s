.text
main:
la a1 , hello
addi a0, x0, 4
ecall
addi a0, x0, 1
addi a1, x0, -55
ecall
la a1, num3
addi a0, x0, 11
ecall
la a1, num2
ecall
la a1, str
addi a0, x0, 4
ecall
addi a0, x0, 10
ecall
hello:
.string "Hello\n"
str:
.string "W3ird numb3rs :;{[9901hoho_*"
num:
.word -2222
num2:
.word 8902
num3:
.word 178

