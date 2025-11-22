# Repository Restructuring Plan

**Version:** 1.0
**Date:** 2025-11-09
**Status:** Ready for Implementation
**Estimated Time:** 2-3 hours

---

## Executive Summary

This document outlines the step-by-step plan to transform the current broken SupplyBoost repository into a well-organized, professional monorepo suitable for development.

**Current State:**
- ❌ Uninitialized git submodules (empty directories)
- ❌ 3MB backup bundle bloating repository
- ❌ Parent POM referencing non-existent modules
- ❌ Misleading documentation
- ❌ No actual code or structure

**Target State:**
- ✅ Clean monorepo structure with proper directories
- ✅ Professional documentation
- ✅ Working parent POM
- ✅ Clear contribution guidelines
- ✅ Proper .gitignore
- ✅ Ready for Week 1 development

---

## Restructuring Checklist

### Phase 1: Cleanup (30 minutes)

- [ ] Remove git submodules
- [ ] Remove backup bundle
- [ ] Clean up resource directory
- [ ] Archive old Milestone.md

### Phase 2: Directory Structure (15 minutes)

- [ ] Create monorepo directory structure
- [ ] Move existing diagrams to appropriate location
- [ ] Organize documentation

### Phase 3: Configuration Files (45 minutes)

- [ ] Update parent POM
- [ ] Update .gitignore
- [ ] Add LICENSE file
- [ ] Create .editorconfig

### Phase 4: Documentation (60 minutes)

- [x] Create docs/PRD.md ✅
- [x] Create docs/ARCHITECTURE.md ✅
- [x] Create docs/ROADMAP.md ✅
- [x] Create docs/adr/ framework ✅
- [ ] Rewrite README.md
- [ ] Create CONTRIBUTING.md
- [ ] Create CODE_OF_CONDUCT.md

### Phase 5: Developer Experience (30 minutes)

- [ ] Create setup script
- [ ] Create initial docker-compose.yml
- [ ] Add Makefile for common commands

---

## Detailed Implementation Steps

### Phase 1: Cleanup

#### Step 1.1: Remove Git Submodules

**Current Problem:** Submodules `shop/` and `account/` are empty placeholders pointing to unrelated repositories.

**Action:**
```bash
# Remove submodule directories from git
git rm -r shop account

# Remove .gitmodules file
git rm .gitmodules

# Verify removal
git status
```

**Verification:**
- `shop/` and `account/` directories should be gone
- `.gitmodules` should be removed
- Changes staged for commit

---

#### Step 1.2: Remove Backup Bundle

**Current Problem:** `resource/repo-archive-backup.bundle` is 3MB and shouldn't be in version control.

**Action:**
```bash
# Remove from git
git rm resource/repo-archive-backup.bundle

# Optional: Upload to external storage for safekeeping
# (Google Drive, Dropbox, or external backup service)
```

**Verification:**
- Bundle removed from repository
- Repository size reduced

**Note:** If you ever need the old code, restore from the bundle with:
```bash
git clone repo-archive-backup.bundle old-codebase
```

---

#### Step 1.3: Reorganize Resource Directory

**Current Structure:**
```
resource/
├── banner.jpg
├── BuyProductFromRetail.drawio
├── BuyProductFromRetail.svg
├── SupplyChainProcess.drawio.svg
├── Milestone.md  # Outdated
└── repo-archive-backup.bundle  # Removed
```

**Target Structure:**
```
docs/
├── assets/
│   ├── banner.jpg
│   └── diagrams/
│       ├── BuyProductFromRetail.drawio
│       ├── BuyProductFromRetail.svg
│       └── SupplyChainProcess.drawio.svg
└── archive/
    └── Milestone-v0.md  # Old roadmap for reference
```

**Action:**
```bash
# Create new directories
mkdir -p docs/assets/diagrams
mkdir -p docs/archive

# Move files
mv resource/banner.jpg docs/assets/
mv resource/*.drawio resource/*.svg docs/assets/diagrams/
mv resource/Milestone.md docs/archive/Milestone-v0.md

# Remove empty resource directory
rmdir resource

# Add to git
git add docs/
git rm -r resource/
```

---

### Phase 2: Directory Structure

#### Step 2.1: Create Monorepo Structure

**Target Structure:**
```
SupplyBoost/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── deploy.yml
├── services/
│   ├── identity-service/
│   ├── product-catalog-service/
│   ├── shopping-cart-service/
│   ├── order-management-service/
│   ├── inventory-service/
│   ├── payment-service/
│   ├── shipping-service/
│   ├── notification-service/
│   └── accounting-service/
├── frontend/
│   └── web-app/
├── infrastructure/
│   ├── docker-compose/
│   │   ├── docker-compose.yml
│   │   └── .env.example
│   └── kubernetes/
│       ├── base/
│       └── overlays/
├── docs/
│   ├── PRD.md ✅
│   ├── ARCHITECTURE.md ✅
│   ├── ROADMAP.md ✅
│   ├── CONTRIBUTING.md
│   ├── CODE_OF_CONDUCT.md
│   ├── adr/ ✅
│   ├── assets/
│   └── archive/
├── scripts/
│   ├── setup-dev-env.sh
│   ├── run-all-tests.sh
│   ├── build-all.sh
│   └── deploy.sh
├── .editorconfig
├── .gitignore
├── pom.xml
├── README.md
├── CONTRIBUTING.md
├── LICENSE
└── Makefile
```

