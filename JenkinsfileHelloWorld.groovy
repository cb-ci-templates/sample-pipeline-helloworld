pipeline {
    agent none
    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                sleep 10
                echo "test"
            }
        }
    }
}