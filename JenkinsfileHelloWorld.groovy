pipeline {
    agent none

    stages {
        stage('Main') {
            environment
            steps {
                echo "Hello world"
                withCredentials([string(credentialsId: 'jenkins-token', variable: 'jenkins-token')]) {
                    sh "curl -o changelog.xml https://${jenkins-token}@sda.acaternberg.flow-training.beescloud.com/sb/job/ci-templates-demo/job/testChangeLog/6/api/xml?wrapper=changes&xpath=//changeSet//comment"
                    sh "cat changelog.xml"
                    sh "env|sort"
                }
            }
        }
    }
}
