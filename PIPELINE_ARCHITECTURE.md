# Pipeline Architecture

## 🏗️ Complete Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     JENKINS CI/CD PIPELINE                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ STAGE 1: Version (main branch only)                             │
├─────────────────────────────────────────────────────────────────┤
│ • Fetch git tags                                                 │
│ • Analyze commits (feat:, fix:, BREAKING CHANGE)                │
│ • Calculate semantic version                                     │
│ • Create and push new tag                                        │
│ Output: NEXT_VERSION environment variable                       │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 2: Checkout                                               │
├─────────────────────────────────────────────────────────────────┤
│ • Clone repository                                               │
│ • Checkout specified branch                                      │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 3: Build                                                   │
├─────────────────────────────────────────────────────────────────┤
│ • Maven clean compile                                            │
│ • Set version from NEXT_VERSION                                  │
│ • Compile all modules                                            │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 4: Code Quality Analysis (PARALLEL)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │ Checkstyle   │    │     PMD      │    │  SpotBugs    │     │
│  ├──────────────┤    ├──────────────┤    ├──────────────┤     │
│  │ • Run check  │    │ • PMD check  │    │ • Run scan   │     │
│  │ • Generate   │    │ • CPD check  │    │ • Generate   │     │
│  │   XML report │    │ • Generate   │    │   XML report │     │
│  │ • Record     │    │   reports    │    │ • Record     │     │
│  │   issues     │    │ • Record     │    │   issues     │     │
│  │              │    │   issues     │    │              │     │
│  │ Threshold:   │    │ Threshold:   │    │ Threshold:   │     │
│  │ UNSTABLE     │    │ UNSTABLE     │    │ UNSTABLE     │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
│         ↓                   ↓                    ↓              │
│         └───────────────────┴────────────────────┘              │
│                             ↓                                    │
│              Warnings Next Generation Plugin                    │
│              (Aggregates all issues)                            │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 5: Test & Coverage                                        │
├─────────────────────────────────────────────────────────────────┤
│ • Run unit tests (Maven test)                                   │
│ • JaCoCo agent collects coverage data                           │
│ • Generate coverage reports                                      │
│ • Publish JUnit test results                                     │
│ • Publish JaCoCo coverage report                                │
│ • Publish HTML coverage report                                   │
│                                                                  │
│ Quality Gates:                                                   │
│ • Line Coverage ≥ 80%  → FAIL if not met                        │
│ • Branch Coverage ≥ 70% → FAIL if not met                       │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 6: Security Scan                                          │
├─────────────────────────────────────────────────────────────────┤
│ • OWASP Dependency Check                                         │
│ • Scan all dependencies                                          │
│ • Check against NVD database                                     │
│ • Generate security report                                       │
│ • Publish HTML security report                                   │
│                                                                  │
│ Quality Gate:                                                    │
│ • CVSS Score ≥ 7 (High) → FAIL                                  │
│ • CVSS Score < 7 → UNSTABLE (if any vulnerabilities)           │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 7: Package                                                │
├─────────────────────────────────────────────────────────────────┤
│ • Maven package (skip tests)                                     │
│ • Create JAR artifacts                                           │
│ • Include version in artifact name                               │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 8: Archive Artifacts                                      │
├─────────────────────────────────────────────────────────────────┤
│ • Archive all JAR files                                          │
│ • Generate fingerprints                                          │
│ • Make available for download                                    │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 9: Quality Gate                                           │
├─────────────────────────────────────────────────────────────────┤
│ • Check build result                                             │
│ • If UNSTABLE → FAIL build                                      │
│ • If SUCCESS → Continue                                          │
│                                                                  │
│ This enforces that all quality checks must pass                 │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ POST-BUILD ACTIONS                                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│ SUCCESS:                                                         │
│ • Send success email with quality metrics                       │
│ • Include links to all reports                                   │
│ • Clean workspace                                                │
│                                                                  │
│ UNSTABLE:                                                        │
│ • Send warning email                                             │
│ • List all quality issues found                                  │
│ • Provide links to specific reports                              │
│ • Preserve workspace for debugging                               │
│                                                                  │
│ FAILURE:                                                         │
│ • Send failure email                                             │
│ • Include troubleshooting steps                                  │
│ • Notify culprits (who broke the build)                         │
│ • Preserve workspace for debugging                               │
│                                                                  │
│ ALWAYS:                                                          │
│ • Publish Maven site reports                                     │
│ • Conditional workspace cleanup                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 Quality Tools Integration

