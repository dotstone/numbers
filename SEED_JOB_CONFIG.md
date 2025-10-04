# Seed Job Configuration

## Overview
The seed job uses Job DSL to automatically create and manage Jenkins jobs for the Numbers microservices project.

## Configuration Steps

### 1. Create Seed Job
- Job Type: **Freestyle project**
- Name: `seed`

### 2. Source Code Management
- **Git**
  - Repository URL: `https://github.com/dotstone/numbers.git`
  - Credentials: `github-pat-numbers`
  - Branch: `*/main`

### 3. Build Steps
Add a **Process Job DSLs** build step:
- **DSL Scripts**: `jenkins_jobs.groovy`
- **Action for removed jobs**: Select **Delete**
- **Action for removed views**: Select **Delete**

This ensures that any jobs not defined in `jenkins_jobs.groovy` will be automatically deleted when the seed job runs.

### 4. Script Approval
After the first run, you'll need to approve the script:
1. Go to **Manage Jenkins** → **In-process Script Approval**
2. Approve the `jenkins_jobs.groovy` script

## Jobs Created by Seed Job

The seed job creates the following Jenkins jobs:

1. **numbers-services** (Organization Folder)
   - Parent folder for all Numbers microservices
   - Scans GitHub organization for repositories

2. **number-generator** (Multibranch Pipeline)
   - Builds and deploys the number-generator microservice
   - Uses `number-generator/Jenkinsfile`

3. **number-calculator** (Multibranch Pipeline)
   - Builds and deploys the number-calculator microservice
   - Uses `number-calculator/Jenkinsfile`

## Automatic Job Cleanup

With the "Delete" action configured:
- Any job that exists in Jenkins but is NOT defined in `jenkins_jobs.groovy` will be automatically deleted
- This keeps your Jenkins instance clean and in sync with your configuration
- Example: The old `numbers` multibranch pipeline will be deleted automatically

## Triggering the Seed Job

The seed job can be triggered:
- **Manually**: Click "Build Now" in Jenkins
- **Automatically**: Configure SCM polling or webhooks to run when `jenkins_jobs.groovy` changes
- **Scheduled**: Add a build trigger to run periodically

## Modifying Jobs

To add, modify, or remove jobs:
1. Edit `jenkins_jobs.groovy` in the repository
2. Commit and push the changes
3. Run the seed job (manually or automatically)
4. Jobs will be created, updated, or deleted accordingly
