.PHONY: help build test lint format run

help:
	@echo "Available targets:"
	@echo "  build   - Compile the project"
	@echo "  test    - Run unit tests"
	@echo "  lint    - Run formatting checks"
	@echo "  format  - Run auto-format"
	@echo "  run     - Start server"

build:
	./mvnw package -DskipTests -q

test:
	./mvnw test

lint:
	./mvnw spotless:check

format:
	./mvnw spotless:apply

run:
	./mvnw spring-boot:run