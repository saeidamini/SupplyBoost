# Contributing to SupplyBoost

First off, thank you for considering contributing to SupplyBoost! It's people like you who make this a great learning resource for the community.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Project Structure](#project-structure)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

---

## Code of Conduct

This project adheres to the Contributor Covenant [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to the project maintainers.

---

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the [issue tracker](../../issues) to avoid duplicates.

**When submitting a bug report, include:**

- **Clear, descriptive title**
- **Steps to reproduce** the issue
- **Expected behavior** vs **actual behavior**
- **Screenshots** if applicable
- **Environment details:**
  - OS (Linux, macOS, Windows)
  - Java version (`java -version`)
  - Docker version (`docker --version`)
  - Maven version (`mvn --version`)
- **Relevant logs** or error messages

**Use this template:**

```markdown
## Bug Description
[Clear description of the bug]

## Steps to Reproduce
1. Start services with `docker-compose up`
2. Navigate to `http://localhost:8080`
3. Click on '...'
4. See error

## Expected Behavior
[What you expected to happen]

## Actual Behavior
[What actually happened]

## Environment
- OS: Ubuntu 22.04
- Java: OpenJDK 17.0.9
- Docker: 24.0.7
- Maven: 3.9.5

## Logs
```
[Paste relevant logs here]
```
```

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion:

- **Use a clear, descriptive title**
- **Provide detailed description** of the suggested enhancement
- **Explain why this enhancement would be useful** to most users
- **List alternative approaches** you've considered
- **Reference similar features** in other projects if applicable

### Contributing Code

We love pull requests! Here's how you can contribute:

1. **Find or create an issue** to work on
2. **Comment on the issue** to let others know you're working on it
3. **Fork the repository** and create a feature branch
4. **Write code** following our standards
5. **Write tests** for your changes
6. **Submit a pull request**

**Good first issues:** Look for issues labeled `good first issue` or `help wanted`

### Improving Documentation

Documentation improvements are always welcome:

- Fix typos or clarify existing documentation
- Add examples to existing docs
- Create new guides or tutorials
- Improve code comments
- Update outdated information

---

## Development Setup

### Prerequisites

Ensure you have the following installed:

- **Java 17+** - [OpenJDK](https://openjdk.org/) or [Azul Zulu](https://www.azul.com/downloads/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Docker 20+** and **Docker Compose** - [Get Docker](https://docs.docker.com/get-docker/)
- **Node.js 18+** (for frontend) - [Download](https://nodejs.org/)
- **Git** - [Download](https://git-scm.com/)
- **IDE** (recommended):
  - IntelliJ IDEA Community Edition or Ultimate
  - VS Code with Java Extension Pack

### Initial Setup

1. **Fork the repository** on GitHub

2. **Clone your fork:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SupplyBoost.git
   cd SupplyBoost
   ```

3. **Add upstream remote:**
   ```bash
   git remote add upstream https://github.com/deepkulk/SupplyBoost.git
   ```

4. **Verify Java version:**
   ```bash
   java -version
   # Should show Java 17 or higher
   ```

5. **Build the project:**
   ```bash
   mvn clean install
   ```

6. **Start infrastructure services:**
   ```bash
   cd infrastructure/docker-compose
   docker-compose up -d
   ```

7. **Verify services are running:**
   ```bash
   docker-compose ps
   ```

### IDE Configuration

#### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select `SupplyBoost` directory
   - Choose "Maven" project type
   - Wait for indexing to complete

2. **Configure Java SDK:**
   - File → Project Structure → Project
   - Set Project SDK to Java 17+
   - Set Language Level to 17

3. **Install Plugins:**
   - Lombok Plugin (for @Data, @Builder annotations)
   - SonarLint (code quality)
   - CheckStyle-IDEA (code style)

4. **Enable Annotation Processing:**
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Enable annotation processing ✓

#### VS Code

1. **Install Extensions:**
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (VMware)
   - Lombok Annotations Support
   - Docker
   - REST Client

2. **Configure Java:**
   - Open Command Palette (Cmd/Ctrl + Shift + P)
   - "Java: Configure Java Runtime"
   - Set Java 17+ as runtime

---

## Development Workflow

### Staying Up to Date

Before starting work, sync with upstream:

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

### Creating a Feature Branch

```bash
# Create and switch to a new branch
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/bug-description
```

**Branch naming conventions:**

- `feature/add-user-authentication` - New features
- `fix/order-validation-bug` - Bug fixes
- `docs/update-architecture` - Documentation updates
- `refactor/simplify-payment-service` - Code refactoring
- `test/add-integration-tests` - Test additions

### Making Changes

1. **Make your changes** in your feature branch
2. **Test locally:**
   ```bash
   # Run tests
   mvn test

   # Run integration tests
   mvn verify

   # Check code coverage
   mvn clean test jacoco:report
   open target/site/jacoco/index.html
   ```

3. **Format code:**
   ```bash
   mvn spotless:apply
   ```

4. **Verify build:**
   ```bash
   mvn clean install
   ```

### Running Services Locally

**Start infrastructure:**
```bash
cd infrastructure/docker-compose
docker-compose up -d
```

**Run a single service:**
```bash
cd services/order-management-service
mvn spring-boot:run
```

**Run all services (future):**
```bash
./scripts/run-all-services.sh
```

**Stop services:**
```bash
docker-compose down
```

---

## Coding Standards

### Java Code Style

We follow **Google Java Style Guide** with a few modifications.

**Key principles:**

- **Indentation:** 4 spaces (not tabs)
- **Line length:** Max 120 characters
- **Braces:** Required for all control structures
- **Imports:** No wildcard imports (no `import java.util.*`)
- **Naming:**
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

**Example:**

```java
package com.supplyboost.order.service;

import com.supplyboost.order.domain.Order;
import com.supplyboost.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final int MAX_RETRY_ATTEMPTS = 3;

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        Order order = Order.builder()
            .id(UUID.randomUUID())
            .userId(request.getUserId())
            .status(OrderStatus.PENDING)
            .build();

        return orderRepository.save(order);
    }
}
```

### Lombok Usage

Use Lombok to reduce boilerplate:

- `@Data` - For simple DTOs
- `@Builder` - For complex object construction
- `@RequiredArgsConstructor` - For dependency injection
- `@Slf4j` - For logging
- Avoid `@AllArgsConstructor` and `@NoArgsConstructor` unless necessary

### Code Comments

- **JavaDoc** for all public classes and methods
- **Inline comments** for complex logic only
- **No commented-out code** (use git history instead)

**Example JavaDoc:**

```java
/**
 * Processes payment for an order.
 *
 * @param orderId the unique identifier of the order
 * @param paymentRequest the payment details
 * @return payment confirmation with transaction ID
 * @throws PaymentException if payment processing fails
 */
public PaymentConfirmation processPayment(UUID orderId, PaymentRequest paymentRequest) {
    // Implementation
}
```

### Null Safety

- Use `Optional<T>` for return values that may be absent
- Never return `null` from public methods
- Use `@NonNull` annotations where appropriate
- Validate inputs at API boundaries

### Exception Handling

- Create custom exceptions for domain-specific errors
- Use `@ControllerAdvice` for global exception handling
- Log exceptions with appropriate levels:
  - `ERROR` - System errors, unexpected exceptions
  - `WARN` - Recoverable errors, validation failures
  - `INFO` - Business events (order created, payment processed)
  - `DEBUG` - Detailed diagnostic information

---

## Commit Guidelines

### Commit Message Format

We follow the **Conventional Commits** specification.

**Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Example:**
```
feat(order): add order cancellation endpoint

Implement REST endpoint to allow customers to cancel orders
within 1 hour of placement. Cancellation triggers inventory
release and payment refund saga.

Closes #42
```

### Commit Types

- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation changes
- `style` - Code formatting (no logic change)
- `refactor` - Code refactoring
- `test` - Adding or updating tests
- `chore` - Build process, dependencies, tooling
- `perf` - Performance improvements
- `ci` - CI/CD changes

### Commit Scopes

Use service names as scopes:

- `identity` - Identity service
- `order` - Order management service
- `inventory` - Inventory service
- `payment` - Payment service
- `frontend` - Frontend application
- `infra` - Infrastructure/DevOps
- `docs` - Documentation

### Commit Best Practices

- **Atomic commits:** One logical change per commit
- **Present tense:** "Add feature" not "Added feature"
- **Imperative mood:** "Fix bug" not "Fixes bug"
- **50-character subject line:** Keep it concise
- **Wrap body at 72 characters**
- **Reference issues:** Use `Closes #123` or `Fixes #456`

**Good commits:**
```
feat(payment): integrate Stripe payment gateway
fix(order): prevent duplicate order creation
docs(architecture): add saga pattern diagram
test(inventory): add integration tests for reservation
```

**Bad commits:**
```
updates
fixed stuff
WIP
asdfasdf
```

---

## Pull Request Process

### Before Submitting

- [ ] Code follows style guidelines (`mvn spotless:check`)
- [ ] All tests pass (`mvn verify`)
- [ ] New tests added for new functionality
- [ ] Code coverage is adequate (>70% for new code)
- [ ] Documentation updated (if applicable)
- [ ] No breaking changes (or clearly documented)
- [ ] Commits follow commit guidelines
- [ ] Branch is up to date with `main`

### Creating a Pull Request

1. **Push your branch:**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Open PR on GitHub:**
   - Navigate to your fork on GitHub
   - Click "Compare & pull request"
   - Fill out the PR template

3. **PR Title:** Follow commit message format
   ```
   feat(order): add order cancellation endpoint
   ```

4. **PR Description:** Use the template

```markdown
## Description
[Brief description of the changes]

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Related Issue
Closes #[issue number]

## How Has This Been Tested?
[Describe the tests you ran]

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed a self-review
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged and published

## Screenshots (if applicable)
[Add screenshots]
```

### Code Review Process

- **At least one approval** required before merging
- **Address all review comments** or provide reasoning
- **Re-request review** after making changes
- **Be responsive** to feedback
- **Be respectful** in discussions

### After Approval

- **Squash and merge** (for most PRs)
- **Rebase and merge** (for maintaining detailed history)
- **Delete branch** after merging

---

## Project Structure

```
SupplyBoost/
├── services/
│   └── [service-name]-service/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/supplyboost/[service]/
│       │   │   │   ├── config/       # Configuration classes
│       │   │   │   ├── controller/   # REST controllers
│       │   │   │   ├── domain/       # Domain entities
│       │   │   │   ├── dto/          # Data Transfer Objects
│       │   │   │   ├── repository/   # Data access
│       │   │   │   ├── service/      # Business logic
│       │   │   │   ├── exception/    # Custom exceptions
│       │   │   │   └── Application.java
│       │   │   └── resources/
│       │   │       ├── application.yml
│       │   │       ├── db/migration/ # Liquibase migrations
│       │   │       └── static/
│       │   └── test/
│       │       ├── java/
│       │       │   ├── unit/         # Unit tests
│       │       │   └── integration/  # Integration tests
│       │       └── resources/
│       ├── Dockerfile
│       ├── pom.xml
│       └── README.md
```

---

## Testing Guidelines

### Test Coverage Requirements

- **Minimum 70% overall coverage**
- **80%+ for business logic** (service layer)
- **100% for critical paths** (payment, order creation)

### Test Naming

```java
@Test
void shouldCreateOrderWhenValidRequest() {
    // Test implementation
}

@Test
void shouldThrowExceptionWhenInsufficientInventory() {
    // Test implementation
}
```

**Pattern:** `should[ExpectedBehavior]When[Condition]`

### Unit Tests

- Test business logic in isolation
- Mock all external dependencies
- Fast execution (< 1 second per test)

**Example:**

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderWhenInventoryAvailable() {
        // Given
        OrderRequest request = OrderRequest.builder()
            .userId(UUID.randomUUID())
            .build();
        when(inventoryService.checkAvailability(any()))
            .thenReturn(true);

        // When
        Order order = orderService.createOrder(request);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).save(any(Order.class));
    }
}
```

### Integration Tests

- Test actual database interactions
- Use Testcontainers for PostgreSQL, Kafka
- Test REST endpoints with MockMvc or WebTestClient

**Example:**

```java
@SpringBootTest
@Testcontainers
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateOrderSuccessfully() {
        webTestClient.post()
            .uri("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(orderRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").exists()
            .jsonPath("$.status").isEqualTo("PENDING");
    }
}
```

---

## Documentation

### When to Update Documentation

- Adding new features → Update `docs/PRD.md` and README
- Changing architecture → Update `docs/ARCHITECTURE.md`
- Making architectural decisions → Create new ADR in `docs/adr/`
- Changing API contracts → Update OpenAPI spec
- Changing deployment → Update `docs/RESTRUCTURING_PLAN.md` or create deployment guide

### Writing Good Documentation

- **Be concise** - Get to the point quickly
- **Use examples** - Show, don't just tell
- **Keep it current** - Update docs with code changes
- **Use diagrams** - Visual aids help understanding
- **Link related docs** - Help readers navigate

---

## Getting Help

- **GitHub Issues:** Ask questions labeled as `question`
- **GitHub Discussions:** For broader discussions
- **Discord/Slack:** (To be set up)
- **Email:** [maintainer email]

---

## Recognition

Contributors will be recognized in:

- GitHub contributors page
- Release notes (for significant contributions)
- Special `CONTRIBUTORS.md` file (planned)

---

**Thank you for contributing to SupplyBoost!** 🚀

