pipeline {
    agent any
    
    environment {
        // กำหนดตัวแปรสำหรับ deploy
        DEPLOY_PATH = '/var/www/html'  // เปลี่ยนตามที่ต้องการ
        PROJECT_NAME = 'test-webapp'
    }
    
    stages {
        stage('Preparation') {
            steps {
                // ทำความสะอาด workspace
                cleanWs()
                // Clone โปรเจคจาก Git
                git 'https://github.com/tamatee/test_cve_2024_23897.git'
            }
        }
        
        stage('Deploy') {
            steps {
                // สร้าง backup ก่อน deploy
                sh """
                    if [ -d "${DEPLOY_PATH}/${PROJECT_NAME}" ]; then
                        mv ${DEPLOY_PATH}/${PROJECT_NAME} ${DEPLOY_PATH}/${PROJECT_NAME}_backup_\$(date +%Y%m%d_%H%M%S)
                    fi
                """
                
                // Copy ไฟล์ไปยัง server
                sh """
                    mkdir -p ${DEPLOY_PATH}/${PROJECT_NAME}
                    cp -r * ${DEPLOY_PATH}/${PROJECT_NAME}/
                    chmod -R 755 ${DEPLOY_PATH}/${PROJECT_NAME}
                """
            }
        }
        
        stage('Verify') {
            steps {
                // ตรวจสอบว่า deploy สำเร็จ
                sh "curl -f http://localhost/${PROJECT_NAME}/index.html"
            }
        }
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            // ถ้า deploy ไม่สำเร็จ ให้ rollback
            sh """
                if [ -d "${DEPLOY_PATH}/${PROJECT_NAME}_backup_*" ]; then
                    rm -rf ${DEPLOY_PATH}/${PROJECT_NAME}
                    mv ${DEPLOY_PATH}/${PROJECT_NAME}_backup_* ${DEPLOY_PATH}/${PROJECT_NAME}
                fi
            """
            echo 'Deployment failed! Rolled back to previous version.'
        }
    }
}