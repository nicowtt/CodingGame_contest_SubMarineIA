

@echo off
SET PATH_OUT=C:\Users\nicob\Desktop\CodingGame_contest_SubMarineIA\SandBox\out

cd C:\Users\nicob\Desktop\CodingGame_contest_SubMarineIA\SandBox\out
type nul > Player.java

cd C:\Users\nicob\Desktop\CodingGame_contest_SubMarineIA\SandBox\src

for /r %a in (*) do findstr "import" %~nxa >> %PATH_OUT%\Player.java
for /r %a in (*) do findstr /V "import" %~nxa >> %PATH_OUT%\Player.java
