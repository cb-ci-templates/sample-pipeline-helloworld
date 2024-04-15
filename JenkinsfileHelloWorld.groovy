def changelogs=""
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
        stage('GetChangesByGitLog') {
            steps {
                echo "#######################################################"
                echo "print commit messages"
                sh "git config --global --add safe.directory ${WORKSPACE}"
                //Move better to shared Library
                script{
                    changelogs=sh returnStdout: true, script: "git log  ${GIT_PREVIOUS_COMMIT}..${GIT_COMMIT}"
                }
                echo"${changelogs}"
            }
        }
        stage('GetChangesByCurls') {
            environment {
                JENKINS_TOKEN = credentials("jenkins-token")
            }
            steps {
                sh "env |sort "
                echo "#######################################################"
                echo "print commit messages"
                echo "see https://stackoverflow.com/questions/11823826/get-access-to-build-changelog-in-jenkins"
                sh "curl -L -u ${JENKINS_TOKEN} -o changelog.xml ${BUILD_URL}/api/xml?wrapper=changes&xpath=//changeSet//comment"
                script {
                   def workflowRun = new XmlParser().parse("changelog.xml")
                   println workflowRun.workflowRun.changeSet.text()
                }
                sh "cat changelog.xml"


                /*
                    Here are some options to grep the sub-content like comments
                    xmllint might require xmllint tool installation on agent
                    Other options are: sed or xq
                    sh 'xmllint --xpath "//changeSet" changelog.xml'
                    sh "xmllint --xpath "//changeSet/item/comment" changelog.xml"
                */

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
                        println "No changeSet found for this build."
                    }
                }
            }
        }
    }
}
