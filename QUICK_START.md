# Quick Start Guide - Code Quality

## 🚀 Quick Commands

### Run All Quality Checks
```bash
# Windows
.\mvnw.cmd clean verify

# Linux/Mac
./mvnw clean verify
```

### Run Individual Checks
```bash
# Code Style (Checkstyle)
./mvnw checkstyle:check

# Code Quality (PMD)
./mvnw pmd:check pmd:cpd-check

# Bug Detection (SpotBugs)
./mvnw spotbugs:check

# Test Coverage (JaCoCo)
./mvnw test jacoco:report

# Security Scan (OWASP)
./mvnw dependency-check:check

# Generate All Reports
./mvnw site
```

## 📊 View Reports

After running checks, open these files in your browser:

| Tool | Report Location |
|------|----------------|
| **JaCoCo Coverage** | `target/site/jacoco/index.html` |
| **OWASP Security** | `target/dependency-check-report.html` |
| **Checkstyle** | `target/site/checkstyle.html` |
| **PMD** | `target/site/pmd.html` |
| **SpotBugs** | `target/site/spotbugs.html` |
| **Test Results** | `target/site/surefire-report.html` |
| **All Reports** | `target/site/index.html` |

## ✅ Quality Thresholds

| Metric | Threshold | Enforced |
|--------|-----------|----------|
| Line Coverage | ≥80% | ✅ Yes |
| Branch Coverage | ≥70% | ✅ Yes |
| Checkstyle Violations | 0 warnings | ⚠️ Unstable |
| PMD Violations | 0 violations | ⚠️ Unstable |
| SpotBugs Issues | 0 bugs | ⚠️ Unstable |
| Security CVSS | <7 (High) | ✅ Yes |

## 🔧 Common Fixes

### Checkstyle Violations
```java
// ❌ Bad: Missing Javadoc
public void myMethod() { }

// ✅ Good: With Javadoc
/**
 * Description of what this method does.
 */
public void myMethod() { }
```

### PMD - Unused Variables
```java
// ❌ Bad: Unused variable
public void process() {
    int unused = 5;
}

// ✅ Good: Remove unused
public void process() {
    // Only declare what you use
}
```

### Low Coverage
```java
// ❌ Bad: No test
public int add(int a, int b) {
    return a + b;
}

// ✅ Good: With test
@Test
void testAdd() {
    assertEquals(5, calculator.add(2, 3));
}
```

## 🐛 Troubleshooting

### Build Fails on Coverage
1. Run: `./mvnw test jacoco:report`
2. Open: `target/site/jacoco/index.html`
3. Find red/yellow highlighted code
4. Write tests for uncovered lines

### Checkstyle Errors
1. Run: `./mvnw checkstyle:check`
2. Check console output for violations
3. Fix formatting issues
4. Or suppress in `checkstyle-suppressions.xml`

### Security Vulnerabilities
1. Run: `./mvnw dependency-check:check`
2. Open: `target/dependency-check-report.html`
3. Update vulnerable dependencies
4. Or suppress false positives in `dependency-check-suppressions.xml`

## 📝 Pre-Commit Checklist

Before committing code:
- [ ] Run `./mvnw clean verify`
- [ ] All tests pass
- [ ] Code coverage ≥80%
- [ ] No Checkstyle violations
- [ ] No PMD violations
- [ ] No SpotBugs issues
- [ ] No security vulnerabilities

## 🎯 IDE Integration

### IntelliJ IDEA
1. Install **Checkstyle-IDEA** plugin
2. Install **PMDPlugin** plugin
3. Install **SpotBugs** plugin
4. Configure plugins to use project configurations

### VS Code
1. Install **Checkstyle for Java** extension
2. Install **SonarLint** extension
3. Configure workspace settings

### Eclipse
1. Install **Checkstyle Plugin**
2. Install **PMD Plugin**
3. Install **SpotBugs Plugin**
4. Import project configurations

## 🔄 CI/CD Pipeline

The Jenkins pipeline automatically:
1. ✅ Runs all quality checks in parallel
2. ✅ Generates and publishes reports
3. ✅ Enforces quality gates
4. ✅ Sends email notifications
5. ✅ Archives artifacts

**Pipeline URL**: `${JENKINS_URL}/job/${JOB_NAME}/`

## 📚 More Information

- [CODE_QUALITY.md](CODE_QUALITY.md) - Detailed guidelines
- [JENKINS_PLUGINS.md](JENKINS_PLUGINS.md) - Required plugins
- [README.md](README.md) - Project overview

## 🆘 Getting Help

1. Check the console output for specific errors
2. Review the HTML reports for details
3. Consult [CODE_QUALITY.md](CODE_QUALITY.md) for best practices
4. Ask the team for guidance

## 💡 Tips

- Run checks **before** pushing to avoid pipeline failures
- Use **IDE plugins** for real-time feedback
- Fix violations **incrementally** rather than all at once
- **Document suppressions** with clear justifications
- Keep dependencies **up to date** for security
