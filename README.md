# java-explore-with-me

Сервис-афиша, где можно предложить какое-либо событие от выставки до похода в кино и набрать компанию для участия в нём

# Реализованные функции
* Создание и редактирование пользователей, категорий и подборок событий администратором.
* Создание и редактирование событий созданным пользователем. 
* Запросы пользователей на участие в событии - запрос, подтверждение, отклонение. 
* обытия и работа с ними - создание, модерация. 
* Добавление и удаление Лайков событиям, формирование рейтингов. 
* Сервис статистики, хранит количество просмотров определенного события

# Технологии, применяемые в проекте
* Java 11;
* Lombok;
* REST API;
* Spring;
* JDBCTemplate;
* PostgreSQL;
* Docker.


# Инструкция по развертыванию проекта
* Скачать данный репозиторий;
* MAVEN: mvn clean package;
* CMD: docker-compose up;

# Спецификация REST API swagger
<a href="https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json" target="_blank">Основной сервис</a> 

<a href="https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json" target="_blank">Сервис статистики</a> 
