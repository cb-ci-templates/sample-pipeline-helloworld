pipeline {
    agent {
        kubernetes {
            yaml '''
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                  - name: shell
                    image: caternberg/ci-utils:1.0
                    command:
                    - sleep
                    args:
                    - infinity
                '''
            defaultContainer 'shell'
        }
    }

    stages {
        stage('GetChangesByCurls') {
            environment {
                JENKINS_TOKEN = credentials("jenkins-token")
            }
            steps {
                echo "Hello world"
                sh "curl -L -u ${JENKINS_TOKEN} -o changelog.xml ${BUILD_URL}/api/xml?wrapper=changes&xpath=//changeSet//comment"
                sh "cat changelog.xml"
                //better to use xq , however, here with sed
                sh "sed -n '/<changeSet/,/<\\/changeSet>/p' changelog.xml"
                sh "env|sort"
            }
        }
        stage('GetChangesByGroovy') {
            steps {
                //see https://devops.stackexchange.com/questions/2310/get-all-change-logs-of-since-last-successful-build-in-jenkins-pipeline
                //Should be moved to shared Library
                script {
                    def changeLogSets = currentBuild.changeSets
                    // Check if changeSets is not null and contains any entries
                    if (changeLogSets) {
                        // Iterate through each change set
                        changeSets.each { changeSet ->
                            // Accessing change set details
                            def commitMessages = changeSet.msg
                            def affectedFiles = changeSet.items.collect { it.path }

                            // Outputting change set details
                            println "Commit Messages:"
                            commitMessages.each { println "- $it" }

                            println "Affected Files:"
                            affectedFiles.each { println "- $it" }
                        }
                    } else {
                        println "No change sets found for this build."
                    }
                }
            }
        }
    }
}
