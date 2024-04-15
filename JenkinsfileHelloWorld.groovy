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
                    image: caternberg/ci-utils:1.2
                    command:
                    - sleep
                    args:
                    - infinity
                  - name: xmllint
                    image: pipelinecomponents/xmllint 
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
                echo "#######################################################"
                echo "print commit messages"
                echo "see https://stackoverflow.com/questions/11823826/get-access-to-build-changelog-in-jenkins"
                sh "curl -L -u ${JENKINS_TOKEN} -o changelog.xml ${BUILD_URL}/api/xml?wrapper=changes&xpath=//changeSet//comment"
                sh "cat changelog.xml"
                //This requires script approval!! It also requires Groovy what we want to avoid
                //If you use it,move the script code to Shared Library
               /* script {
                    def myXml = sh returnStdout: true, script: "cat changelog.xml"
                    def workflowRun = new XmlParser().parseText(myXml)
                    def xml = new XmlSlurper().parseText(myXml)
                    def items = xml.changeSet.item.collect { item ->
                        //println item
                        def comment = item.comment.text()
                        def author = item.author.fullName.text()
                        def email = item.authorEmail.text()
                        [name: author, comment: comment, email: email]
                    }

                    items.each { println it }
                }*/



                /* So better to use shell tools, which doesn`t require approvals
                    Here are some options to grep the sub-content like comments
                    xmllint might require xmllint tool installation on agent
                    Other options are: sed or xq
                */
                   // container ("xmllint"){
                        sh 'xmllint --xpath "//changeSet" changelog.xml'
                        sh 'xmllint --xpath "//changeSet/item/comment" changelog.xml'
                   // }
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
