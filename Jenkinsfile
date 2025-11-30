pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-21'
            args '-v /var/jenkins_home/.m2:/root/.m2'
        }
    }

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
        timeout(time: 40, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        ansiColor('xterm')
    }

    stages {

        /* ===========================
           🔍 CHECKOUT
        ============================ */
        stage('🔍 Checkout Code') {
            steps {
                echo '📥 Checking out code from repository...'
                checkout scm
                sh 'git rev-parse HEAD > .git/commit-id'
                sh 'cat .git/commit-id'
            }
        }

        /* ===========================
           ⚙️ ENV INFO
        ============================ */
        stage('⚙️ Setup Environment') {
            steps {
                echo '🔧 Setting up build environment (Docker Maven Agent)...'
                sh '''
                    echo "=== Environment Info ==="
                    java -version
                    mvn -version
                    echo "========================"
                '''
            }
        }

        /* ===========================
           🐬 WAIT FOR MYSQL
        ============================ */
        stage('🐬 Wait for MySQL') {
            steps {
                echo '⏳ Waiting for MySQL...'
                sh '''
                    for i in {1..20}; do
                        if nc -z ${MYSQL_HOST} 3306; then
                            echo "✅ MySQL is ready!"
                            exit 0
                        fi
                        echo "Waiting for MySQL ($i/20)..."
                        sleep 3
                    done
                    echo "❌ MySQL did not start!"
                    exit 1
                '''
            }
        }

        /* ===========================
           🗄️ SETUP DATABASE
        ============================ */
        stage('🗄️ Setup Database') {
            steps {
                sh '''
                    echo "Creating database if not exists..."
                    echo "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE};" \
                        | mysql -h ${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASS} || true

                    if [ -f vegana.sql ]; then
                        echo "Importing DB schema..."
                        mysql -h ${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASS} ${MYSQL_DATABASE} < vegana.sql
                        echo "✅ Schema imported!"
                    else
                        echo "⚠️ vegana.sql not found → skipping import"
                    fi
                '''
            }
        }

        /* ===========================
           🔨 BUILD APP
        ============================ */
        stage('🔨 Build Application') {
            steps {
                sh '''
                    echo "🏗️ Building Spring Boot app..."
                    mvn clean package -DskipTests
                    echo "✅ Build done!"
                '''
            }
        }

        /* ===========================
           🚀 START APP
        ============================ */
        stage('🚀 Start Spring Boot Application') {
            steps {
                sh '''
                    echo "Starting Spring Boot in background..."
                    nohup mvn spring-boot:run \
                        -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=false" \
                        > app.log 2>&1 &

                    echo $! > app.pid
                    echo "PID: $(cat app.pid)"
                '''

                sh '''
                    echo "⏳ Waiting for app to start..."
                    for i in {1..30}; do
                        if curl -f http://localhost:${APP_PORT}/ >/dev/null 2>&1; then
                            echo "✅ App started!"
                            exit 0
                        fi
                        echo "($i/30) App not ready, retrying..."
                        sleep 3
                    done

                    echo "❌ App failed! Tail log:"
                    tail -50 app.log || true
                    exit 1
                '''
            }
        }

        /* ===========================
           🌐 CHECK GRID
        ============================ */
        stage('🌐 Check Selenium Grid') {
            steps {
                sh '''
                    for i in {1..10}; do
                        if curl -s http://selenium-hub:4444/wd/hub/status >/dev/null; then
                            echo "✅ Selenium Grid Ready"
                            exit 0
                        fi
                        echo "Waiting Selenium Grid ($i/10)..."
                        sleep 2
                    done
                    echo "⚠️ Grid offline → tests will use local Chrome"
                '''
            }
        }

        /* ===========================
           🧪 RUN TESTS
        ============================ */
        stage('🧪 Run Automation Tests') {
            steps {
                sh '''
                    mkdir -p test-output/reports test-output/screenshots
                    echo "Running TestNG tests..."
                    mvn test -DsuiteXmlFile=src/test/resources/testng.xml
                '''
            }
        }

        /* ===========================
           📊 ARCHIVE RESULTS
        ============================ */
        stage('📊 Archive Test Results') {
            steps {
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                archiveArtifacts artifacts: 'app.log', allowEmptyArchive: true

                publishTestNGResults(
                    testResultsPattern: 'target/surefire-reports/testng-results.xml',
                    escapeTestDescription: false,
                    escapeExceptionMsg: false
                )
            }
        }
    }

    /* ===========================
       🧹 POST ACTIONS
    ============================ */
    post {
        always {
            sh '''
                if [ -f app.pid ]; then
                    kill $(cat app.pid) || true
                    rm -f app.pid
                fi
                pkill -f "spring-boot:run" || true
                echo "🧹 App stopped."
            '''
        }
        success {
            echo "🎉 SUCCESS: CI/CD Pipeline Completed!"
        }
        failure {
            echo "❌ FAILURE: Check console log"
        }
    }
}
