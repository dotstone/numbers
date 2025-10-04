# Jenkins Jobs Setup Guide

This guide explains how to set up the two separate Jenkins jobs for the microservices.

## Prerequisites

1. Jenkins with the following plugins installed:
   - Job DSL Plugin
   - Git Plugin
   - Pipeline Plugin
   - Multibranch Pipeline Plugin
   - Credentials Plugin

## Option 1: Using Job DSL (Recommended)

### Step 1: Create a Seed Job

1. Go to Jenkins Dashboard → New Item
2. Enter name: `seed-job`
3. Select: **Freestyle project**
4. Click OK

### Step 2: Configure the Seed Job

1. Under **Source Code Management**:
   - Select **Git**
   - Repository URL: `https://github.com/dotstone/numbers.git`
   - Credentials: Select `github-pat-numbers`
   - Branch: `*/main`

2. Under **Build Steps**:
   - Click **Add build step** → **Process Job DSLs**
   - Select **Look on Filesystem**
   - DSL Scripts: `jenkins-jobs.yaml`

3. Click **Save**

### Step 3: Run the Seed Job

1. Click **Build Now** on the seed-job
2. Wait for the build to complete
3. Check the console output to verify both jobs were created

### Step 4: Verify the Jobs

Go to Jenkins Dashboard and you should see:
- `number-generator` job
- `number-calculator` job

## Option 2: Using Jenkins Configuration as Code (JCasC)

If you have the JCasC plugin installed:

### Step 1: Copy the Configuration

1. Copy the contents of `jenkins-jobs.yaml`
2. Go to Jenkins → Manage Jenkins → Configuration as Code
3. Paste the YAML content
4. Click **Apply new configuration**

## Option 3: Manual Setup via UI

If the above options don't work, follow these steps:

### For number-generator Job:

1. Jenkins Dashboard → New Item
2. Name: `number-generator`
3. Type: **Multibranch Pipeline**
4. Configure:
   - **Branch Sources** → Add source → Git
     - Project Repository: `https://github.com/dotstone/numbers.git`
     - Credentials: `github-pat-numbers`
   - **Build Configuration**:
     - Mode: `by Jenkinsfile`
     - Script Path: `number-generator/Jenkinsfile`
   - **Scan Multibranch Pipeline Triggers**:
     - ☑ Periodically if not otherwise run
     - Interval: 1 minute
   - **Orphaned Item Strategy**:
     - Days to keep old items: (leave blank)
     - Max # of old items to keep: 10
5. Save

### For number-calculator Job:

1. Jenkins Dashboard → New Item
2. Name: `number-calculator`
3. Type: **Multibranch Pipeline**
4. Configure:
   - **Branch Sources** → Add source → Git
     - Project Repository: `https://github.com/dotstone/numbers.git`
     - Credentials: `github-pat-numbers`
   - **Build Configuration**:
     - Mode: `by Jenkinsfile`
     - Script Path: `number-calculator/Jenkinsfile`
   - **Scan Multibranch Pipeline Triggers**:
     - ☑ Periodically if not otherwise run
     - Interval: 1 minute
   - **Orphaned Item Strategy**:
     - Days to keep old items: (leave blank)
     - Max # of old items to keep: 10
5. Save

## Testing the Setup

### Step 1: Trigger Initial Scans

1. Go to each job (`number-generator` and `number-calculator`)
2. Click **Scan Repository Now**
3. Wait for the scan to complete
4. You should see the `main` branch appear

### Step 2: Test Independent Builds

Make a change to one service:

```bash
# Example: Change number-generator
cd number-generator/src/main/resources
# Edit application.properties
git add .
git commit -m "feat: update number-generator configuration"
git push
```

Wait for Jenkins to detect the change (or trigger manually). Only the `number-generator` job should build.

### Step 3: Verify Versioning

After the build completes:
1. Check the console output for version information
2. Verify the Git tag was created: `number-generator-X.Y.Z`
3. Check GitHub for the new tag

## Troubleshooting

### Jobs Not Created
- Verify Job DSL plugin is installed
- Check seed-job console output for errors
- Ensure credentials ID `github-pat-numbers` exists

### Builds Not Triggering
- Verify branch indexing is configured
- Check Jenkins → Manage Jenkins → System Log for errors
- Manually trigger "Scan Repository Now"

### Version Tags Not Pushed
- Verify `github-pat-numbers` credential has `repo` scope
- Check build console output for Git errors
- Ensure the token hasn't expired

## Next Steps

1. **Commit the Jenkinsfiles**: Push the new Jenkinsfiles to GitHub
   ```bash
   git add number-generator/Jenkinsfile number-calculator/Jenkinsfile
   git commit -m "feat: add separate Jenkinsfiles for each service"
   git push
   ```

2. **Set up the Jenkins jobs** using one of the methods above

3. **Test the builds** by making changes to each service

4. **Configure webhooks** (optional) for instant build triggers:
   - GitHub repo → Settings → Webhooks
   - Payload URL: `http://your-jenkins-url/github-webhook/`
   - Content type: `application/json`
   - Events: Just the push event
