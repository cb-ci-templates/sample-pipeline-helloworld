pipeline {
    agent none

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
            }
        }
    }
}
