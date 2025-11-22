# ADR-005: Repository Structure - Monorepo Approach

**Status:** Accepted
**Date:** 2025-11-09
**Deciders:** Engineering Team
**Technical Story:** Repository restructuring

## Context

SupplyBoost consists of multiple microservices, a frontend application, infrastructure configuration, and shared documentation. We need to decide how to organize the code in version control.

**Current State:**
- Broken repository with git submodules pointing to external repos that don't align with the project
- 3MB backup bundle in resource directory
- No actual code in repository
- Confusing structure for new developers

**Requirements:**
- Easy for single developer to work across services
- Simple onboarding for new contributors
- Atomic commits across multiple services when needed
- Shared tooling and dependencies
- Consistent versioning

**Options:**
1. **Monorepo:** All services in one repository
2. **Multirepo:** Each service in its own repository
3. **Hybrid:** Core services in monorepo, frontend/infrastructure separate

## Decision

We will use a **monorepo** structure where all microservices, frontend, and infrastructure code live in a single Git repository.

**Repository Structure:**
```
SupplyBoost/
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
│   ├── kubernetes/
│   └── terraform/ (future)
├── docs/
│   ├── PRD.md
│   ├── ARCHITECTURE.md
│   ├── ROADMAP.md
│   └── adr/
├── scripts/
│   ├── setup-dev-environment.sh
│   ├── run-tests.sh
│   └── deploy.sh
├── pom.xml (parent POM)
├── README.md
├── CONTRIBUTING.md
└── LICENSE
```

## Consequences

### Positive

- **Single Source of Truth:** All code in one place, one `git clone` to get everything
- **Atomic Changes:** Can update multiple services in a single commit when refactoring contracts
- **Simplified Dependency Management:** Parent POM manages all versions centrally
- **Easier Refactoring:** Search/replace across all services, IDE sees all code
- **Unified CI/CD:** Single pipeline can build and test all services
- **Better for Learning:** New developers see entire system in one place
- **Consistent Tooling:** Shared linting, formatting, testing configurations
- **Cross-Service Testing:** Integration tests can reference multiple services easily
- **Documentation Proximity:** Docs live alongside code they describe
- **Single Issue Tracker:** All issues/PRs in one place

### Negative

- **Repository Size:** Will grow larger over time (mitigated: no binary artifacts, proper .gitignore)
- **Clone Time:** Slightly longer to clone (minimal impact for small team)
- **Git History Noise:** Commits to one service appear in global history (mitigated: good commit messages)
- **CI/CD Build Time:** Must be smart about only building changed services (can be optimized)
- **Access Control Complexity:** Can't give different teams access to different services (not relevant for solo/small team)

### Neutral

- **Versioning Strategy:** Will need to decide between unified versioning vs per-service versioning
- **Build Tool:** Maven multi-module project fits well with monorepo

## Alternatives Considered

### Alternative 1: Multirepo (Polyrepo)

**Description:** Each microservice gets its own Git repository.

**Structure:**
```
supplyboost-identity-service (repo 1)
supplyboost-product-catalog-service (repo 2)
supplyboost-order-management-service (repo 3)
... (9+ repositories)
supplyboost-infrastructure (repo N)
supplyboost-docs (repo N+1)
```

**Pros:**
- Service independence (true isolated ownership)
- Smaller repository size per service
- Can use different build tools per service if desired
- Fine-grained access control per service
- Clear service boundaries enforced by repo boundaries

**Cons:**
- **Onboarding Complexity:** New developer must clone 10+ repositories
- **Cross-Service Changes:** Breaking API changes require coordinated PRs across repos
- **Dependency Hell:** Managing shared library versions across repos
- **Tooling Fragmentation:** Each repo may drift in tooling choices
- **Discoverability:** Hard to find related code, jump between repos
- **CI/CD Complexity:** Need separate pipelines for each repo or meta-build system
- **Documentation Scattered:** Architecture docs separated from implementation
- **Not Ideal for Solo Developer:** Overhead of managing multiple repos outweighs benefits

**Reason for rejection:** For a learning project with a single primary developer (or small team), the coordination overhead of multirepo far outweighs the benefits. Multirepo shines with large organizations with many independent teams—not our use case.

### Alternative 2: Git Submodules (Current Broken Approach)

**Description:** Meta-repository with each service as a Git submodule.

**Pros:**
- Services are independent repositories
- Parent repo provides unified view

**Cons:**
- **Fragile:** Easy to get into detached HEAD state
- **Confusing:** Many developers struggle with submodule workflows
- **Update Friction:** Updating submodules requires extra steps
- **CI/CD Complexity:** Must recursively clone and manage submodule versions
- **Already Failed:** Previous attempt resulted in broken, uninitialized submodules

**Reason for rejection:** Git submodules are notoriously difficult to work with and add little value for our use case. The current repository already has broken submodules—a sign this approach doesn't fit our needs.

### Alternative 3: Hybrid Approach

**Description:** Backend services in one monorepo, frontend in separate repo, infrastructure in third repo.

**Pros:**
- Groups related concerns
- Frontend team can work independently (if team grows)

**Cons:**
- **Fragmentation:** Still managing 3 repositories
- **Cross-Cutting Changes:** Updates affecting backend + frontend require coordinated PRs
- **Partial Improvement:** Doesn't fully solve multirepo problems

**Reason for rejection:** For a solo/small team, this splits the difference without capturing the full benefits of either approach. If we're going to split, go full multirepo; if staying together, go full monorepo.

## Implementation Plan

1. **Remove Git Submodules:**
   ```bash
   git rm -r shop account
   rm -rf .gitmodules
   ```

2. **Remove Backup Bundle:**
   ```bash
   git rm resource/repo-archive-backup.bundle
   # Store bundle elsewhere (Google Drive, Dropbox) for historical reference
   ```

3. **Create Directory Structure:**
   ```bash
   mkdir -p services/{identity,product-catalog,shopping-cart,order-management,inventory,payment,shipping,notification,accounting}-service
   mkdir -p frontend/web-app
   mkdir -p infrastructure/{docker-compose,kubernetes}
   mkdir -p scripts
   ```

4. **Update Parent POM:**
   - Add all services as `<modules>`
   - Configure shared dependency management
   - Set up common build plugins

5. **Update Documentation:**
   - Rewrite README.md with correct structure
   - Update CONTRIBUTING.md with monorepo workflows
   - Document how to work with monorepo in IDE

## Versioning Strategy

**Decision:** Unified versioning for MVP, reevaluate later.

- All services share version number (e.g., `v0.1.0`)
- Simplified for learning and demos
- Can switch to independent versioning if project grows

## Build Strategy

**Selective Building:**
```bash
# Build all services
mvn clean install

# Build single service
mvn clean install -pl services/order-management-service

# Build service and its dependencies
mvn clean install -pl services/order-management-service -am
```

**CI Optimization (Future):**
- Use GitHub Actions matrix strategy
- Detect changed files and build only affected services
- Cache Maven dependencies between builds

## References

- [Monorepo Tools](https://monorepo.tools/)
- [Google's Monorepo Approach](https://cacm.acm.org/magazines/2016/7/204032-why-google-stores-billions-of-lines-of-code-in-a-single-repository/fulltext)
- [Advantages of Monorepos](https://danluu.com/monorepo/)
- [Maven Multi-Module Projects](https://maven.apache.org/guides/mini/guide-multiple-modules.html)
- Related: ADR-001 (Microservices Architecture)

