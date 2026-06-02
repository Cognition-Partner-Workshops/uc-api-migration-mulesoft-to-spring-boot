.PHONY: build test verify db-up db-down clean

# Build the Spring Boot application
build:
	cd spring-boot-app && ./mvnw clean compile -q

# Run unit tests
test:
	cd spring-boot-app && ./mvnw test -q

# Full verification: build + unit tests + contract tests (requires running app)
verify: build test
	@echo "--- Contract verification against running app ---"
	cd verify && ./mvnw verify -Dapp.base-url=http://localhost:8080 -q

# Start local PostgreSQL
db-up:
	docker compose -f docker/docker-compose.yml up -d

# Stop local PostgreSQL
db-down:
	docker compose -f docker/docker-compose.yml down -v

# Package the Spring Boot app
package:
	cd spring-boot-app && ./mvnw clean package -DskipTests -q

# Run the app locally (requires db-up first)
run: db-up package
	cd spring-boot-app && java -jar target/employee-service-1.0.0-SNAPSHOT.jar

# Clean all build artifacts
clean:
	cd spring-boot-app && ./mvnw clean -q
	cd verify && ./mvnw clean -q
