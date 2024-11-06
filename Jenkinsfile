pipeline {
    agent any
    
    environment {
        PROJECT_PATH = 'C:\\Users\\FackG\\Desktop\\test_cve_2024_23897'
        DEPLOY_PATH = 'C:\\xampp\\htdocs\\test_cve'
        PORT = '8090'  // กำหนด port ที่ต้องการ
    }
    
    stages {
        stage('Clean') {
            steps {
                cleanWs()
            }
        }
        
        stage('Checkout') {
            steps {
                bat """
                    xcopy /s /e /y "${PROJECT_PATH}\\*" ".\\*"
                """
            }
        }
        
        stage('Configure Port') {
            steps {
                // แก้ไขไฟล์ httpd.conf ของ XAMPP
                bat """
                    echo "Configuring Apache port..."
                    (
                        echo Listen ${PORT}
                        echo ^<VirtualHost *:${PORT}^>
                        echo     DocumentRoot "${DEPLOY_PATH}"
                        echo     ^<Directory "${DEPLOY_PATH}"^>
                        echo         Options Indexes FollowSymLinks MultiViews
                        echo         AllowOverride All
                        echo         Require all granted
                        echo     ^</Directory^>
                        echo ^</VirtualHost^>
                    ) > "C:\\xampp\\apache\\conf\\extra\\httpd-vhosts-${PORT}.conf"
                    
                    echo "Include conf/extra/httpd-vhosts-${PORT}.conf" >> "C:\\xampp\\apache\\conf\\httpd.conf"
                """
                
                // รีสตาร์ท Apache
                bat """
                    net stop Apache2.4
                    net start Apache2.4
                """
            }
        }
        
        stage('Deploy') {
            steps {
                bat """
                    echo "Backing up existing deployment..."
                    if exist "${DEPLOY_PATH}" (
                        ren "${DEPLOY_PATH}" "test_cve_backup_%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%"
                    )
                    
                    echo "Deploying to ${DEPLOY_PATH}"
                    mkdir "${DEPLOY_PATH}"
                    xcopy /s /e /y ".\\*" "${DEPLOY_PATH}\\"
                """
            }
        }
        
        stage('Verify') {
            steps {
                // ตรวจสอบว่าเว็บทำงานบน port ที่กำหนด
                bat """
                    timeout /t 5 /nobreak
                    curl -f http://localhost:${PORT}/index.html || echo "Warning: Could not verify deployment"
                """
            }
        }
    }
    
    post {
        success {
            echo "Website deployed successfully! Access at http://localhost:${PORT}"
        }
        failure {
            bat """
                echo "Deployment failed, attempting rollback..."
                if exist "${DEPLOY_PATH}_backup_*" (
                    rmdir /s /q "${DEPLOY_PATH}"
                    for /d %%i in ("${DEPLOY_PATH}_backup_*") do (
                        ren "%%i" "test_cve"
                        goto :done
                    )
                    :done
                )
            """
            error 'Deployment failed and rolled back to previous version.'
        }
        always {
            cleanWs()
        }
    }
}