# Jenkins Setup Checklist

This checklist ensures your Jenkins instance is properly configured to run the code quality pipeline.

## 📋 Pre-requisites

### ✅ Jenkins Version
- [ ] Jenkins LTS 2.387.x or later installed
- [ ] Jenkins running and accessible

### ✅ System Requirements
- [ ] JDK 21 installed on Jenkins server
- [ ] Maven 3.8+ available (or use Maven Wrapper)
- [ ] Sufficient disk space for reports (min 10GB recommended)
- [ ] Network access to Maven Central and NVD database

## 🔌 Required Plugins Installation

### Core Plugins
- [ ] **Git Plugin** - SCM integration
- [ ] **Pipeline** - Pipeline support
- [ ] **Pipeline: Stage View** - Stage visualization
- [ ] **Credentials Binding** - Secure credentials

### Code Quality Plugins
- [ ] **Warnings Next Generation** - Code analysis aggregation
- [ ] **JaCoCo Plugin** - Coverage reports
- [ ] **HTML Publisher** - HTML report publishing
- [ ] **OWASP Dependency-Check** - Security scanning

### Testing & Reporting
- [ ] **JUnit Plugin** - Test results
- [ ] **Test Results Analyzer** - Test analysis

### Notifications
- [ ] **Email Extension (Email-ext)** - Enhanced emails
- [ ] **Mailer** - Basic email support

### Installation Command
```bash
# Install all required plugins via CLI
java -jar jenkins-cli.jar -s http://your-jenkins-url/ install-plugin \
  git workflow-aggregator pipeline-stage-view credentials-binding \
  warnings-ng jacoco htmlpublisher dependency-check-jenkins-plugin \
  junit test-results-analyzer email-ext mailer

# Restart Jenkins
java -jar jenkins-cli.jar -s http://your-jenkins-url/ safe-restart
```

## 🔧 Jenkins Configuration

### 1. JDK Configuration
- [ ] Navigate to **Manage Jenkins** → **Global Tool Configuration**
- [ ] Add JDK installation named **'JDK-21'**
- [ ] Configure JDK 21 path or auto-installer
- [ ] Save configuration

### 2. Git Configuration
- [ ] Ensure Git is installed on Jenkins server
- [ ] Configure Git in **Global Tool Configuration** (if needed)
- [ ] Test Git connectivity

### 3. Email Configuration
- [ ] Navigate to **Manage Jenkins** → **Configure System**
- [ ] Configure **E-mail Notification**:
  - [ ] SMTP server address
  - [ ] SMTP port (usually 25, 465, or 587)
  - [ ] Authentication credentials (if required)
  - [ ] Test email configuration
- [ ] Configure **Extended E-mail Notification**:
  - [ ] SMTP server (same as above)
  - [ ] Default recipients: `${DEFAULT_RECIPIENTS}`
  - [ ] Default content type: HTML
  - [ ] Test configuration

### 4. Credentials Setup
- [ ] Navigate to **Manage Jenkins** → **Manage Credentials**
- [ ] Add GitHub credentials:
  - [ ] ID: **'github-pat-numbers'**
  - [ ] Type: Username with password
  - [ ] Username: Your GitHub username
  - [ ] Password: GitHub Personal Access Token (PAT)
  - [ ] Scope: Global
- [ ] Verify credentials work

### 5. OWASP Dependency Check Configuration
- [ ] Navigate to **Manage Jenkins** → **Configure System**
- [ ] Find **OWASP Dependency-Check** section
- [ ] Configure NVD API Key (optional but recommended):
  - [ ] Get API key from https://nvd.nist.gov/developers/request-an-api-key
  - [ ] Add to Jenkins configuration
- [ ] Set update frequency (daily recommended)
- [ ] Save configuration

## 🚀 Pipeline Setup

### 1. Create Jenkins Job
- [ ] Click **New Item**
- [ ] Enter job name (e.g., "numbers-pipeline")
- [ ] Select **Pipeline**
- [ ] Click **OK**

### 2. Configure Pipeline
- [ ] In **General** section:
  - [ ] Add description
  - [ ] Check **GitHub project** (optional)
  - [ ] Add project URL
- [ ] In **Build Triggers**:
  - [ ] Configure as needed (e.g., GitHub webhook, poll SCM)
- [ ] In **Pipeline** section:
  - [ ] Definition: **Pipeline script from SCM**
  - [ ] SCM: **Git**
  - [ ] Repository URL: Your GitHub repository
  - [ ] Credentials: Select 'github-pat-numbers'
  - [ ] Branch: `*/main` (or your default branch)
  - [ ] Script Path: `Jenkinsfile`
- [ ] Save configuration

### 3. Environment Variables (Optional)
- [ ] Navigate to **Manage Jenkins** → **Configure System**
- [ ] Add **Global properties** → **Environment variables**:
  - [ ] `DEFAULT_RECIPIENTS`: email addresses for notifications
  - [ ] Any other project-specific variables
