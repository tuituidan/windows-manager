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

echo. & pause