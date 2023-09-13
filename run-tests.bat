rd \allure-results /q /s

mvn -Dgroups=Search clean test

mkdir .\allure-results\history\

copy .\allure-report\history\ .\allure-results\history\

allure generate --clean

allure open