pipeline {
    agent none

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                sh "echo ${CHANGES, showPaths=true} > chnagelog.xml"
                sh "cat > chnagelog.xml"
            }
        }
    }
}
