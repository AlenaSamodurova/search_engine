# Поисковый движок  
## Функции поискового "движка"  
1.Индексация WEB-сайтов    
2.Поиск лемм  
3.Получение результатов индексации и лемматизации  
4.Осуществление поиска в индексированном контенте    
## СТЭК
Java version 17, Spring Boot version 2.5.7, maven, Hibernate, migratiom FlyWay Db, Swagger Api, Lombok,JSOUP, DB MySQL8O, Morphology Library
## Использование
Перед использованием необходимо отредактировать файл *application.yaml*
````

server:
  port: 8080
spring:
  datasource:
    username: {}
    password:{}
    url: jdbc:mysql://localhost:3306/search_engine?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: update
    #    show-sql: true

indexing-settings:
  sites:
  - url:
    name:
  - url:  
    name:...
    
    
jsoup-setting:
  jsoup:
    userAgent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0
    referrer: http://www:google.com
    timeout: 10000
    ignoreHttpErrors: true
    followRedirects: false
```
