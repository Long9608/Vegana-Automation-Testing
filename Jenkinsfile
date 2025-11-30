pipeline {
    agent any
    
    environment {
        MYSQL_HOST = "mysql"
        MYSQL_USER = "root"
        MYSQL_PASS = "123456"
        MYSQL_DATABASE = "vegana_store"
        APP_PORT = "8080"
        BASE_URL = "http://localhost:8080"
        SELENIUM_HUB_URL = "http://selenium-hub:4444"
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        ansiColor('xterm')
    }
    
    stages {
        stage('🔍 Checkout Code') {
            steps {
                echo '📥 Checking out code from repository...'
                checkout scm
                sh 'git rev-parse HEAD > .git/commit-id'
                sh 'cat .git/commit-id'
            }
        }
        
        stage('⚙️ Setup Environment') {
            steps {
                echo '🔧 Setting up environment...'
                script {
                    sh '''
                        echo "=== Environment Info ==="
                        java -version
                        mvn -version
                        docker --version
                        echo "========================"
                    '''
                }
            }
        }
        
        stage('🐬 Wait for MySQL') {
            steps {
                echo '⏳ Waiting for MySQL to be ready...'
                script {
                    sh '''
                        echo "Checking MySQL connection..."
                        for i in {1..30}; do
                            if docker exec mysql8 mysqladmin ping -h localhost -u${MYSQL_USER} -p${MYSQL_PASS} --silent 2>/dev/null; then
                                echo "✅ MySQL is ready!"
                                exit 0
                            fi
                            echo "Attempt $i/30: MySQL not ready yet..."
                            sleep 2
                        done
                        echo "❌ MySQL failed to start"
                        exit 1
                    '''
                }
            }
        }
        
        stage('🗄️ Setup Database') {
            steps {
                echo '📊 Setting up database schema...'
                script {
                    sh '''
                        echo "Creating database if not exists..."
                        docker exec -i mysql8 mysql -u${MYSQL_USER} -p${MYSQL_PASS} \
                            -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE};" || true
                        
                        if [ -f vegana.sql ]; then
                            echo "Importing schema from vegana.sql..."
                            docker exec -i mysql8 mysql -u${MYSQL_USER} -p${MYSQL_PASS} ${MYSQL_DATABASE} < vegana.sql
                            echo "✅ Database schema imported successfully!"
                        else
                            echo "⚠️ WARNING: vegana.sql not found, skipping import."
                        fi
                    '''
                }
            }
        }
        
        stage('🔨 Build Application') {
            steps {
                echo '🏗️ Building Spring Boot application...'
                sh '''
                    mvn clean package -DskipTests -q
                    echo "✅ Build completed!"
                '''
            }
        }
        
        stage('🚀 Start Spring Boot Application') {
            steps {
                echo '🌐 Starting Spring Boot application...'
                script {
                    sh '''
                        echo "Starting application in background..."
                        nohup mvn spring-boot:run \
                            -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=false -Dhibernate.hbm2ddl.auto=none" \
                            > app.log 2>&1 &
                        echo $! > app.pid
                        echo "Application PID: $(cat app.pid)"
                    '''
                    
                    sh '''
                        echo "⏳ Waiting for application to start..."
                        for i in {1..30}; do
                            if curl -f http://localhost:${APP_PORT} >/dev/null 2>&1; then
                                echo "✅ Application started successfully!"
                                exit 0
                            fi
                            echo "Attempt $i/30: Application not ready yet, waiting..."
                            sleep 3
                        done
                        echo "❌ Application failed to start. Last 50 lines of app.log:"
                        tail -50 app.log || true
                        exit 1
                    '''
                }
            }
        }
        
        stage('🌐 Check Selenium Grid') {
            steps {
                echo '🔍 Checking Selenium Grid availability...'
                script {
                    sh '''
                        echo "Checking Selenium Hub..."
                        for i in {1..10}; do
                            if curl -f http://selenium-hub:4444/wd/hub/status >/dev/null 2>&1; then
                                echo "✅ Selenium Hub is ready!"
                                curl -s http://selenium-hub:4444/wd/hub/status | head -20
                                exit 0
                            fi
                            echo "Attempt $i/10: Selenium Hub not ready yet..."
                            sleep 2
                        done
                        echo "⚠️ Selenium Hub not available, will use local Chrome"
                    '''
                }
            }
        }
        
        stage('🧪 Run Automation Tests') {
            steps {
                echo '🎯 Running Selenium automation tests...'
                script {
                    sh '''
                        mkdir -p test-output/reports test-output/screenshots test-output/logs
                        export GITHUB_ACTIONS=true
                        export SELENIUM_HUB_URL=${SELENIUM_HUB_URL}
                        
                        echo "Running TestNG suite..."
                        mvn test -DsuiteXmlFile=src/test/resources/testng.xml || true
                        
                        echo "✅ Tests completed!"
                    '''
                }
            }
        }
        
        stage('📊 Archive Test Results') {
            steps {
                echo '📦 Archiving test results and reports...'
                script {
                    // Archive Extent Reports
                    archiveArtifacts artifacts: 'test-output/reports/**/*', allowEmptyArchive: true
                    
                    // Archive Screenshots
                    archiveArtifacts artifacts: 'test-output/screenshots/**/*', allowEmptyArchive: true
                    
                    // Archive Surefire Reports
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    
                    // Archive Application Logs
                    archiveArtifacts artifacts: 'app.log', allowEmptyArchive: true
                    
                    // Publish TestNG Results
                    publishTestNGResults(
                        testResultsPattern: 'target/surefire-reports/testng-results.xml',
                        reportFilesPattern: 'target/surefire-reports/**/*',
                        escapeTestDescription: false,
                        escapeExceptionMsg: false
                    )
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up...'
            script {
                // Stop Spring Boot application
                sh '''
                    if [ -f app.pid ]; then
                        PID=$(cat app.pid)
                        echo "Stopping application (PID: $PID)..."
                        kill $PID 2>/dev/null || true
                        sleep 2
                        kill -9 $PID 2>/dev/null || true
                        rm -f app.pid
                    fi
                    pkill -f "spring-boot:run" || true
                    echo "✅ Application stopped"
                '''
            }
        }
        success {
            echo '✅ Pipeline completed successfully!'
            script {
                sh '''
                    echo "=== Build Summary ==="
                    echo "✅ All stages completed successfully"
                    echo "📊 Check artifacts for test reports"
                    echo "======================"
                '''
            }
        }
        failure {
            echo '❌ Pipeline failed!'
            script {
                sh '''
                    echo "=== Build Failed ==="
                    echo "❌ Check console output for details"
                    echo "📋 Check artifacts for logs"
                    echo "====================="
                '''
            }
        }
        unstable {
            echo '⚠️ Pipeline completed with warnings!'
        }
    }
}

