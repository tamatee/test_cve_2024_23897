pipeline {
    agent any
    
    environment {
        // กำหนดพาธสำหรับ Windows
        DEPLOY_PATH = 'C:\\xampp\\htdocs'  // สำหรับ XAMPP
        // หรือ 'C:\\inetpub\\wwwroot'     // สำหรับ IIS
        PROJECT_NAME = 'test-webapp'
    }
    
    stages {
        stage('Preparation') {
            steps {
                // ทำความสะอาด workspace
                cleanWs()
                // Clone โปรเจค
                git 'https://github.com/yourusername/test-webapp.git'
            }
        }
        
        stage('Backup') {
            steps {
                // สร้าง backup ใน Windows
                bat """
                    if exist "${DEPLOY_PATH}\\${PROJECT_NAME}" (
                        rename "${DEPLOY_PATH}\\${PROJECT_NAME}" "${PROJECT_NAME}_backup_%date:~10,4%%date:~4,2%%date:~7,2%"
                    )
                """
            }
        }
        
        stage('Deploy') {
            steps {
                // Copy ไฟล์ไปยัง web server
                bat """
                    if not exist "${DEPLOY_PATH}\\${PROJECT_NAME}" mkdir "${DEPLOY_PATH}\\${PROJECT_NAME}"
                    xcopy /s /y "*.html" "${DEPLOY_PATH}\\${PROJECT_NAME}\\"
                    xcopy /s /y "*.css" "${DEPLOY_PATH}\\${PROJECT_NAME}\\"
                    xcopy /s /y "*.js" "${DEPLOY_PATH}\\${PROJECT_NAME}\\"
                """
            }
        }
        
        stage('Verify') {
            steps {
                // ตรวจสอบการ deploy
                bat 'curl -f http://localhost/%PROJECT_NAME%/index.html'
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            // Rollback ใน Windows
            bat """
                if exist "${DEPLOY_PATH}\\${PROJECT_NAME}_backup_*" (
                    rmdir /s /q "${DEPLOY_PATH}\\${PROJECT_NAME}"
                    rename "${DEPLOY_PATH}\\${PROJECT_NAME}_backup_*" "${PROJECT_NAME}"
                )
            """
            echo 'Deployment failed! Rolled back to previous version.'
        }
    }
}