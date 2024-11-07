pipeline {
    agent any
    environment {
        DEPLOY_PATH = '/var/www/html'
        PROJECT_NAME = 'test-webapp'
        APACHE_CONFIG = '/etc/apache2/sites-available' // Apache config path
    }
    stages {
        stage('Preparation') {
            steps {
                cleanWs()
                git 'https://github.com/tamatee/test_cve_2024_23897.git'
            }
        }
        stage('Deploy') {
            steps {
                // Create backup
                sh """
                    if [ -d "${DEPLOY_PATH}/${PROJECT_NAME}" ]; then
                        mv ${DEPLOY_PATH}/${PROJECT_NAME} ${DEPLOY_PATH}/${PROJECT_NAME}_backup_\$(date +%Y%m%d_%H%M%S)
                    fi
                """
                
                // Deploy files
                sh """
                    mkdir -p ${DEPLOY_PATH}/${PROJECT_NAME}
                    cp -r * ${DEPLOY_PATH}/${PROJECT_NAME}/
                    chmod -R 755 ${DEPLOY_PATH}/${PROJECT_NAME}
                """

                // Create Apache virtual host configuration
                sh """
                    echo '<VirtualHost *:80>
                        ServerName localhost
                        DocumentRoot ${DEPLOY_PATH}/${PROJECT_NAME}
                        <Directory ${DEPLOY_PATH}/${PROJECT_NAME}>
                            Options Indexes FollowSymLinks
                            AllowOverride All
                            Require all granted
                        </Directory>
                    </VirtualHost>' | sudo tee ${APACHE_CONFIG}/${PROJECT_NAME}.conf
                """

                // Enable the site and restart Apache
                sh """
                    sudo a2ensite ${PROJECT_NAME}
                    sudo a2dissite 000-default.conf
                    sudo systemctl restart apache2
                """
            }
        }
        stage('Verify') {
            steps {
                // Wait for Apache to fully restart
                sh 'sleep 5'
                // Verify deployment
                sh "curl -f http://localhost/index.html"
            }
        }
    }
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            // Rollback if deployment fails
            sh """
                if [ -d "${DEPLOY_PATH}/${PROJECT_NAME}_backup_*" ]; then
                    rm -rf ${DEPLOY_PATH}/${PROJECT_NAME}
                    mv ${DEPLOY_PATH}/${PROJECT_NAME}_backup_* ${DEPLOY_PATH}/${PROJECT_NAME}
                fi
                
                # Restore original Apache configuration
                sudo a2dissite ${PROJECT_NAME}
                sudo a2ensite 000-default.conf
                sudo systemctl restart apache2
            """
            echo 'Deployment failed! Rolled back to previous version.'
        }
    }
}