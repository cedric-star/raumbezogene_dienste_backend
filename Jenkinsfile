pipeline {
    agent any

    environment {
        DEPLOY_HOST = 'host.containers.internal'
        DEPLOY_USER = 'cedric'
        DEPLOY_DIR  = '/home/cedric/raumbezogene_dienste_backend'
        REPO_URL    = 'https://github.com/cedric-star/raumbezogene_dienste_backend.git'
    }

    stages {
        stage('Deploy on Host') {
            steps {
                sshagent(credentials: ['host-ssh-deplay']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                            if [ -d "${DEPLOY_DIR}/.git" ]; then
                                cd ${DEPLOY_DIR} && git pull
                            else
                                git clone ${REPO_URL} ${DEPLOY_DIR}
                            fi
                            cd ${DEPLOY_DIR} && podman compose down && podman compose up -d --build
                        '
                    """
                }
            }
        }
    }
}