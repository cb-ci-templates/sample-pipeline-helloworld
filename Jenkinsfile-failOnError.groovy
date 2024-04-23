//https://devops.stackexchange.com/questions/885/cleanest-way-to-prematurely-exit-a-jenkins-pipeline-job-as-a-success
//https://stackoverflow.com/questions/36852310/show-a-jenkins-pipeline-stage-as-failed-without-failing-the-whole-job
def globalReturnCode = 0
def mockStepFail(exitCode){
    echo "mockStepFail"
    this.globalReturnCode=exitCode
    echo "MOCKRETURNCODE: ${this.globalReturnCode}"
    sh "exit ${this.globalReturnCode}"
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
                    //Instead of using "script" step in a pipeline, we should better move it to a custom step in a shared Library.
                    //We use the script step here directly just for demo purpose
                    script {
                        //We should better externalize the script. For demo purpose we make the script here inline:
                        this.globalReturnCode=sh label: 'exitStatus',
                                returnStatus: true,
                                script: """
                                        #! /bin/bash                        
                                        set -e # option instructs bash to immediately exit if any command [1] has a non-zero exit status
                                        set -u # Affects variables. When set, a reference to any variable you haven't previously defined - with the exceptions of `\$*` and `\$@` - is an error, and causes the program to immediately exit.
                                        #set -o pipefail # This setting prevents errors in a pipeline from being masked. If any command in a pipeline fails, that return code will be used as the return code of the whole pipeline.
                                        set -x # Enables a mode of the shell where all executed commands are printed to the terminal.
                                        
                                        greetings="World"
                                        #This will fail fast because of "set -u"
                                        #script exit  with error code !=0
                                        #echo \$notExist
                                        #This will not be executed because script fails before
                                        echo "Hello \$greetings"
                                        exit 0
                                """
                    }
                    sh "exit ${this.globalReturnCode}"
                }
            }
        }
        stage('2') {
            when { equals expected: 0, actual: this.globalReturnCode }
            parallel {
                stage("2a") {
                    when { equals expected: 0, actual: this.globalReturnCode }
                    steps {
                        //Set pipeline Stage to "yellow"
                        //warnError(catchInterruptions: true, message: 'warnError with returnCode ${globalReturnCode}') {
                        //Set pipeline Stage to "red"
                        catchError(message: 'Pipeline Stage 2a failed with returnCode ${this.globalReturnCode}', buildResult:'FAILURE', stageResult: 'FAILURE') {
                            sh "echo  2a"
                            sh "echo GLOBAL_RETURN_CODE: ${this.globalReturnCode}"
                            mockStepFail(1)
                            sh "exit ${this.globalReturnCode}"
                        }
                    }
                }
                stage("2b") {
                    when { equals expected: 0, actual: this.globalReturnCode }
                    steps {
                        sh "echo 2b"
                    }
                }
            }
        }
        stage('3') {
            when { equals expected: 0, actual: this.globalReturnCode }
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
