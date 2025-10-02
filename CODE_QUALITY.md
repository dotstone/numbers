# Code Quality Guidelines

This document outlines the code quality standards and tools configured for this project.

## Overview

The Jenkins pipeline enforces comprehensive code quality checks to ensure maintainable, secure, and high-quality code.

## Quality Tools

### 1. **Checkstyle** - Code Style Enforcement
- **Purpose**: Ensures consistent code formatting and style
- **Configuration**: Uses Google Java Style Guide (`google_checks.xml`)
- **Threshold**: Warnings cause build to become UNSTABLE
- **Reports**: Available at `${BUILD_URL}checkstyle`

**Key Rules:**
- Indentation: 2 spaces
- Line length: 100 characters
- Proper Javadoc comments
- Naming conventions
- Import organization

### 2. **PMD** - Code Quality Analysis
- **Purpose**: Detects common programming flaws
- **Configuration**: Uses quickstart ruleset
- **Threshold**: Violations cause build to become UNSTABLE
- **Reports**: Available at `${BUILD_URL}pmd`

**Detects:**
- Unused variables and imports
- Empty catch blocks
- Unnecessary object creation
- Suboptimal code patterns
- Duplicate code (CPD)

### 3. **SpotBugs** - Bug Detection
- **Purpose**: Identifies potential bugs using static analysis
- **Configuration**: Maximum effort, low threshold
- **Threshold**: Any bugs cause build to become UNSTABLE
- **Reports**: Available at `${BUILD_URL}spotbugs`

**Detects:**
- Null pointer dereferences
- Resource leaks
- Concurrency issues
- Security vulnerabilities
- Bad practices

### 4. **JaCoCo** - Code Coverage
- **Purpose**: Measures test coverage
- **Thresholds**:
  - Line Coverage: **≥80%**
  - Branch Coverage: **≥70%**
- **Reports**: Available at `${BUILD_URL}jacoco`

**Coverage Requirements:**
- All new code must have tests
- Critical paths require 100% coverage
- Exclude only generated code

### 5. **OWASP Dependency Check** - Security Scanning
- **Purpose**: Identifies known vulnerabilities in dependencies
- **Configuration**: Fails on CVSS score ≥7 (High severity)
- **Reports**: Available at `${BUILD_URL}dependency-check-jenkins-plugin`

**Security Standards:**
- No critical vulnerabilities (CVSS ≥9)
- No high vulnerabilities (CVSS ≥7)
- Document and suppress false positives

## Quality Gates

The pipeline includes a **Quality Gate** stage that enforces:

1. ✅ All code style checks pass (Checkstyle)
2. ✅ No code quality violations (PMD)
3. ✅ No bugs detected (SpotBugs)
4. ✅ Minimum code coverage met (JaCoCo)
5. ✅ No high-severity security vulnerabilities (OWASP)

**Build Status:**
- **SUCCESS**: All quality gates passed
- **UNSTABLE**: Quality issues detected but build completed
- **FAILURE**: Critical issues or quality gate threshold exceeded

## Pipeline Stages

```
1. Version       → Semantic versioning based on commits
2. Checkout      → Fetch source code
3. Build         → Compile the project
4. Code Quality  → Parallel execution of:
   - Checkstyle
   - PMD
   - SpotBugs
5. Test & Coverage → Run tests with JaCoCo
6. Security Scan → OWASP dependency check
7. Package       → Create JAR artifacts
8. Archive       → Store build artifacts
9. Quality Gate  → Enforce quality thresholds
```

## Local Development

### Running Quality Checks Locally

```bash
# Run all quality checks
./mvnw clean verify

# Run specific checks
./mvnw checkstyle:check
./mvnw pmd:check pmd:cpd-check
./mvnw spotbugs:check
./mvnw jacoco:report
./mvnw dependency-check:check
```

### Viewing Reports Locally

After running checks, reports are available in:
- Checkstyle: `target/checkstyle-result.xml`
- PMD: `target/pmd.xml` and `target/cpd.xml`
- SpotBugs: `target/spotbugsXml.xml`
- JaCoCo: `target/site/jacoco/index.html`
- OWASP: `target/dependency-check-report.html`

## Best Practices

### Code Style
1. Follow Google Java Style Guide
2. Use meaningful variable and method names
3. Keep methods short and focused (max 50 lines)
4. Document public APIs with Javadoc
5. Organize imports properly

### Testing
1. Write tests before or alongside code (TDD)
2. Aim for >80% line coverage, >70% branch coverage
3. Test edge cases and error conditions
4. Use descriptive test method names
5. Keep tests independent and fast

### Security
1. Keep dependencies up to date
2. Review and address security scan findings
3. Never commit secrets or credentials
4. Validate all external inputs
5. Use secure coding practices

### Code Quality
1. Avoid code duplication (DRY principle)
2. Handle exceptions properly
3. Close resources in try-with-resources
4. Avoid magic numbers (use constants)
5. Keep cyclomatic complexity low (<10)

## Suppressing False Positives

### Checkstyle
Edit `checkstyle-suppressions.xml`:
```xml
<suppress checks="SpecificCheck" files="FileName\.java"/>
```

### OWASP Dependency Check
Edit `dependency-check-suppressions.xml`:
```xml
<suppress>
    <notes>Justification for suppression</notes>
    <cve>CVE-XXXX-XXXXX</cve>
</suppress>
```

### SpotBugs
Add annotation in code:
```java
@SuppressFBWarnings(value = "RULE_NAME", justification = "Reason")
```

## Continuous Improvement

1. **Review Reports**: Regularly check quality reports
2. **Address Technical Debt**: Fix warnings and code smells
3. **Update Rules**: Adjust thresholds as code quality improves
4. **Team Discussion**: Discuss quality issues in code reviews
5. **Training**: Share knowledge about quality tools and practices

## Configuration Files

- `pom.xml` - Maven plugin configurations
- `checkstyle-suppressions.xml` - Checkstyle suppressions
- `dependency-check-suppressions.xml` - OWASP suppressions
- `Jenkinsfile` - CI/CD pipeline with quality gates

## Troubleshooting

### Build Fails on Quality Checks
1. Review the specific report for violations
2. Fix the issues or suppress false positives
3. Run checks locally before committing
4. Ensure all tests pass

### Coverage Below Threshold
1. Identify untested code in JaCoCo report
2. Write missing unit tests
3. Consider integration tests for complex scenarios
4. Exclude only truly untestable code

### Security Vulnerabilities Found
1. Review OWASP report for details
2. Update vulnerable dependencies
3. If no fix available, assess risk and suppress with justification
4. Monitor for updates

## Resources

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [PMD Rules](https://pmd.github.io/latest/pmd_rules_java.html)
- [SpotBugs Bug Patterns](https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
