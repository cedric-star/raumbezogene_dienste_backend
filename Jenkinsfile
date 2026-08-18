pipeline {
    agent any

    triggers {
        githubPush()
    }

    stages {
        stage('Testdatei ablegen') {
            steps {
                sh 'echo "Testlauf am $(date)" > /home/cedric/jenkins_test.txt'
            }
        }
    }
}