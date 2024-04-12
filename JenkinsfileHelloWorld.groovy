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
                echo "#######################################################"
                echo "print commit messages"
                echo "see https://stackoverflow.com/questions/11823826/get-access-to-build-changelog-in-jenkins"
                sh "curl -L -u ${JENKINS_TOKEN} -o changelog.xml ${BUILD_URL}/api/xml?wrapper=changes&xpath=//changeSet//comment"
                echo "#####NOT FILTERED#####\n"
                sh "cat changelog.xml"
                //Here are some options to grep the subcontent
                //sh 'xmllint --xpath "//changeSet" changelog.xml'
                //sh "xmllint --xpath "//changeSet/item/comment" changelog.xml"
            }
        }
        stage('GetChangesByGroovy') {
            steps {
                echo "#######################################################"
                echo "print commit messages"
                echo "see https://devops.stackexchange.com/questions/2310/get-all-change-logs-of-since-last-successful-build-in-jenkins-pipeline"
                //Should be moved to shared Library
                script {
                    def changeLogSets = currentBuild.changeSets
                    def log = ""
                    // Check if changeSets is not null and contains any entries
                    if (changeLogSets) {
                        // Iterate through each change set
                        for (int i = 0; i < changeLogSets.size(); i++) {
                            def entries = changeLogSets[i].items
                            for (int j = 0; j < entries.length; j++) {
                                def entry = entries[j]
                                log += "* ${entry.msg} by ${entry.author} \n"
                            }
                        }
                        println log
                    } else {
                        println "No change sets found for this build."
                    }
                }
            }
        }
    }
}
