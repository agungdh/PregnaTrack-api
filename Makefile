.PHONY: help reset down up logs

.DEFAULT_GOAL := help

help:  ## Show this help message
	@awk 'BEGIN {FS = ":.*##"; printf "Usage:\n  make \033[36m<target>\033[0m\n\nTargets:\n"} \
		/^[a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2 }' \
		$(MAKEFILE_LIST)

reset:  ## Stop containers, remove all volumes, then bring them up again
	docker compose down -v
	docker compose up -d

down:  ## Stop and remove containers (keep volumes)
	docker compose down

up:  ## Start all containers in the background
	docker compose up -d

logs:  ## Follow logs from all services
	docker compose logs -f
