pipeline {
    agent any
    
    environment {
        // กำหนด path ตามที่คุณต้องการ
        PROJECT_PATH = 'C:\\Users\\FackG\\Desktop\\test_cve_2024_23897'
        DEPLOY_PATH = 'C:\\xampp\\htdocs\\test_cve'  // path ที่จะ deploy ไป
    }
    
    stages {
        stage('Clean') {
            steps {
                // ทำความสะอาด workspace
                cleanWs()
            }
        }
        
        stage('Checkout') {
            steps {
                // ใช้โค้ดจาก local path แทนการดึงจาก Git
                bat """
                    xcopy /s /e /y "${PROJECT_PATH}\\*" ".\\*"
                """
            }
        }
        
        stage('Backup') {
            steps {
                // สร้าง backup ถ้ามีไฟล์เดิมอยู่
                bat """
                    if exist "${DEPLOY_PATH}" (
                        echo "Creating backup..."
                        ren "${DEPLOY_PATH}" "test_cve_backup_%date:~-4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%"
                    )
                """
            }
        }
        
        stage('Deploy') {
            steps {
                // Deploy ไฟล์ใหม่
                bat """
                    echo "Deploying to ${DEPLOY_PATH}"
                    if not exist "${DEPLOY_PATH}" (
                        mkdir "${DEPLOY_PATH}"
                    )
                    xcopy /s /e /y ".\\*" "${DEPLOY_PATH}\\"
                """
            }
        }
        
        stage('Verify') {
            steps {
                // ตรวจสอบว่าเว็บทำงาน
                bat 'curl -f http://localhost/test_cve/index.html || echo "Warning: Could not verify deployment"'
            }
        }
    }
    
    post {
        success {
            echo 'Website deployed successfully!'
        }
        failure {
            // ถ้าไม่สำเร็จให้ rollback
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
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}