```
┌─────────────────────────────────────────────────────────────────┐
│                    QUALITY TOOLS ECOSYSTEM                       │
└─────────────────────────────────────────────────────────────────┘

Maven Plugins                Jenkins Plugins              Reports
─────────────               ─────────────────            ──────────

┌──────────────┐            ┌──────────────┐           ┌──────────┐
│ Checkstyle   │───────────→│ Warnings NG  │──────────→│   HTML   │
│   Plugin     │            │              │           │  Report  │
└──────────────┘            └──────────────┘           └──────────┘
                                    ↑
┌──────────────┐                   │                   ┌──────────┐
│     PMD      │───────────────────┤                   │   XML    │
│   Plugin     │                   │                   │  Report  │
└──────────────┘                   │                   └──────────┘
                                    │
┌──────────────┐                   │                   ┌──────────┐
│  SpotBugs    │───────────────────┘                   │  Trends  │
│   Plugin     │                                       │  Charts  │
└──────────────┘                                       └──────────┘

┌──────────────┐            ┌──────────────┐           ┌──────────┐
│    JaCoCo    │───────────→│    JaCoCo    │──────────→│ Coverage │
│   Plugin     │            │    Plugin    │           │  Report  │
└──────────────┘            └──────────────┘           └──────────┘

┌──────────────┐            ┌──────────────┐           ┌──────────┐
│    OWASP     │───────────→│  Dependency  │──────────→│ Security │
│ Dep-Check    │            │    Check     │           │  Report  │
└──────────────┘            └──────────────┘           └──────────┘

┌──────────────┐            ┌──────────────┐           ┌──────────┐
│   Surefire   │───────────→│     JUnit    │──────────→│   Test   │
│   Plugin     │            │    Plugin    │           │  Results │
└──────────────┘            └──────────────┘           └──────────┘
```

## 🔄 Build Flow Decision Tree

```
                        [Build Starts]
                             │
                             ↓
                    [Compile Success?]
                      ╱           ╲
                    NO             YES
                    ↓               ↓
              [FAILURE]      [Run Quality Checks]
                                    │
                                    ↓
                        [Any Quality Issues?]
                          ╱              ╲
                        YES               NO
                        ↓                 ↓
                  [UNSTABLE]         [Run Tests]
                        │                 │
                        ↓                 ↓
                [Run Tests]      [Coverage ≥80%/70%?]
                        │           ╱           ╲
                        ↓         NO             YES
                [Coverage OK?]    ↓               ↓
                  ╱        ╲   [FAILURE]    [Security Scan]
                NO          YES               │
                ↓            ↓                ↓
           [FAILURE]   [Security OK?]  [CVSS < 7?]
                          ╱        ╲      ╱      ╲
                        NO          YES  NO       YES
                        ↓            ↓   ↓         ↓
                  [UNSTABLE]    [Package] [FAILURE] [Package]
                        │            │              │
                        ↓            ↓              ↓
                  [Quality Gate] [Quality Gate] [Archive]
                        │            │              │
                        ↓            ↓              ↓
                  [FAILURE]    [SUCCESS]      [SUCCESS]
```

## 📈 Report Accessibility

```
Jenkins Build Page
├── Console Output
├── Test Results (JUnit Plugin)
├── Code Coverage (JaCoCo Plugin)
│   └── Line/Branch coverage charts
├── Warnings (Warnings NG Plugin)
│   ├── Checkstyle issues
│   ├── PMD violations
│   └── SpotBugs bugs
├── Security Report (OWASP Plugin)
│   └── Vulnerability details
├── HTML Reports
│   ├── JaCoCo Coverage Report
│   ├── OWASP Dependency Check
│   └── Maven Site Report
└── Artifacts
    ├── number-generator.jar
    └── number-calculator.jar
```

## 🎯 Quality Gate Logic

