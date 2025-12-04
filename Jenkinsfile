pipeline {
    agent any

    environment {
        // Jenkins credentials
        DOCKER_USER = credentials('dockerhub-user')
        DOCKER_PASS = credentials('dockerhub-pass')

        // MySQL
        MYSQL_ROOT_PASSWORD = "123456"
        MYSQL_DATABASE = "vegana_store"
    }

    stages {

        // ============================================
        // 1) Checkout Code
        // ============================================
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ============================================
        // 2) Start MySQL (Docker)
        // ============================================
        stage('Start MySQL') {
            steps {
                script {
                    sh '''
                    echo "🟦 Removing old MySQL container..."
                    docker rm -f mysql-vegana || true

                    echo "🟩 Starting MySQL 8 container..."
                    docker run -d --name mysql-vegana \
                        -e MYSQL_ROOT_PASSWORD=123456 \
                        -e MYSQL_DATABASE=vegana_store \
                        -p 3306:3306 \
                        mysql:8.0

                    echo "⏳ Waiting for MySQL to be ready..."
                    for i in {1..40}; do
                        docker exec mysql-vegana mysqladmin ping -uroot -p123456 --silent && break
                        sleep 2
                    done
                    '''
                }
            }
        }

        // ============================================
        // 3) Build Spring Boot (skip tests)
        // ============================================
        stage('Build Spring Boot') {
            steps {
                sh '''
                echo "🛠 Building Spring Boot..."
                mvn -q -DskipTests clean package
                '''
            }
        }

        // ============================================
        // 4) Start Spring Boot backend
        // ============================================
        stage('Start Backend') {
            steps {
                sh '''
                echo "🚀 Starting Spring Boot app..."
                pkill -f "spring-boot:run" || true
                nohup mvn spring-boot:run > app.log 2>&1 &

                echo "⏳ Waiting for backend..."
                sleep 40

                curl -f http://localhost:9090 || (echo "❌ Backend not reachable!" && exit 1)
                echo "🟩 Backend is UP!"
                '''
            }
        }

        // ============================================
        // 5) Run Selenium + TestNG automation tests
        // ============================================
        stage('Automation Tests') {
            steps {
                sh '''
                mkdir -p test-output/reports test-output/screenshots test-output/logs

                echo "🧪 Running TestNG Selenium tests..."
                mvn -q test -DsuiteXmlFile=src/test/resources/testng.xml || true
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'app.log', allowEmptyArchive: true
                }
            }
        }

        // ============================================
        // 6) Build & Push Docker image to Docker Hub
        // ============================================
        stage('Docker Build & Push') {
            steps {
                sh '''
                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                echo "🐳 Building Docker image..."
                docker build -t $DOCKER_USER/vegana-shop:latest .

                echo "⬆️ Pushing image to Docker Hub..."
                docker push $DOCKER_USER/vegana-shop:latest
                '''
            }
        }

        // ============================================
        // 7) Deploy container (redeploy latest)
        // ============================================
        stage('Deploy') {
            steps {
                sh '''
                echo "♻️ Redeploying container..."

                docker rm -f vegana || true

                docker run -d --name vegana \
                    -p 9090:9090 \
                    $DOCKER_USER/vegana-shop:latest

                echo "🟩 Deployment completed!"
                '''
            }
        }
    }

    // ============================================
    // CLEANUP
    // ============================================
    post {
        always {
            sh '''
            echo "🧹 Cleaning up backend process..."
            pkill -f "spring-boot:run" || true

            echo "🧹 Cleaning up MySQL..."
            docker rm -f mysql-vegana || true
            '''
        }
    }
}
