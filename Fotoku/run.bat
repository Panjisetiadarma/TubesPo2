@echo off
echo Mengkompilasi ulang aplikasi...
javac -cp "lib/*" -d bin src/utils/*.java src/component/*.java src/model/*.java src/controller/*.java src/view/*.java src/Main.java

echo Menjalankan aplikasi PixView...
java -cp "bin;lib/*" Main
pause
