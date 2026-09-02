# Mock Employee API

Development-only mock backend and employee directory UI for local frontend work.

## Mock files

- `spring-boot-app/src/main/java/com/workshop/employee/mock/MockEmployeeApiController.java`
- `spring-boot-app/src/main/resources/application-mock.yml`

Run it with:

```bash
cd spring-boot-app && JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./mvnw spring-boot:run -Dspring-boot.run.profiles=mock
```

The static UI files are **NOT mocks** and must be kept:
`spring-boot-app/src/main/resources/static/index.html`,
`spring-boot-app/src/main/resources/static/app.js`, and
`spring-boot-app/src/main/resources/static/styles.css`.
