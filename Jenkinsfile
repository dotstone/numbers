pipeline {
    agent any
    
    tools {
        jdk 'JDK-21'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
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
                        withCredentials([usernamePassword(credentialsId: 'e6bf49f4-bda7-481e-b74a-083c43fe4732', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
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
                        sh './mvnw clean compile -Drevision=${NEXT_VERSION}'
                    } else {
                        bat '.\\mvnw.cmd clean compile -Drevision=%NEXT_VERSION%'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                script {
                    if (isUnix()) {
                        sh './mvnw test -Drevision=${NEXT_VERSION}'
                    } else {
                        bat '.\\mvnw.cmd test -Drevision=%NEXT_VERSION%'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    echo 'Test results published'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo 'Packaging the applications...'
                script {
                    if (isUnix()) {
                        sh './mvnw package -DskipTests -Drevision=${NEXT_VERSION}'
                    } else {
                        bat '.\\mvnw.cmd package -DskipTests -Drevision=%NEXT_VERSION%'
                    }
                }
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo 'Archiving artifacts...'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        
        stage('Code Coverage') {
            steps {
                echo 'Generating code coverage report...'
                script {
                    if (isUnix()) {
                        sh './mvnw verify -Drevision=${NEXT_VERSION}'
                    } else {
                        bat '.\\mvnw.cmd verify -Drevision=%NEXT_VERSION%'
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            emailext(
                subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext(
                subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
                body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
                    <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
