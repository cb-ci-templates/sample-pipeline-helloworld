//https://devops.stackexchange.com/questions/885/cleanest-way-to-prematurely-exit-a-jenkins-pipeline-job-as-a-success
//https://stackoverflow.com/questions/36852310/show-a-jenkins-pipeline-stage-as-failed-without-failing-the-whole-job
def globalReturnCode = 0
def exitOrContinue(){
    if (this.globalReturnCode != 0){
        sh "exit ${this.globalReturnCode}"
    }
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
        stage('runScript') {
            steps {
                catchError(catchInterruptions: false,message: 'Pipeline Stage 1 failed with returnCode ${this.globalReturnCode}', buildResult:'FAILURE', stageResult: 'FAILURE') {
                    //Instead of using "script" step in a pipeline, we should better move it to a custom step in a shared Library.
                    //We use the script step here directly just for demo purpose
                    script {
                        //We should better externalize the script. For demo purpose we make the script here inline:
                        this.globalReturnCode=sh label: 'exitStatus',
                                returnStatus: true,
                                script: """
                                    #! /bin/bash                        
                                    set -e # option instructs bash to immediately exit if any command [1] has a non-zero exit status
                                    set -u # Affects variables. When set, a reference to any variable you haven't previously defined - with the exceptions of `\\\$*` and `\\\$@` - is an error, and causes the program to immediately exit.
                                    #set -o pipefail # This setting prevents errors in a pipeline from being masked. If any command in a pipeline fails, that return code will be used as the return code of the whole pipeline.
                                    set -x # Enables a mode of the shell where all executed commands are printed to the terminal.
                                    RESULT_JSON=mytest.json
                                    #echo \$NonExistingVarCauseError
                                    greetings="World"
                                    echo "{"hello": "\$greetings"}" > \${WORKSPACE}/\${RESULT_JSON}
                                    exit 0
                                    """
                        exitOrContinue ()
                        env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat \${WORKSPACE}/mytest.json"
                    }
                }
                sh "echo ${env.MYJSON}"
                exitOrContinue ()
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
