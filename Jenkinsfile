pipeline {
    agent any
    
    tools {
        jdk 'JDK-21'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m -XX:+TieredCompilation -XX:TieredStopAtLevel=1'
        MAVEN_CLI_OPTS = '--batch-mode --errors --fail-at-end --show-version'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 1, unit: 'HOURS')
        timestamps()
        disableConcurrentBuilds()
    }
    
    stages {
        stage('Version') {
            when { branch 'main' }
            steps {
                script {
                    // Ensure we have full git history and tags
                    sh '''
                        git fetch --tags --prune --unshallow || true
                        git fetch --tags --prune || true
                    '''

                    // Determine last tag or default to 0.0.0
                    def lastTag = sh(script: "git describe --tags --abbrev=0 2>/dev/null || echo 0.0.0", returnStdout: true).trim()
                    // Collect commit messages since last tag (or all if 0.0.0)
                    def range = (lastTag == '0.0.0') ? '' : "${lastTag}..HEAD"
                    def commits = sh(script: "git log ${range} --pretty=%s 2>/dev/null || true", returnStdout: true).trim()

                    // Decide bump level
                    def bump = 'none'
                    if (commits =~ /BREAKING CHANGE|!:/) {
                        bump = 'major'
                    } else if (commits =~ /(?m)^feat:/) {
                        bump = 'minor'
                    } else if (commits =~ /(?m)^fix:/) {
                        bump = 'patch'
                    }

                    // Parse lastTag into components
                    def (maj, min, pat) = lastTag.tokenize('.')
                    if (!maj) { maj = '0'; min = '0'; pat = '0' }
                    int iMaj = maj as int
                    int iMin = min as int
                    int iPat = pat as int

                    if (bump == 'major') {
                        iMaj += 1; iMin = 0; iPat = 0
                    } else if (bump == 'minor') {
                        iMin += 1; iPat = 0
                    } else if (bump == 'patch') {
                        iPat += 1
                    }

                    def nextVersion = "${iMaj}.${iMin}.${iPat}"
                    // If no bump and lastTag != 0.0.0, reuse lastTag; otherwise start at 0.1.0
                    if (bump == 'none') {
                        nextVersion = (lastTag == '0.0.0') ? '0.1.0' : lastTag
                    }

                    echo "Last tag: ${lastTag}"
                    echo "Bump: ${bump}"
                    echo "Next version: ${nextVersion}"
                    env.NEXT_VERSION = nextVersion

                    // Tag and push only when a new tag is needed (bump != none or no tag existed)
                    if (nextVersion != lastTag) {
                        withCredentials([usernamePassword(credentialsId: 'github-pat-numbers', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                            sh """
                                git config user.email "ci@jenkins"
                                git config user.name "Jenkins CI"
                                git tag -a ${nextVersion} -m "chore(release): ${nextVersion}"
                                git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/dotstone/numbers.git
                                git push origin ${nextVersion}
                            """
                        }
                    }
                }
            }
        }
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building the project...'
                script {
                    if (isUnix()) {
                        sh "./mvnw ${MAVEN_CLI_OPTS} clean compile -Drevision=\${NEXT_VERSION}"
                    } else {
                        bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} clean compile -Drevision=%NEXT_VERSION%"
                    }
                }
            }
        }
        
        stage('Code Quality Analysis') {
            parallel {
                stage('Checkstyle') {
                    steps {
                        echo 'Running Checkstyle analysis...'
                        script {
                            try {
                                if (isUnix()) {
                                    sh "./mvnw ${MAVEN_CLI_OPTS} checkstyle:check -Drevision=\${NEXT_VERSION}"
                                } else {
                                    bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} checkstyle:check -Drevision=%NEXT_VERSION%"
                                }
                            } catch (Exception e) {
                                unstable(message: "Checkstyle violations found")
                                currentBuild.result = 'UNSTABLE'
                            }
                        }
                    }
                    post {
                        always {
                            recordIssues(
                                enabledForFailure: true,
                                tools: [checkStyle(pattern: '**/target/checkstyle-result.xml')],
                                qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
                            )
                        }
                    }
                }
                
                stage('PMD') {
                    steps {
                        echo 'Running PMD analysis...'
                        script {
                            try {
                                if (isUnix()) {
                                    sh "./mvnw ${MAVEN_CLI_OPTS} pmd:pmd pmd:cpd -Drevision=\${NEXT_VERSION}"
                                } else {
                                    bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} pmd:pmd pmd:cpd -Drevision=%NEXT_VERSION%"
                                }
                            } catch (Exception e) {
                                unstable(message: "PMD violations found")
                                currentBuild.result = 'UNSTABLE'
                            }
                        }
                    }
                    post {
                        always {
                            recordIssues(
                                enabledForFailure: true,
                                tools: [pmdParser(pattern: '**/target/pmd.xml'), cpd(pattern: '**/target/cpd.xml')],
                                qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
                            )
                        }
                    }
                }
                
                stage('SpotBugs') {
                    steps {
                        echo 'Running SpotBugs analysis...'
                        script {
                            try {
                                if (isUnix()) {
                                    sh "./mvnw ${MAVEN_CLI_OPTS} spotbugs:spotbugs -Drevision=\${NEXT_VERSION}"
                                } else {
                                    bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} spotbugs:spotbugs -Drevision=%NEXT_VERSION%"
                                }
                            } catch (Exception e) {
                                unstable(message: "SpotBugs violations found")
                                currentBuild.result = 'UNSTABLE'
                            }
                        }
                    }
                    post {
                        always {
                            recordIssues(
                                enabledForFailure: true,
                                tools: [spotBugs(pattern: '**/target/spotbugsXml.xml')],
                                qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
                            )
                        }
                    }
                }
            }
        }
        
        stage('Test & Coverage') {
            steps {
                echo 'Running unit tests with coverage...'
                script {
                    if (isUnix()) {
                        sh "./mvnw ${MAVEN_CLI_OPTS} test -Drevision=\${NEXT_VERSION}"
                    } else {
                        bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} test -Drevision=%NEXT_VERSION%"
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/*Test*.class',
                        minimumLineCoverage: '80',
                        minimumBranchCoverage: '70',
                        changeBuildStatus: true
                    )
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                echo 'Running OWASP Dependency Check...'
                script {
                    try {
                        if (isUnix()) {
                            sh "./mvnw ${MAVEN_CLI_OPTS} dependency-check:check -Drevision=\${NEXT_VERSION}"
                        } else {
                            bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} dependency-check:check -Drevision=%NEXT_VERSION%"
                        }
                    } catch (Exception e) {
                        unstable(message: "Security vulnerabilities found")
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    dependencyCheckPublisher pattern: '**/target/dependency-check-report.xml'
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check'
                    ])
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the applications...'
                script {
                    if (isUnix()) {
                        sh "./mvnw ${MAVEN_CLI_OPTS} package -DskipTests -Drevision=\${NEXT_VERSION}"
                    } else {
                        bat ".\\mvnw.cmd ${MAVEN_CLI_OPTS} package -DskipTests -Drevision=%NEXT_VERSION%"
                    }
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: true
            }
        }
        
        stage('Quality Gate') {
            steps {
                script {
                    def qualityGate = currentBuild.result ?: 'SUCCESS'
                    if (qualityGate == 'UNSTABLE') {
                        error "Quality gate failed: Code quality issues detected"
                    }
                    echo "Quality gate passed: ${qualityGate}"
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            script {
                def coverageReport = "Code coverage and quality checks passed."
                emailext(
                    subject: "✅ SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                    body: """
                        <h2>Build Successful</h2>
                        <p><strong>Job:</strong> ${env.JOB_NAME} [${env.BUILD_NUMBER}]</p>
                        <p><strong>Version:</strong> ${env.NEXT_VERSION}</p>
                        <p><strong>Status:</strong> ${currentBuild.result ?: 'SUCCESS'}</p>
                        <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                        <hr>
                        <h3>Quality Metrics</h3>
                        <ul>
                            <li>✅ Code Style (Checkstyle): Passed</li>
                            <li>✅ Code Quality (PMD): Passed</li>
                            <li>✅ Bug Detection (SpotBugs): Passed</li>
                            <li>✅ Code Coverage (JaCoCo): ≥80% line, ≥70% branch</li>
                            <li>✅ Security Scan (OWASP): No critical vulnerabilities</li>
                        </ul>
                        <hr>
                        <p>📊 <a href='${env.BUILD_URL}'>View Build Details</a></p>
                        <p>📈 <a href='${env.BUILD_URL}jacoco'>View Coverage Report</a></p>
                        <p>🔒 <a href='${env.BUILD_URL}dependency-check-jenkins-plugin'>View Security Report</a></p>
                    """,
                    mimeType: 'text/html',
                    recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                    to: '${DEFAULT_RECIPIENTS}'
                )
            }
        }
        unstable {
            echo 'Pipeline completed with warnings!'
            emailext(
                subject: "⚠️ UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <h2>Build Unstable - Quality Issues Detected</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME} [${env.BUILD_NUMBER}]</p>
                    <p><strong>Version:</strong> ${env.NEXT_VERSION}</p>
                    <p><strong>Status:</strong> UNSTABLE</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <hr>
                    <h3>Issues Found</h3>
                    <p>⚠️ Code quality or security issues were detected. Please review:</p>
                    <ul>
                        <li>📋 <a href='${env.BUILD_URL}checkstyle'>Checkstyle Report</a></li>
                        <li>📋 <a href='${env.BUILD_URL}pmd'>PMD Report</a></li>
                        <li>🐛 <a href='${env.BUILD_URL}spotbugs'>SpotBugs Report</a></li>
                        <li>🔒 <a href='${env.BUILD_URL}dependency-check-jenkins-plugin'>Security Report</a></li>
                        <li>📈 <a href='${env.BUILD_URL}jacoco'>Coverage Report</a></li>
                    </ul>
                    <hr>
                    <p>📊 <a href='${env.BUILD_URL}'>View Full Build Details</a></p>
                """,
                mimeType: 'text/html',
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext(
                subject: "❌ FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """
                    <h2>Build Failed</h2>
                    <p><strong>Job:</strong> ${env.JOB_NAME} [${env.BUILD_NUMBER}]</p>
                    <p><strong>Version:</strong> ${env.NEXT_VERSION}</p>
                    <p><strong>Status:</strong> FAILURE</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <hr>
                    <h3>Action Required</h3>
                    <p>❌ The build has failed. Please investigate the following:</p>
                    <ul>
                        <li>Check console output for error messages</li>
                        <li>Review test failures</li>
                        <li>Verify code quality violations</li>
                        <li>Check for compilation errors</li>
                    </ul>
                    <hr>
                    <p>📊 <a href='${env.BUILD_URL}console'>View Console Output</a></p>
                    <p>📋 <a href='${env.BUILD_URL}'>View Build Details</a></p>
                """,
                mimeType: 'text/html',
                recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'CulpritsRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        always {
            echo 'Publishing all reports...'
            // Publish aggregated test results
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site',
                reportFiles: 'project-reports.html',
                reportName: 'Maven Site Report'
            ])
            
            // Clean workspace only on success to preserve artifacts for debugging
            script {
                if (currentBuild.result == 'SUCCESS') {
                    echo 'Cleaning up workspace...'
                    cleanWs(
                        deleteDirs: true,
                        disableDeferredWipeout: true,
                        notFailBuild: true
                    )
                } else {
                    echo 'Preserving workspace for debugging (build not successful)'
                }
            }
        }
    }
}
