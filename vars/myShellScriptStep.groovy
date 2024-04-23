
def call(Map params){
    def returnCode="0"
    catchError(catchInterruptions: false,message: "Pipeline Stage failed with returnCode ${returnCode}", buildResult:'FAILURE', stageResult: 'FAILURE') {
        writeFile encoding: 'utf-8', file: "${WORKSPACE}/sampleFailFast.sh", text: libraryResource ('scripts/sampleFailFast.sh')
        returnCode=sh label: 'exitStatus',
                returnStatus: true,
                script: """chmod a+x \${WORKSPACE}/sampleFailFast.sh && \
                            \${WORKSPACE}/sampleFailFast.sh
                         """
        env.globalReturnCode="${returnCode}"
        if ( returnCode != "0" ){
            echo "EXIT 1"
            sh "exit 1"
        }
    }
    env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat \${WORKSPACE}/mytest.json"
    sh "echo ${env.MYJSON}"
    if ( returnCode != "0" ){
        echo "EXIT 2"
        sh "exit 1"
    }
}
/*
def exitOrContinue(returnCode ){
    echo "call exitOrContinue  returnCode:${this.returnCode}"
    if ( this.returnCode != "0" ){
        sh "exit ${this.returnCode}"
    }
}
*/



