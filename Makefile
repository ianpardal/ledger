.PHONY: help build test lint format

help:
	@echo "Available targets:"
	@echo "  build   - Compile the project"
	@echo "  test    - Run unit tests"
	@echo "  lint    - Run formatting checks"
	@echo "  format  - Run auto-format"

build:
	./mvnw package -DskipTests -q

test:
	./mvnw test

lint:
	./mvnw spotless:check

format:
	./mvnw spotless:apply