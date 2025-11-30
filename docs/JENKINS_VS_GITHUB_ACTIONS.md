# 🔄 So sánh GitHub Actions vs Jenkins Pipeline

## 📊 Tổng quan

Cả hai pipeline đều thực hiện **cùng một quy trình CI/CD**, nhưng có sự khác biệt về cách triển khai và tính năng.

---

## ✅ Tính năng giống nhau

| Tính năng | GitHub Actions | Jenkins |
|-----------|---------------|---------|
| **Checkout Code** | ✅ `actions/checkout@v3` | ✅ `checkout scm` |
| **Setup JDK 17** | ✅ `actions/setup-java@v3` | ✅ Built-in |
| **MySQL Setup** | ✅ Services | ✅ Docker Compose |
| **Database Import** | ✅ `mysql` command | ✅ `docker exec mysql` |
| **Build App** | ✅ `mvn clean package` | ✅ `mvn clean package` |
| **Start App** | ✅ `mvn spring-boot:run` | ✅ `mvn spring-boot:run` |
| **Run Tests** | ✅ `mvn test` | ✅ `mvn test` |
| **Archive Reports** | ✅ `actions/upload-artifact@v4` | ✅ `archiveArtifacts` |
| **Cleanup** | ✅ `pkill` | ✅ `pkill` |

---

## 🔀 Khác biệt chính

### 1. **Setup & Configuration**

#### GitHub Actions
```yaml
# .github/workflows/ci-cd.yml
on:
  push:
    branches: [ main ]
jobs:
  test:
    runs-on: ubuntu-latest
```

**Ưu điểm:**
- ✅ Setup tự động (built-in GitHub)
- ✅ Không cần server riêng
- ✅ Free cho public repos
- ✅ Auto-scaling

**Nhược điểm:**
- ❌ Phụ thuộc vào GitHub
- ❌ Limited customization
- ❌ Không có Selenium Grid

#### Jenkins
```groovy
// Jenkinsfile
pipeline {
    agent any
    stages {
        stage('Build') { ... }
    }
}
```

**Ưu điểm:**
- ✅ Full control & customization
- ✅ Selenium Grid support
- ✅ Self-hosted (privacy)
- ✅ Plugin ecosystem

**Nhược điểm:**
- ⚙️ Cần setup server
- ⚙️ Cần maintain
- ⚙️ Cần cấu hình Docker

---

### 2. **MySQL Setup**

#### GitHub Actions
```yaml
services:
  mysql:
    image: mysql:8.0
    env:
      MYSQL_ROOT_PASSWORD: 123456
    ports:
      - 3306:3306
```

**Đặc điểm:**
- ✅ Tự động start/stop
- ✅ Isolated per job
- ✅ Health checks built-in

#### Jenkins
```groovy
// docker-compose.yml
services:
  mysql:
    image: mysql:8.0
    container_name: mysql8
```

**Đặc điểm:**
- ✅ Persistent data (volumes)
- ✅ Shared across builds
- ✅ Manual control

---

### 3. **Selenium Grid**

#### GitHub Actions
```yaml
# ❌ Không có Selenium Grid
# Chỉ chạy headless Chrome trực tiếp
```

**Giới hạn:**
- ❌ Không có distributed testing
- ❌ Không có parallel execution
- ❌ Chỉ 1 browser instance

#### Jenkins
```yaml
# docker-compose.yml
services:
  selenium-hub:
    image: selenium/hub:4.15.0
  selenium-chrome:
    image: selenium/node-chrome:4.15.0
```

**Ưu điểm:**
- ✅ Distributed testing
- ✅ Parallel execution
- ✅ Multiple browser nodes
- ✅ Scale horizontally

---

### 4. **Test Reports**

#### GitHub Actions
```yaml
- name: Upload Reports
  uses: actions/upload-artifact@v4
  with:
    name: test-report
    path: test-output/
```

**Cách xem:**
- Download artifacts từ Actions tab
- View trong GitHub UI

#### Jenkins
```groovy
archiveArtifacts artifacts: 'test-output/**/*'
publishTestNGResults(...)
```

**Cách xem:**
- View trong Jenkins UI
- TestNG Results plugin
- HTML reports
- Screenshots gallery

---

### 5. **Environment Variables**

#### GitHub Actions
```yaml
env:
  MYSQL_PWD: 123456
  GITHUB_ACTIONS: true
```

**Auto-detected:**
- ✅ `GITHUB_ACTIONS` = true
- ✅ `RUNNER_OS`
- ✅ `GITHUB_REPOSITORY`

#### Jenkins
```groovy
environment {
    MYSQL_HOST = "mysql"
    GITHUB_ACTIONS = "true"  // Manual set
}
```

**Manual setup:**
- ⚙️ Cần set `GITHUB_ACTIONS=true` manually
- ⚙️ Custom variables

---

## 📈 Performance Comparison

| Metric | GitHub Actions | Jenkins |
|--------|---------------|---------|
| **Startup Time** | ~30s | ~10s (local) |
| **Build Time** | ~5-10 min | ~5-10 min |
| **Parallel Jobs** | ✅ Unlimited | ⚙️ Limited by resources |
| **Cost** | Free (public) | Free (self-hosted) |

---

## 🎯 Khi nào dùng gì?

### Dùng GitHub Actions khi:
- ✅ Public repository
- ✅ Muốn setup nhanh
- ✅ Không cần Selenium Grid
- ✅ Muốn cloud-based
- ✅ Team nhỏ

### Dùng Jenkins khi:
- ✅ Private repository
- ✅ Cần full control
- ✅ Cần Selenium Grid
- ✅ Cần customization cao
- ✅ Enterprise environment
- ✅ Cần on-premise

---

## 🔄 Migration Guide

### Từ GitHub Actions → Jenkins

1. **Copy logic từ `.github/workflows/*.yml`**
2. **Convert sang Jenkinsfile syntax**
3. **Setup Docker Compose cho services**
4. **Configure Jenkins plugins**
5. **Test pipeline**

### Từ Jenkins → GitHub Actions

1. **Extract logic từ Jenkinsfile**
2. **Convert sang YAML format**
3. **Use GitHub Actions services**
4. **Configure workflow triggers**
5. **Test workflow**

---

## 📝 Code Comparison

### Start MySQL

**GitHub Actions:**
```yaml
services:
  mysql:
    image: mysql:8.0
```

**Jenkins:**
```yaml
# docker-compose.yml
services:
  mysql:
    image: mysql:8.0
```

### Wait for MySQL

**GitHub Actions:**
```yaml
- name: Wait for MySQL
  run: |
    timeout 60 bash -c 'until mysqladmin ping...'
```

**Jenkins:**
```groovy
sh '''
    for i in {1..30}; do
        docker exec mysql8 mysqladmin ping...
    done
'''
```

### Run Tests

**GitHub Actions:**
```yaml
- name: Run Tests
  run: mvn test
```

**Jenkins:**
```groovy
stage('Run Tests') {
    sh 'mvn test'
}
```

---

## ✅ Kết luận

**Cả hai đều tốt**, tùy vào nhu cầu:

- **GitHub Actions**: Đơn giản, nhanh, cloud-based
- **Jenkins**: Mạnh mẽ, flexible, self-hosted

**Pipeline logic giống nhau 100%**, chỉ khác cách triển khai!

---

**Happy CI/CD! 🚀**

