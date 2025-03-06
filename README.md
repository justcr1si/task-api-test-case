# 📌 Руководство по запуску проекта

## 📋 Требования
Перед запуском убедитесь, что у вас установлены:
- **Docker** и **Docker Compose**
- **Java 17+**
- **Maven 3+**
- **pgAdmin Desktop** (опционально для работы с БД)

## ⚙️ Настройка подключения к базе данных
(FIXME: они уже указаны в application.properties)
Перед запуском укажите в `application.properties` следующие параметры:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=cdh6ed39cz
```

---

## 🚀 Запуск окружения

### 1️⃣ Очистка проекта и упаковка
Команда ниже собирает проект **без запуска тестов** (если убрать `-DskipTests`, тесты будут выполняться):
```sh
mvn clean package -DskipTests
```

### 2️⃣ Запуск сервисов в контейнерах
Разверните все необходимые контейнеры:
```sh
docker-compose up -d --build
```

### 3️⃣ Запуск приложения
Перед запуском убедитесь, что контейнеры работают. Затем выполните команду:
```sh
mvn clean spring-boot:run -DskipTests
```

---

## 🌍 Доступ к сервисам
🔹 **Swagger UI** – [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
🔹 **pgAdmin** – [http://localhost:5050](http://localhost:5050)
   - **Логин:** `admin@admin.com`
   - **Пароль:** `admin`

---

## 🛠 Подключение к базе данных в pgAdmin Web
1. Авторизуйтесь в pgAdmin Web.
2. Нажмите ПКМ на **Servers → Register → Server**.
3. Введите следующие данные:
   - **Hostname:** `postgres`
   - **Username:** `postgres`
   - **Password:** `cdh6ed39cz`

### 🔄 Альтернативный вариант (pgAdmin Desktop)
Если возникают проблемы, попробуйте зайти через pgAdmin Desktop, используя данные из `application.properties`.

---

## 🛑 Остановка контейнеров
Для остановки всех запущенных контейнеров выполните команду:
```sh
docker-compose down
```

✅ Теперь ваш проект полностью настроен и готов к работе!

