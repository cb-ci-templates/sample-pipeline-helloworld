library identifier: 'ci-shared-library@main', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/cb-ci-templates/sample-pipelines.git'])

//https://devops.stackexchange.com/questions/885/cleanest-way-to-prematurely-exit-a-jenkins-pipeline-job-as-a-success
//https://stackoverflow.com/questions/36852310/show-a-jenkins-pipeline-stage-as-failed-without-failing-the-whole-job
def globalReturnCode = "0"
def mockStepFail(exitCode){
    echo "mockStepFail"
    globalReturnCode="${exitCode}"
    env.globalReturnCode="${exitCode}"
    echo "MOCKRETURNCODE: ${globalReturnCode}"
    sh "exit ${globalReturnCode}"
}
pipeline {
    agent {
        kubernetes {
            yaml '''
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: shell
                image: ubuntu
                command:
                - sleep
                args:
                - infinity
            '''
            defaultContainer 'shell'
        }
    }
    stages {
        stage('1') {
            steps {
                myShellScriptStep (arg:"-M",version:"1.2.3")
            }
        }
        stage('2') {
            when { environment name: 'globalReturnCode', value: "0"  }
            parallel {
                stage("2a") {
                    when { environment name: 'globalReturnCode', value: "0"  }
                    steps {
                        //Set pipeline Stage to "yellow"
                        //warnError(catchInterruptions: true, message: 'warnError with returnCode ${env.globalReturnCode}') {
                        //Set pipeline Stage to "red"
                        catchError(message: "Pipeline Stage 2a failed with returnCode ${env.globalReturnCode}", buildResult:'FAILURE', stageResult: 'FAILURE') {
                            sh "echo  2a"
                            sh "echo GLOBAL_RETURN_CODE: ${globalReturnCode}"
                            mockStepFail(1)
                        }
                    }
                }
                stage("2b") {
                    when { environment name: 'globalReturnCode', value: "0"  }
                    steps {
                        sh "echo 2b"
                    }
                }
            }
        }
        stage('3') {
            when { environment name: 'globalReturnCode', value: "0"  }
            steps {
                sh "echo ${env.globalReturnCode}"
                sh 'hostname'
            }
        }
    }
    post {
        failure {
            echo "failure with returnCode ${env.globalReturnCode}"
        }
        success {
            echo "success with returnCode ${env.globalReturnCode}"
        }
    }
}
