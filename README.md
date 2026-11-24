# Курсова робота: інформаційна система планування заходів енергоаудиту (Варіант №9)

## Про проєкт

Система зберігає об'єкти (будівлі), енергоаудити цих об'єктів, заходи, запропоновані за результатами аудитів, і записи про споживання ресурсів. Для кожного заходу сервер рахує інтегральний показник I = 0,40·F1 + 0,35·F2 + 0,25·F3 (F1, F2, F3 вводяться від 0 до 100) і стежить за станом заходу: PROPOSED → APPROVED → IMPLEMENTATION → COMPLETED. Інші переходи сервер відхиляє.

Проєкт складається з двох окремих Maven-модулів. Сервер (server) збирається у WAR і працює в Apache Tomcat: Servlet приймає HTTP-запити, Service перевіряє дані та правила, DAO через JDBC працює з MySQL, а відповіді повертаються у форматі JSON. Клієнт (client) – це JavaFX-програма. Вона показує дані в таблицях, дозволяє додавати, змінювати й видаляти записи, змінювати стан заходу та імпортувати споживання з CSV-файлу. До бази клієнт не підключається, усі дані він отримує від сервера через HTTP.

Схема роботи: JavaFX-клієнт → HTTP/JSON → Servlet → Service → DAO → JDBC → MySQL.

## Що використано

- Java 21
- Maven
- Apache Tomcat 9.x
- Servlet API 4.0.1 (javax.servlet)
- MySQL та JDBC (mysql-connector-j 9.7.0)
- Jackson Databind 2.18.2
- JavaFX 21.0.2
- JUnit 5 (junit-jupiter 5.11.4)
- Git

## Структура

```
kpp-coursework-zhmur-variant9/
├── README.md
├── .gitignore
├── database.sql
├── client/
│   ├── pom.xml
│   ├── sample-data/
│   │   └── consumption_records.csv
│   └── src/main/java/ua/edu/nupp/kpp/client/
└── server/
    ├── pom.xml
    └── src/
       ├── main/java/ua/edu/nupp/kpp/server/
       ├── main/webapp/WEB-INF/web.xml
       └── test/java/ua/edu/nupp/kpp/server/
```

database.sql містить схему бази та демонстраційні дані. У server/src/main/java лежать моделі, DAO, Service, Servlet і DBUtil, а в server/src/test/java JUnit-тести. У client/src/main/java лежать моделі, HTTP-клієнт, клас фонового імпорту ImportRecordsTask і головне вікно MainApp.

## Запуск

1. Запустіть MySQL і виконайте database.sql ( через phpMyAdmin, вкладка «Імпорт» на рівні сервера). Буде створено базу energy_audit_db із таблицями object_item, energy_audit, measure, consumption_record.
2. Зберіть сервер: cd server, потім mvn clean package.
3. Скопіюйте server/target/energy-audit-server.war у каталог webapps Tomcat 9 і запустіть Tomcat.
4. Перевірте сервер у браузері (має відобразитися JSON):
    - http://localhost:8080/energy-audit-server/objects
    - http://localhost:8080/energy-audit-server/audits
    - http://localhost:8080/energy-audit-server/measures
    - http://localhost:8080/energy-audit-server/records
5. Запустіть клієнт: cd client, потім mvn javafx:run.

Потрібен JDK 21 (змінна JAVA_HOME) і саме Tomcat 9: код використовує javax.servlet, а Tomcat 10 і 11 працюють з jakarta. Параметри підключення до MySQL (URL, USER, PASSWORD) задаються в server/src/main/java/ua/edu/nupp/kpp/server/DBUtil.java. Типово це localhost:3306, користувач root і порожній пароль; свій справжній пароль змінюйте лише локально й не публікуйте.

Адреси сервера: /objects, /audits, /measures, /records. GET повертає список у JSON. POST приймає параметр action: add, update, delete, а для заходів ще й transition. Помилки повертаються як {"error":"..."} з кодом 400 (некоректні дані) або 500 (збій сервера).

## Тести

```
cd server
mvn clean test
```

Запускається 17 тестів JUnit 5, MySQL і Tomcat для цього не потрібні. Тести перевіряють розрахунок інтегрального показника, дозволені й заборонені переходи станів і перевірку даних.

## CSV-імпорт

Файл: client/sample-data/consumption_records.csv. Кодування UTF-8, роздільник `;`, дробові числа з крапкою. Перший рядок із заголовком обов'язковий:

```
object_id;resource_type;consumption_value;period
1;Електроенергія;11800.5;2026-02
```

Щоб імпортувати, відкрийте у клієнті вкладку «Споживання» і натисніть «Імпортувати CSV». Імпорт працює у фоновому потоці (JavaFX Task), вікно не зависає, внизу видно індикатор і підсумок. Клієнт потрібно запускати з каталогу client, бо шлях до файлу відносний.

Некоректні рядки пропускаються, а якщо сервер відхилив запис (наприклад, немає такого object_id), імпорт зупиняється з номером рядка в повідомленні.

Перевірки на дублікати немає: якщо імпортувати той самий файл двічі, записи додадуться знову. Щоб повернути базу до початкового стану, ще раз виконайте database.sql.