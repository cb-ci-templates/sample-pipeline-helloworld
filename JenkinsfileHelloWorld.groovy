pipeline {
    agent none

    stages {
        stage('Main') {
            environment {
                JENKINS_TOKEN = credentials("jenkins-token")
            }
            steps {
                echo "Hello world"
                sh "curl -o changelog.xml https://${JENKINS_TOKEN}@sda.acaternberg.flow-training.beescloud.com/sb/job/ci-templates-demo/job/testChangeLog/6/api/xml?wrapper=changes&xpath=//changeSet//comment"
                sh "cat changelog.xml"
                sh "env|sort"
            }
        }
    }
}
