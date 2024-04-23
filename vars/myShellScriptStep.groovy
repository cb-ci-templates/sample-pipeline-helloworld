def returnCode="0"
def call(Map params){
    catchError(catchInterruptions: false,message: "Pipeline Stage failed with returnCode ${returnCode}", buildResult:'FAILURE', stageResult: 'FAILURE') {
        writeFile encoding: 'utf-8', file: "${WORKSPACE}/sampleFailFast.sh", text: libraryResource ('scripts/sampleFailFast.sh')
        env.globalReturnCode=sh label: 'exitStatus',
                returnStatus: true,
                script: """chmod a+x \${WORKSPACE}/sampleFailFast.sh && \
                            \${WORKSPACE}/sampleFailFast.sh
                         """
        env.globalReturnCode=returnCode
        this.exitOrContinue()
        env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat \${WORKSPACE}/mytest.json"
    }
    sh "echo ${env.MYJSON}"
    this.exitOrContinue()
}

def exitOrContinue(){
    echo "call exitOrContinue  globalReturnCode:${returnCode}"
    if ( returnCode != "0" ){
        sh "exit ${returnCode}"
    }
}


