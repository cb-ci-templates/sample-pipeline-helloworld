pipeline {
    agent none

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                withCredentials([string(credentialsId: 'jenkins-token', variable: 'jenkins-token')]) {
                    sh "curl -o changelog.xml https://${jenkins}@sda.acaternberg.flow-training.beescloud.com/sb/job/ci-templates-demo/job/testChangeLog/6/api/xml?wrapper=changes&xpath=//changeSet//comment"
                    sh "cat changelog.xml "
                }curl ch
            }
        }
    }
}
