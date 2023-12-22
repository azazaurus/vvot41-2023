# cloudphoto

## Порядок сборки

1. Установить JDK версии 11 или выше (например, [Oracle JDK](https://www.oracle.com/java/technologies/downloads/))
2. Установить [Maven](https://maven.apache.org/download.cgi) версии 3.8.* или выше, но ниже 4.0.0
3. В командной строке перейти в корневую папку репозитория и выполнить:
   ```
   mvn clean package -DskipTests
   ```

Результатом сборки является файл по пути target/cloudphoto-1.0.jar.

## Порядок запуска

1. Установить JRE версии 11 или выше
2. Запустить приложение в командной строке:
   ```
   java -jar cloudphoto-1.0.jar <параметры командной строки>
   ```
