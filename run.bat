@echo off
chcp 65001 > nul
echo 正在编译游戏...
if not exist "out" mkdir out
javac -encoding UTF-8 -d out src\main\java\com\restaurant\model\*.java src\main\java\com\restaurant\game\*.java src\main\java\com\restaurant\ui\*.java src\main\java\com\restaurant\Main.java
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)
echo 编译成功！启动游戏...
java -Dfile.encoding=UTF-8 -cp out com.restaurant.Main
pause