**Action:**
```bash
# Create service directories
mkdir -p services/{identity,product-catalog,shopping-cart,order-management,inventory,payment,shipping,notification,accounting}-service

# Create frontend directory
mkdir -p frontend/web-app

# Create infrastructure directories
mkdir -p infrastructure/docker-compose
mkdir -p infrastructure/kubernetes/{base,overlays/dev,overlays/staging,overlays/prod}

# Create scripts directory
mkdir -p scripts

# Create GitHub Actions directory
mkdir -p .github/workflows

# Add .gitkeep to preserve empty directories
find services -type d -empty -exec touch {}/.gitkeep \;
find frontend -type d -empty -exec touch {}/.gitkeep \;
```

**Verification:**
```bash
tree -L 2 -a
```

---

### Phase 3: Configuration Files

#### Step 3.1: Update Parent POM

**Current pom.xml Problems:**
- References non-existent modules (`core`, `notify`, `shop`)
- Minimal configuration

**Target pom.xml:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.supplyboost</groupId>
    <artifactId>supplyboost-parent</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>SupplyBoost</name>
    <description>Educational supply chain management platform</description>
    <url>https://github.com/deepkulk/SupplyBoost</url>

    <properties>
        <!-- Java Version -->
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Spring Boot Version -->
        <spring-boot.version>3.2.0</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>

        <!-- Dependency Versions -->
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <postgresql.version>42.7.1</postgresql.version>
        <testcontainers.version>1.19.3</testcontainers.version>
        <kafka.version>3.6.0</kafka.version>

        <!-- Plugin Versions -->
        <maven-compiler-plugin.version>3.11.0</maven-compiler-plugin.version>
        <maven-surefire-plugin.version>3.2.2</maven-surefire-plugin.version>
        <jacoco-maven-plugin.version>0.8.11</jacoco-maven-plugin.version>
        <spotless-maven-plugin.version>2.41.0</spotless-maven-plugin.version>
    </properties>

    <modules>
        <!-- Microservices will be added as they are created -->
        <!-- <module>services/identity-service</module> -->
        <!-- <module>services/product-catalog-service</module> -->
        <!-- ... -->
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud BOM -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- PostgreSQL Driver -->
            <dependency>
                <groupId>org.postgresql</groupId>
                <artifactId>postgresql</artifactId>
                <version>${postgresql.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>

            <!-- Testcontainers BOM -->
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <!-- Spring Boot Maven Plugin -->
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>

                <!-- Maven Compiler Plugin -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>${maven-compiler-plugin.version}</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>

                <!-- Maven Surefire (Unit Tests) -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>${maven-surefire-plugin.version}</version>
                </plugin>

                <!-- JaCoCo (Code Coverage) -->
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>${jacoco-maven-plugin.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>prepare-agent</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>report</id>
                            <phase>test</phase>
                            <goals>
                                <goal>report</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>

                <!-- Spotless (Code Formatting) -->
                <plugin>
                    <groupId>com.diffplug.spotless</groupId>
                    <artifactId>spotless-maven-plugin</artifactId>
                    <version>${spotless-maven-plugin.version}</version>
                    <configuration>
                        <java>
                            <googleJavaFormat>
                                <version>1.18.1</version>
                                <style>GOOGLE</style>
                            </googleJavaFormat>
                        </java>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

</project>
```

**Action:** Replace existing pom.xml with the above content.

---

#### Step 3.2: Update .gitignore

**Current .gitignore:** Basic Java/Maven/Node ignores

**Enhanced .gitignore:**

```gitignore
# Java
*.class
*.jar
*.war
*.ear
*.log
*.ctxt
.mtj.tmp/
hs_err_pid*
replay_pid*

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr
out/
.idea_modules/

# Eclipse
.classpath
.project
.settings/
bin/

# VS Code
.vscode/
*.code-workspace

# macOS
.DS_Store
.AppleDouble
.LSOverride
._*

# Linux
*~
.directory

# Windows
Thumbs.db
ehthumbs.db
Desktop.ini

# Node.js (for frontend)
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
.npm
.yarn/
dist/
.cache/

# Docker
.docker/

# Environment Variables
.env
.env.local
.env.*.local
*.env

# Logs
logs/
*.log
npm-debug.log*
yarn-debug.log*

# Test Coverage
coverage/
*.lcov
.nyc_output/

# Temporary Files
*.tmp
*.temp
*.swp
*.swo
*.bak

# Database
*.db
*.sqlite
*.sqlite3

# Secrets (extra safety)
secrets/
*-secret.yaml
credentials.json
service-account.json

# Build Artifacts
build/
dist/
*.zip
*.tar.gz
*.bundle  # Prevent backup bundles from being committed again!

# IDEs
nbproject/
.nb-gradle/

# Spring Boot
spring-boot-devtools.properties

# JaCoCo
jacoco.exec

# OS generated files
.Spotlight-V100
.Trashes
```

**Action:** Replace .gitignore with enhanced version

---

#### Step 3.3: Add LICENSE

**Action:** Create LICENSE file with Apache 2.0 (as mentioned in old README)

```bash
cat > LICENSE << 'EOF'
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
   ... [full Apache 2.0 license text]

   Copyright 2025 SupplyBoost Contributors

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
EOF
```

---

#### Step 3.4: Add .editorconfig

**Purpose:** Consistent code formatting across editors

```bash
cat > .editorconfig << 'EOF'
# EditorConfig: https://EditorConfig.org

root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.{java,kt}]
indent_style = space
indent_size = 4
max_line_length = 120

[*.{xml,html}]
indent_style = space
indent_size = 2

[*.{yml,yaml}]
indent_style = space
indent_size = 2

[*.{js,jsx,ts,tsx,vue}]
indent_style = space
indent_size = 2

[*.md]
trim_trailing_whitespace = false
max_line_length = off

[Makefile]
indent_style = tab
EOF
```

---

### Phase 4: Documentation

#### Step 4.1: Rewrite README.md

See separate file creation (will create next)

#### Step 4.2: Create CONTRIBUTING.md

See separate file creation (will create next)

#### Step 4.3: Create CODE_OF_CONDUCT.md

Standard Contributor Covenant

---

### Phase 5: Developer Experience

#### Step 5.1: Create Makefile

```makefile
.PHONY: help clean build test run docker-up docker-down

help:
	@echo "SupplyBoost - Available Commands:"
	@echo "  make build        - Build all services"
	@echo "  make test         - Run all tests"
	@echo "  make run          - Run infrastructure with Docker Compose"
	@echo "  make docker-up    - Start all Docker services"
	@echo "  make docker-down  - Stop all Docker services"
	@echo "  make clean        - Clean build artifacts"

clean:
	mvn clean

build:
	mvn clean install -DskipTests

test:
	mvn test

run:
	cd infrastructure/docker-compose && docker-compose up

docker-up:
	cd infrastructure/docker-compose && docker-compose up -d

docker-down:
	cd infrastructure/docker-compose && docker-compose down
```

---

## Post-Restructuring Verification

### Checklist

After completing all steps:

- [ ] Repository is clean (no submodules, no bundle)
- [ ] Directory structure matches target
- [ ] `mvn clean install` succeeds (even with no modules yet)
- [ ] All documentation files present
- [ ] LICENSE file exists
- [ ] .gitignore covers all important patterns
- [ ] README.md is professional and accurate
- [ ] Git history is clean (sensible commit messages)

### Test Commands

```bash
# Verify structure
tree -L 2

# Verify Maven
mvn validate

# Verify Git
git status
git log --oneline -5

# Verify documentation
ls -la docs/
```

---

## Rollback Plan

If anything goes wrong during restructuring:

**Option 1: Git Reset (if not pushed)**
```bash
git reset --hard HEAD~1  # Undo last commit
```

**Option 2: Restore from Bundle (nuclear option)**
```bash
# If you still have the bundle elsewhere
git clone /path/to/repo-archive-backup.bundle old-state
# Manually recover files
```

---

## Timeline

| Task | Estimated Time | Priority |
|------|---------------|----------|
| Phase 1: Cleanup | 30 min | CRITICAL |
| Phase 2: Directory Structure | 15 min | CRITICAL |
| Phase 3: Configuration | 45 min | HIGH |
| Phase 4: Documentation | 60 min | HIGH |
| Phase 5: Developer Experience | 30 min | MEDIUM |
| **Total** | **3 hours** | |

---

## Success Criteria

Restructuring is complete when:

1. ✅ New developer can clone repository and understand structure immediately
2. ✅ README accurately describes the project and how to get started
3. ✅ No broken references (submodules, non-existent files)
4. ✅ Maven parent POM validates successfully
5. ✅ All documentation is professional and comprehensive
6. ✅ Repository is ready for Week 1 development work

---

## Next Steps (After Restructuring)

Once restructuring is complete:

1. **Week 1 Development:**
   - Create `infrastructure/docker-compose/docker-compose.yml`
   - Add PostgreSQL, Redis, Kafka, Elasticsearch services
   - Test local environment startup

2. **First Service:**
   - Scaffold Identity Service using Spring Initializr
   - Add as first module to parent POM
   - Create Dockerfile
   - Update docs/ROADMAP.md to check off Week 1 tasks

3. **CI/CD:**
   - Create `.github/workflows/ci.yml`
   - Set up automated build and test
   - Configure code coverage reporting

---

**Document Status:** Ready for Implementation
**Approval:** Engineering Team
**Implementation Date:** 2025-11-09

