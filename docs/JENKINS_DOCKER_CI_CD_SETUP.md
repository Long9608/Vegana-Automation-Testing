# 🚀 VEGANA SHOP – CI/CD PIPELINE (Docker Jenkins + Spring Boot + MySQL + Selenium TestNG)

[![Jenkins](https://img.shields.io/badge/Jenkins-Docker-blue.svg)](https://www.jenkins.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Selenium](https://img.shields.io/badge/Selenium-4.15.0-orange.svg)](https://www.selenium.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)

Dự án này cung cấp **hệ thống CI/CD hoàn chỉnh**, bao gồm:

✅ **Dockerized Jenkins** - CI/CD server  
✅ **MySQL Database** - Containerized database  
✅ **Spring Boot Application** - Backend API  
✅ **Selenium Grid** - Distributed test execution  
✅ **Selenium UI Automation** - TestNG framework  
✅ **Jenkins Pipeline** - Automated CI/CD  
✅ **Test Reports** - Extent Reports + Screenshots  

---

## 📁 Cấu trúc Project

```
Vegana-Automation-Testing/
│
├── docker-compose.yml          # Docker Compose cho Jenkins + MySQL + Selenium Grid
├── Jenkinsfile                 # Jenkins Pipeline CI/CD
│
├── src/                        # Spring Boot Application
│   ├── main/java/              # Source code
│   ├── main/resources/         # Config files
│   └── test/java/              # Selenium TestNG tests
│
├── vegana.sql                  # Database schema
│
├── test-output/                # Test reports (generated)
│   ├── reports/                # Extent Reports
│   ├── screenshots/            # Screenshots on failure
│   └── logs/                   # Test logs
│
└── docs/                       # Documentation
    ├── JENKINS_DOCKER_CI_CD_SETUP.md
    ├── GITHUB_ACTIONS_CI_CD_SETUP.md
    └── TEST_README.md
```

---

## 🐳 Docker Compose Setup

### Services Included

1. **Jenkins** (Port 8080) - CI/CD server
2. **MySQL 8.0** (Port 3306) - Database
3. **Selenium Hub** (Port 4444) - Grid hub
4. **Selenium Chrome Node** - Chrome browser node
5. **Selenium Firefox Node** - Firefox browser node (optional)

### Quick Start

```bash
# 1. Start all services
docker-compose up -d

# 2. Check services status
docker-compose ps

# 3. View Jenkins logs
docker-compose logs -f jenkins

# 4. Access Jenkins
# http://localhost:8080
```

### Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## 🔧 Jenkins Pipeline Configuration

### Bước 1: Access Jenkins

1. Start Docker Compose: `docker-compose up -d`
2. Truy cập: `http://localhost:8080`
3. Lấy initial password:
   ```bash
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

### Bước 2: Install Required Plugins

Vào **Manage Jenkins** → **Manage Plugins** → **Available**, cài đặt:

- ✅ **Pipeline**
- ✅ **Docker Pipeline**
- ✅ **TestNG Results**
- ✅ **AnsiColor**
- ✅ **GitHub Integration** (nếu dùng GitHub)
- ✅ **Credentials Binding**

### Bước 3: Create Pipeline Job

1. **New Item** → Nhập tên: `vegana-shop-ci-cd`
2. Chọn **Pipeline** → **OK**
3. Cấu hình:
   - **Pipeline definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: URL của repository
   - **Branch**: `*/main` hoặc `*/master`
   - **Script Path**: `Jenkinsfile`
4. **Save**

### Bước 4: Run Pipeline

Click **Build Now** để chạy pipeline lần đầu.

---

## 🧪 Selenium Test Configuration

### Headless Mode trong Jenkins

Tests tự động chạy **headless** khi detect `GITHUB_ACTIONS` environment variable:

```java
// BaseTest.java
private final boolean IS_GITHUB = 
    System.getenv("GITHUB_ACTIONS") != null;

if (IS_GITHUB) {
    co.addArguments("--headless=new");
    co.addArguments("--no-sandbox");
    co.addArguments("--disable-dev-shm-usage");
}
```

### Selenium Grid (Optional)

Nếu muốn dùng Selenium Grid:

```java
// Update BaseTest.java
String hubUrl = System.getenv("SELENIUM_HUB_URL");
if (hubUrl != null) {
    driver = new RemoteWebDriver(
        new URL(hubUrl + "/wd/hub"),
        co
    );
} else {
    driver = new ChromeDriver(co);
}
```

---

## 📊 Pipeline Stages

### 1. 🔍 Checkout Code
- Checkout code từ Git repository
- Lưu commit ID

### 2. ⚙️ Setup Environment
- Verify Java, Maven, Docker versions

### 3. 🐬 Wait for MySQL
- Đợi MySQL container ready
- Health check với retry

### 4. 🗄️ Setup Database
- Create database `vegana_store`
- Import schema từ `vegana.sql`

### 5. 🔨 Build Application
- `mvn clean package -DskipTests`
- Build Spring Boot JAR

### 6. 🚀 Start Spring Boot Application
- Start app in background
- Health check với curl
- Wait for app ready (max 90s)

### 7. 🌐 Check Selenium Grid
- Verify Selenium Hub available
- Fallback to local Chrome nếu không có

### 8. 🧪 Run Automation Tests
- Run TestNG suite
- Generate Extent Reports
- Capture screenshots on failure

### 9. 📊 Archive Test Results
- Archive Extent Reports
- Archive Screenshots
- Archive Surefire Reports
- Archive Application Logs
- Publish TestNG Results

### 10. 🧹 Cleanup (Post)
- Stop Spring Boot app
- Cleanup processes

---

## 📈 So sánh GitHub Actions vs Jenkins

| Feature | GitHub Actions | Jenkins |
|---------|---------------|---------|
| **Setup** | ✅ Tự động (built-in) | ⚙️ Cần cài đặt |
| **Docker** | ✅ Services support | ✅ Docker Pipeline |
| **MySQL** | ✅ Services | ✅ Docker Compose |
| **Selenium Grid** | ❌ Không có | ✅ Có (Docker) |
| **Reports** | ✅ Artifacts | ✅ Archive + TestNG Plugin |
| **Cost** | ✅ Free (public repo) | ✅ Free (self-hosted) |
| **Scalability** | ✅ Auto-scaling | ⚙️ Manual setup |

### Migration từ GitHub Actions → Jenkins

Pipeline Jenkins được **rewrite 100% logic giống GitHub Actions**:

✅ Build Spring Boot  
✅ Setup MySQL  
✅ Import Database  
✅ Start Application  
✅ Run Selenium Tests  
✅ Archive Reports  

**Khác biệt:**
- Jenkins: Có Selenium Grid support
- Jenkins: Có thể scale với multiple nodes
- GitHub Actions: Simpler setup, cloud-based

---

## 🔥 Advanced Features

### 1. Selenium Grid Integration

Sử dụng Selenium Grid để chạy tests song song:

```groovy
stage('Run Tests with Grid') {
    steps {
        sh '''
            export SELENIUM_HUB_URL=http://selenium-hub:4444
            mvn test -DsuiteXmlFile=src/test/resources/testng.xml
        '''
    }
}
```

### 2. Parallel Test Execution

Cấu hình TestNG để chạy parallel:

```xml
<!-- testng.xml -->
<suite name="Parallel Suite" parallel="methods" thread-count="4">
    <test name="Login Tests">
        <classes>
            <class name="com.java.automation.tests.user.LoginTest"/>
        </classes>
    </test>
</suite>
```

### 3. Dockerized Spring Boot (Optional)

Có thể dockerize Spring Boot app:

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
COPY target/vegana-shop-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🐛 Troubleshooting

### Lỗi: Jenkins không start

```bash
# Check logs
docker-compose logs jenkins

# Check permissions
ls -la jenkins/jenkins_home
```

### Lỗi: MySQL connection failed

```bash
# Check MySQL container
docker exec mysql8 mysqladmin ping -h localhost -uroot -p123456

# Check network
docker network ls
docker network inspect vegana-network
```

### Lỗi: Application không start

- Check `app.log` trong artifacts
- Verify MySQL connection
- Check port 8080: `lsof -i :8080`

### Lỗi: Selenium tests fail

- Check Selenium Grid status: `http://localhost:4444/wd/hub/status`
- Verify Chrome/Firefox nodes running
- Check screenshots trong artifacts

---

## 📚 Documentation

- **[Jenkins Setup Guide](docs/JENKINS_DOCKER_CI_CD_SETUP.md)** - Chi tiết setup Jenkins
- **[GitHub Actions Guide](docs/GITHUB_ACTIONS_CI_CD_SETUP.md)** - GitHub Actions setup
- **[Test Documentation](docs/TEST_README.md)** - Test framework guide

---

## ✅ Checklist

- [ ] Docker & Docker Compose đã cài
- [ ] Jenkins container đã start
- [ ] MySQL container đã start
- [ ] Selenium Grid đã start (optional)
- [ ] Jenkins plugins đã cài
- [ ] Pipeline job đã tạo
- [ ] Jenkinsfile đã có trong repo
- [ ] Pipeline chạy thành công
- [ ] Test reports được archive

---

## 🚀 Quick Start Commands

```bash
# Start everything
docker-compose up -d

# View logs
docker-compose logs -f

# Stop everything
docker-compose down

# Rebuild
docker-compose up -d --build

# Access Jenkins
open http://localhost:8080

# Access Selenium Grid
open http://localhost:4444
```

---

## 📝 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MYSQL_HOST` | `mysql` | MySQL container name |
| `MYSQL_USER` | `root` | MySQL username |
| `MYSQL_PASS` | `123456` | MySQL password |
| `MYSQL_DATABASE` | `vegana_store` | Database name |
| `APP_PORT` | `8080` | Spring Boot port |
| `SELENIUM_HUB_URL` | `http://selenium-hub:4444` | Selenium Grid URL |

---

## 🎯 Next Steps

1. ✅ Setup Jenkins với Docker Compose
2. ✅ Configure Pipeline job
3. ✅ Run first build
4. ⚡ Add more test cases
5. ⚡ Setup email notifications
6. ⚡ Add deployment stage
7. ⚡ Setup monitoring

---

**Happy Testing! 🚀**

Made with ❤️ for DevOps 2025

