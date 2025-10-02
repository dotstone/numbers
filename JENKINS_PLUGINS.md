# Required Jenkins Plugins

This document lists all Jenkins plugins required for the CI/CD pipeline to function properly.

## Core Plugins

### Build & SCM
- **Git Plugin** - Git SCM integration
- **Pipeline** - Pipeline as code support
- **Pipeline: Stage View** - Visualize pipeline stages
- **Credentials Binding** - Secure credential management

### Code Quality & Analysis
- **Warnings Next Generation** - Aggregates and displays code quality issues
  - Supports Checkstyle, PMD, SpotBugs
  - Provides quality gates and trend analysis
- **JaCoCo Plugin** - Code coverage visualization
- **HTML Publisher** - Publish HTML reports
- **OWASP Dependency-Check** - Security vulnerability reporting

### Testing
- **JUnit Plugin** - Test result reporting
- **Test Results Analyzer** - Advanced test result analysis

### Notifications
- **Email Extension (Email-ext)** - Enhanced email notifications
- **Mailer** - Basic email support

## Installation Instructions

### Via Jenkins UI
1. Navigate to **Manage Jenkins** → **Manage Plugins**
2. Go to **Available** tab
3. Search for each plugin listed above
4. Select and click **Install without restart**

### Via Jenkins CLI
```bash
java -jar jenkins-cli.jar -s http://your-jenkins-url/ install-plugin \
  git \
  workflow-aggregator \
  pipeline-stage-view \
  credentials-binding \
  warnings-ng \
  jacoco \
  htmlpublisher \
  dependency-check-jenkins-plugin \
  junit \
  test-results-analyzer \
  email-ext \
  mailer
```

### Via Configuration as Code (JCasC)
```yaml
jenkins:
  plugins:
    - git:latest
    - workflow-aggregator:latest
    - pipeline-stage-view:latest
    - credentials-binding:latest
    - warnings-ng:latest
    - jacoco:latest
    - htmlpublisher:latest
    - dependency-check-jenkins-plugin:latest
    - junit:latest
    - test-results-analyzer:latest
    - email-ext:latest
    - mailer:latest
```

## Plugin Configuration

### Warnings Next Generation
Configure in **Manage Jenkins** → **Configure System**:
- Enable quality gates
- Set trend chart options
- Configure issue filters

### Email Extension
Configure in **Manage Jenkins** → **Configure System**:
- SMTP server settings
- Default recipients
- Email templates

### OWASP Dependency Check
Configure in **Manage Jenkins** → **Configure System**:
- NVD data feed URL
- Update frequency
- Proxy settings (if needed)

## Verification

After installation, verify plugins are active:
```bash
# List installed plugins
java -jar jenkins-cli.jar -s http://your-jenkins-url/ list-plugins
```

Or check in Jenkins UI:
**Manage Jenkins** → **Manage Plugins** → **Installed** tab

## Troubleshooting

### Plugin Conflicts
- Ensure all plugins are up to date
- Check Jenkins logs for compatibility issues
- Restart Jenkins after plugin installation

### Missing Features
- Verify plugin version compatibility with Jenkins version
- Check plugin documentation for feature availability
- Update to latest stable versions

## Recommended Additional Plugins

### Optional but Useful
- **Blue Ocean** - Modern UI for pipelines
- **Pipeline Utility Steps** - Additional pipeline utilities
- **Workspace Cleanup** - Advanced workspace management
- **Timestamper** - Add timestamps to console output
- **Build Timeout** - Automatic build timeout handling
- **AnsiColor** - Colorize console output

### For Advanced Workflows
- **SonarQube Scanner** - Integration with SonarQube
- **Artifactory** - Artifact repository integration
- **Docker Pipeline** - Docker support in pipelines
- **Kubernetes** - Kubernetes-based agents

## Version Compatibility

Minimum Jenkins version: **2.387.x** or later

Tested with:
- Jenkins LTS: 2.387.x
- Jenkins Weekly: 2.400+

## Support

For plugin-specific issues:
1. Check plugin documentation on Jenkins.io
2. Review GitHub issues for the plugin
3. Consult Jenkins community forums
4. Check Jenkins logs for error details

## Updates

Keep plugins updated for:
- Security patches
- Bug fixes
- New features
- Performance improvements

Set up automatic updates or schedule regular manual updates.
