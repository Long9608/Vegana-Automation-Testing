pipeline {
  agent any

  tools {
    jdk   'jdk17'
    maven 'maven'
  }

  environment {
    APP_PORT = '9090'                 // Port chạy app (tránh đụng Jenkins 8080)
    BASE_URL = "http://localhost:9090"
  }

  options {
    // ansiColor('xterm')             <-- Đã tắt dòng này để tránh lỗi "Invalid option type"
    timeout(time: 40, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build App') {
      steps {
        bat 'mvn clean package -DskipTests'
      }
    }

    stage('Start App') {
      steps {
        powershell(label: 'Start Spring Boot & wait', returnStatus: false, script: '''
          $ErrorActionPreference = "Stop"
          Write-Host "Starting Spring Boot on port $env:APP_PORT ..."

          $mvn = "mvn"
          
          # FIX LỖI: Tách thành 2 file log riêng biệt để tránh lỗi file lock
          $logOut = "app.log"
          $logErr = "app.err"
          
          $args = @("spring-boot:run", "-Dspring-boot.run.arguments=--server.port=$env:APP_PORT")

          # Chạy process ngầm (Hidden) và ghi log ra 2 file khác nhau
          $p = Start-Process $mvn -ArgumentList $args -RedirectStandardOutput $logOut -RedirectStandardError $logErr -WindowStyle Hidden -PassThru
          
          # Lưu PID để lát nữa kill
          $p.Id | Set-Content -Path app.pid -Encoding ascii
          Write-Host "Spring Boot PID: $($p.Id)"

          # Vòng lặp chờ App khởi động (tối đa 2 phút)
          $deadline = (Get-Date).AddMinutes(2)
          while ((Get-Date) -lt $deadline) {
            try {
              # Check health
              Invoke-WebRequest -UseBasicParsing "http://localhost:$env:APP_PORT" -TimeoutSec 3 | Out-Null
              Write-Host "✅ App is UP"
              exit 0
            } catch {
              Start-Sleep -Seconds 2
            }
          }

          # Nếu timeout thì in log ra để debug
          Write-Host "❌ App failed to start in time"
          
          Write-Host "--- STDOUT (Last 50 lines) ---"
          if (Test-Path $logOut) { Get-Content -Tail 50 $logOut }
          
          Write-Host "--- STDERR (Last 50 lines) ---"
          if (Test-Path $logErr) { Get-Content -Tail 50 $logErr }
          
          exit 1
        ''')
      }
    }

    stage('Run UI Tests') {
      steps {
        bat 'mvn test -DbaseUrl=%BASE_URL%'
      }
    }

    stage('Archive Reports') {
      steps {
        archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
        archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
        
        # Archive cả 2 file log mới
        archiveArtifacts artifacts: 'app.log, app.err', allowEmptyArchive: true
      }
    }
  }

  post {
    always {
      echo 'Stopping Spring Boot safely...'
      powershell(label: 'Stop Spring Boot', returnStatus: true, script: '''
        $ErrorActionPreference = "SilentlyContinue"
        if (Test-Path "app.pid") {
          $pidVal = Get-Content "app.pid" | Select-Object -First 1
          if ($pidVal) { Stop-Process -Id $pidVal -Force }
          Remove-Item -Force "app.pid"
        } else {
          # Fallback: kill theo tên process nếu không tìm thấy file PID
          Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.Path -like "*maven*" -or $_.ProcessName -eq "java" } | Stop-Process -Force -ErrorAction SilentlyContinue
        }
        Write-Host "Cleanup complete."
      ''')
    }
  }
}