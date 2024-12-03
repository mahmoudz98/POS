# many of these commands are specific to my macOS setup
# so you may need to install some of these tools like fd, gum
OS := $(shell uname -s)

# Install dependencies based on the OS
install-deps:
ifeq ($(OS), Darwin) # macOS
	@echo "Installing dependencies for macOS..."
	@brew install fd gum openjdk
	@echo "Dependencies installed successfully!"
else ifeq ($(OS), Linux) # Linux (Fedora/Ubuntu)
	@echo "Installing dependencies for Linux..."
	@if [ -f /etc/fedora-release ]; then \
		echo "[charm]"; \
		echo "name=Charm"; \
		echo "baseurl=https://repo.charm.sh/yum/"; \
		echo "enabled=1"; \
		echo "gpgcheck=1"; \
		echo "gpgkey=https://repo.charm.sh/yum/gpg.key"; \
		sudo tee /etc/yum.repos.d/charm.repo > /dev/null; \
		sudo rpm --import https://repo.charm.sh/yum/gpg.key; \
		sudo dnf install -y fd-find gum java-latest-openjdk-devel; \
	elif [ -f /etc/lsb-release ]; then \
		sudo apt update && sudo apt install -y fd-find openjdk-11-jdk; \
		brew install gum || echo "Ensure Homebrew is installed for gum."; \
	fi
	@echo "Dependencies installed successfully!"
else
	@echo "Unsupported OS: $(OS). Install dependencies manually."
endif

clean: clean-build
	@gum log -l debug "removing .gradle directories"
	@fd -u -t d '^.gradle$$' -X rm -Rf
	@gum log -l debug "removing .kotlin directories"
	@fd -u -t d '^.kotlin$$' -X rm -Rf
	@gum log -l debug "removing .DS_Store files"
	@fd -u -tf ".DS_Store" -X rm
	@gum log -l debug "remove empty directories, suppressing error messages"
	@fd -u -td -te -X rmdir

clean-build:
	@gum log -l info "This script will clean the build folders & cache"
	@gum log -l debug "removing build directories"
	@fd -u -t d '^build$$' -X rm -Rf

kill-ksp:
	@gum log -l info "This script will kill your kotlin daemon (useful for ksp errors)"
	@jps | grep -E 'KotlinCompileDaemon' | awk '{print $$1}' | xargs kill -9 || true

b:
	@gum log -l info "This script will assemble the debug app (without linting)"
	@./gradlew assembleDebug -x lint

build:
	@gum log -l info "This script will assemble the project (without linting)"
	@./gradlew assemble -x lint

lint:
	@gum log -l info "This script will run lint checks"
	@./gradlew lint

lint-update:
	@gum log -l info "Update the baseline for lint"
	@./gradlew updateLintBaseline

tests:
	@echo "Run all unit tests without linting"
	./gradlew tests -x lint

spotlessCheck:
	@echo "Run spotless check"
	./gradlew spotlessCheck --init-script gradle/init.gradle.kts --no-configuration-cache

updateProdReleaseBadging:
	@echo "Run updateProdReleaseBadging"
	./gradlew updateProdReleaseBadging

updateDependency:
	@echo "Run updateDependencyGuard"
	./gradlew dependencyGuardBaseline

#tests-screenshots:
#	@echo "Verify all screenshots"
#	./gradlew verifyPaparazziDebug
#
#record-screenshots:
#	@echo "Record all screenshots"
#	./gradlew recordPaparazziDebug