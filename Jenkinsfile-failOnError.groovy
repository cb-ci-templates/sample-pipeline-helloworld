//https://devops.stackexchange.com/questions/885/cleanest-way-to-prematurely-exit-a-jenkins-pipeline-job-as-a-success
//https://stackoverflow.com/questions/36852310/show-a-jenkins-pipeline-stage-as-failed-without-failing-the-whole-job
def globalReturnCode = "0"
def mockStepFail(exitCode){
    echo "mockStepFail"
    this.globalReturnCode=exitCode
    echo "MOCKRETURNCODE: ${this.globalReturnCode}"
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
                catchError(catchInterruptions: false,message: 'Pipeline Stage 1 failed with returnCode ${globalReturnCode}', buildResult:'FAILURE', stageResult: 'FAILURE') {
                    mockStepFail("0")
                    sh "exit ${this.globalReturnCode}"
                }
            }
        }
        stage('2') {
            when { equals expected: "0", actual: this.globalReturnCode }
            parallel {
                stage("2a") {
                    when { equals expected: "0", actual: this.globalReturnCode }
                    steps {
                        //Set pipeline Stage to "yellow"
                        //warnError(catchInterruptions: true, message: 'warnError with returnCode ${globalReturnCode}') {
                        //Set pipeline Stage to "red"
                        catchError(message: 'Pipeline Stage 2a failed with returnCode ${globalReturnCode}', buildResult:'FAILURE', stageResult: 'FAILURE') {
                            sh "echo  2a"
                            sh "echo GLOBAL_RETURN_CODE: ${this.globalReturnCode}"
                            mockStepFail("1")
                            sh "exit ${this.globalReturnCode}"
                        }
                    }
                    /* post{
                         failure{
                             echo "stage 2a failed with exitCode ${globalReturnCode}"
                             //error "stage 2a failed with exitCode ${globalReturnCode}"
                             sh "exit ${globalReturnCode}"
                         }
                         unstable {
                             echo "stage 2a unstable with exitCode ${globalReturnCode}"
                             //unstable 'unstable'
                             sh "exit ${globalReturnCode}"
                         }
                     }
                     */

                }
                stage("2b") {
                    when { equals expected: "0", actual: this.globalReturnCode }
                    steps {
                        sh "echo 2b"
                    }
                }
            }
        }
        stage('3') {
            when { equals expected: "0", actual: this.globalReturnCode }
            steps {
                sh "echo ${this.globalReturnCode}"
                sh 'hostname'
            }
        }
    }
    post {
        failure {
            echo "failure with returnCode ${this.globalReturnCode}"
        }
        success {
            echo "success with returnCode ${this.globalReturnCode}"
        }
    }
}