```
┌─────────────────────────────────────────────────────────────────┐
│                      QUALITY GATE RULES                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  IF Line Coverage < 80%        → BUILD FAILS                    │
│  IF Branch Coverage < 70%      → BUILD FAILS                    │
│  IF Security CVSS ≥ 7          → BUILD FAILS                    │
│                                                                  │
│  IF Checkstyle violations > 0  → BUILD UNSTABLE                 │
│  IF PMD violations > 0         → BUILD UNSTABLE                 │
│  IF SpotBugs issues > 0        → BUILD UNSTABLE                 │
│  IF Security CVSS < 7 (any)    → BUILD UNSTABLE                 │
│                                                                  │
│  IF BUILD is UNSTABLE          → QUALITY GATE FAILS             │
│  IF BUILD is SUCCESS           → QUALITY GATE PASSES            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 🔔 Notification Flow

```
                    [Build Completes]
                           │
                           ↓
                  [Determine Status]
                    ╱      │      ╲
                  ╱        │        ╲
            SUCCESS    UNSTABLE    FAILURE
               │          │           │
               ↓          ↓           ↓
         ┌─────────┐ ┌─────────┐ ┌─────────┐
         │ ✅ Email│ │ ⚠️ Email│ │ ❌ Email│
         ├─────────┤ ├─────────┤ ├─────────┤
         │ Quality │ │ Quality │ │ Build   │
         │ Metrics │ │ Issues  │ │ Failed  │
         │ Summary │ │ Found   │ │ Details │
         │         │ │         │ │         │
         │ Report  │ │ Report  │ │ Console │
         │ Links   │ │ Links   │ │ Link    │
         └─────────┘ └─────────┘ └─────────┘
               │          │           │
               ↓          ↓           ↓
         Developers   Developers   Developers
                                   + Culprits
```

## 🛠️ Tool Execution Timeline

```
Time →

0s    ├─ Version Stage
      │
30s   ├─ Checkout Stage
      │
60s   ├─ Build Stage
      │
120s  ├─ Code Quality Analysis (PARALLEL)
      │  ├─ Checkstyle (30s)
      │  ├─ PMD (45s)
      │  └─ SpotBugs (60s)
      │
180s  ├─ Test & Coverage Stage
      │
240s  ├─ Security Scan Stage
      │
300s  ├─ Package Stage
      │
320s  ├─ Archive Stage
      │
330s  ├─ Quality Gate Stage
      │
340s  └─ Post-Build Actions

Total: ~5-6 minutes (varies by project size)
```

## 📦 Artifact Flow

```
Source Code
    ↓
[Maven Compile]
    ↓
Class Files → [Quality Analysis] → Reports
    ↓
[Maven Test]
    ↓
Test Results → [JaCoCo] → Coverage Reports
    ↓
[Maven Package]
    ↓
JAR Files → [Jenkins Archive] → Downloadable Artifacts
```

## 🔐 Security Integration

```
┌─────────────────────────────────────────────────────────────────┐
│                    SECURITY SCANNING FLOW                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Dependencies (pom.xml)                                          │
│         ↓                                                        │
│  OWASP Dependency Check                                          │
│         ↓                                                        │
│  Download NVD Database                                           │
│         ↓                                                        │
│  Scan Dependencies                                               │
│         ↓                                                        │
│  Match CVEs                                                      │
│         ↓                                                        │
│  Calculate CVSS Scores                                           │
│         ↓                                                        │
│  ┌──────────────────────────────────┐                           │
│  │ CVSS ≥ 9.0 (Critical)  → FAIL   │                           │
│  │ CVSS ≥ 7.0 (High)      → FAIL   │                           │
│  │ CVSS < 7.0 (Medium/Low) → WARN  │                           │
│  └──────────────────────────────────┘                           │
│         ↓                                                        │
│  Generate HTML Report                                            │
│         ↓                                                        │
│  Publish to Jenkins                                              │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 📊 Metrics Dashboard (Conceptual)

```
┌─────────────────────────────────────────────────────────────────┐
│                     BUILD QUALITY DASHBOARD                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Build Status:  ✅ SUCCESS                                      │
│  Version:       1.2.3                                            │
│  Duration:      5m 23s                                           │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Code Coverage                                            │   │
│  │ ████████████████████████░░ 85% Line (Target: 80%)       │   │
│  │ ████████████████████░░░░░░ 73% Branch (Target: 70%)     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Code Quality Issues                                      │   │
│  │ Checkstyle:  0 violations ✅                             │   │
│  │ PMD:         0 violations ✅                             │   │
│  │ SpotBugs:    0 bugs ✅                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Security                                                 │   │
│  │ Vulnerabilities: 0 ✅                                    │   │
│  │ Last Scan:       2025-10-02 20:00                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Tests                                                    │   │
│  │ Total:   42 tests                                        │   │
│  │ Passed:  42 ✅                                           │   │
│  │ Failed:  0                                               │   │
│  │ Skipped: 0                                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

**This architecture ensures comprehensive code quality enforcement at every stage of the CI/CD pipeline.**
