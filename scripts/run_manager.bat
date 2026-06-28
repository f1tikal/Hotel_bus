@echo off
"C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\jbr\bin\java.exe" -Ddb.type=postgresql -Ddb.url="jdbc:postgresql://192.168.56.1:5432/hotel" -Ddb.user=hotel_user -Ddb.pass=sa -jar ../target/desktop-1.0.0.jar
pause