.text
main:
la a1 , hello
addi a0, x0, 4
ecall
addi a0, x0, 10
ecall
hello:
.string "Hello*as\n"
