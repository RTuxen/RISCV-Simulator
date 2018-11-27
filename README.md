# RISCV-Simulator
An instruction set simulator for the RISC-V architecture written in Java running on Ubuntu 18.04.

# Guide til at køre på Ubuntu
First download Java OpenJDK8
```
sudo apt update
sudo apt install default-jre
sudo apt install openjdk-8-jdk
```
Verify that is has been installed by
```
java -version
````
You should se an output like 
```
openjdk version "1.8.0_162"
OpenJDK Runtime Environment (build 1.8.0_162-8u162-b12-1-b12)
OpenJDK 64-Bit Server VM (build 25.162-b12, mixed mode)
```
If you have several versions of java you can manage them by
```
sudo update-alternatives --config java
sudo update-alternatives --config javac
```
Make sure you select "java-8-opgenjdk..."
- How to run the program
Download all files in the "Ubuntu Code" folder and place on the VM
Go to the folder using
```
cd cd Documents/folder
```
Then compile the files using
```
cd Assignment/
javac *.java
```
then run files by
```
cd ..
java Assignment.Simulator XX
```
Where 'XX' is the testname

If you get the error message Failed to load module “canberra-gtk-module” download
```
sudo apt install libcanberra-gtk-module libcanberra-gtk3-module
```

