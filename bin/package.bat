@echo off

cd ..
echo ������
call mvn clean compile -DskipTests

echo ����ǰ��
cd web
call npm run build
cd ..

echo ����ǰ�˵����
call xcopy web\dist\* target\classes\static\ /E /I /Y

echo ���Ӧ��
call mvn package -DskipTests


echo ���Ӧ�õ�windows-manager.zip
call del/s/q windows-manager.zip
call rd/s/q windows-manager
md windows-manager

call copy bin\bin\* windows-manager\
call copy target\windows-manager.jar windows-manager\
call 7z a windows-manager.zip windows-manager

call rd/s/q windows-manager

echo. & pause