- [ ] Save configuration

## ✅ Verification Steps

### 1. Test Pipeline Syntax
- [ ] Open pipeline job
- [ ] Click **Pipeline Syntax**
- [ ] Verify Jenkinsfile syntax
- [ ] Check for any errors

### 2. Run Test Build
- [ ] Click **Build Now**
- [ ] Monitor console output
- [ ] Verify all stages execute
- [ ] Check for plugin errors

### 3. Verify Reports
After successful build, verify:
- [ ] **Checkstyle Report** accessible
- [ ] **PMD Report** accessible
- [ ] **SpotBugs Report** accessible
- [ ] **JaCoCo Coverage Report** accessible
- [ ] **OWASP Dependency Check Report** accessible
- [ ] **JUnit Test Results** accessible

### 4. Test Notifications
- [ ] Verify email sent on build completion
- [ ] Check email formatting (HTML)
- [ ] Verify report links work
- [ ] Test different build statuses (SUCCESS, UNSTABLE, FAILURE)

## 🔒 Security Considerations

### Credentials
- [ ] Use Jenkins Credentials Store (never hardcode)
- [ ] Limit credential scope to specific jobs
- [ ] Regularly rotate credentials
- [ ] Use least privilege principle

### Access Control
- [ ] Configure Jenkins security
- [ ] Set up user authentication
- [ ] Configure authorization (role-based)
- [ ] Limit who can modify pipelines

### Network Security
- [ ] Use HTTPS for Jenkins UI
- [ ] Configure firewall rules
- [ ] Secure webhook endpoints
- [ ] Use VPN if accessing externally

## 📊 Monitoring & Maintenance

### Regular Tasks
- [ ] **Daily**: Monitor build status
- [ ] **Weekly**: Review quality reports
- [ ] **Monthly**: Update plugins
- [ ] **Quarterly**: Review and optimize pipeline

### Health Checks
- [ ] Monitor Jenkins disk space
- [ ] Check build queue length
- [ ] Review build duration trends
- [ ] Monitor executor utilization

### Backup Strategy
- [ ] Backup Jenkins home directory
- [ ] Backup job configurations
- [ ] Backup credentials
- [ ] Test restore procedures

## 🐛 Troubleshooting

### Common Issues

#### Build Fails Immediately
- [ ] Check JDK configuration
- [ ] Verify Git connectivity
- [ ] Check credentials
- [ ] Review Jenkins logs

#### Quality Checks Fail
- [ ] Verify plugins installed correctly
- [ ] Check plugin versions compatibility
- [ ] Review console output for specific errors
- [ ] Ensure Maven can download dependencies

#### Reports Not Showing
- [ ] Verify HTML Publisher plugin installed
- [ ] Check Content Security Policy settings
- [ ] Review report file paths
- [ ] Check file permissions

#### Email Notifications Not Working
- [ ] Test SMTP connection
- [ ] Verify email configuration
- [ ] Check spam/junk folders
- [ ] Review Jenkins system log

### Getting Help
1. Check Jenkins logs: **Manage Jenkins** → **System Log**
2. Review plugin documentation
3. Consult Jenkins community forums
4. Check GitHub issues for plugins

## 📚 Additional Resources

### Documentation
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Pipeline Syntax Reference](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Warnings NG Plugin](https://plugins.jenkins.io/warnings-ng/)
- [JaCoCo Plugin](https://plugins.jenkins.io/jacoco/)

### Project Documentation
- [CODE_QUALITY.md](CODE_QUALITY.md) - Quality guidelines
- [JENKINS_PLUGINS.md](JENKINS_PLUGINS.md) - Plugin details
- [QUICK_START.md](QUICK_START.md) - Developer quick start

## ✨ Post-Setup

### Optimization
- [ ] Configure build caching
- [ ] Set up distributed builds (agents)
- [ ] Optimize Maven settings
- [ ] Configure build retention policies

### Advanced Features
- [ ] Set up Blue Ocean UI
- [ ] Configure build badges
- [ ] Add build trends dashboard
- [ ] Set up Slack/Teams notifications

### Team Onboarding
- [ ] Share Jenkins URL with team
- [ ] Provide access credentials
- [ ] Share documentation links
- [ ] Conduct training session

## 📝 Sign-off

- [ ] All plugins installed and verified
- [ ] Jenkins configured correctly
- [ ] Test build successful
- [ ] Reports accessible
- [ ] Notifications working
- [ ] Team notified
- [ ] Documentation reviewed

**Setup Completed By**: ___________________  
**Date**: ___________________  
**Jenkins Version**: ___________________  
**Notes**: ___________________

---

**Need Help?** Contact the DevOps team or refer to the project documentation.
