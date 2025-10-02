pipeline {
    agent any
    
    tools {
        jdk 'JDK-21'
    }
    
    environment {
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
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
                        sh '''
                            echo "JAVA_HOME: $JAVA_HOME"
                            ls -la "$JAVA_HOME"
                            ./mvnw clean compile
                        '''
                    } else {
                        bat '.\\mvnw.cmd clean compile'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                script {
                    if (isUnix()) {
                        sh './mvnw test'
                    } else {
                        bat '.\\mvnw.cmd test'
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
                        sh './mvnw package -DskipTests'
                    } else {
                        bat '.\\mvnw.cmd package -DskipTests'
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
                        sh './mvnw verify'
                    } else {
                        bat '.\\mvnw.cmd verify'
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
