# Запуск проекта

## Требования
- Docker и Docker Compose
- Java 17+
- Maven 3+
- pgAdmin Desktop

## pgAdmin Desktop (в случае неполадок)
Введите следующие данные в соответствующие поля из application.properties:

- **spring.datasource.url=jdbc:postgresql://localhost:5432/postgres**
- **spring.datasource.username=postgres**
- **spring.datasource.password=cdh6ed39cz**

## Запуск окружения

Очистка и упаковка:

Запуск без тестов, если убрать -DskipTests будет запуск с тестами
```sh
mvn clean package -DskipTests
```

Запуск сервисов в контейнерах:
```sh
docker-compose up -d --build
```

## Запуск приложения
Перед запуском убедитесь, что контейнеры запущены.

### Запуск приложения
```sh
mvn clean spring-boot:run -DskipTests
```

## Доступ к сервисам
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- pgAdmin: [http://localhost:5050](http://localhost:5050) (логин: `admin@admin.com`, пароль: `admin`)

## pgAdmin Web
Залогиньтесь, после чего нажмите ПКМ на Servers -> Register -> Server
Введите следующие данные в соответствующие поля:

- **hostname=postgres**
- **username=postgres**
- **password=cdh6ed39cz**

### !!! В случае неполадок, пробуйте зайти через desktop pgAdmin по данным из application.properties !!!

- **spring.datasource.url=jdbc:postgresql://localhost:5432/postgres**
- **spring.datasource.username=postgres**
- **spring.datasource.password=cdh6ed39cz**

## Остановка контейнеров
```sh
docker-compose down
```