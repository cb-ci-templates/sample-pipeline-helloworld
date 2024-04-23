
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
        if ( this.returnCode != "0" ){
            sh "exit ${this.returnCode}"
        }
        env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat \${WORKSPACE}/mytest.json"
    }
    sh "echo ${env.MYJSON}"
    if ( this.returnCode != "0" ){
        sh "exit ${this.returnCode}"
    }
}

def exitOrContinue(returnCode ){
    echo "call exitOrContinue  returnCode:${this.returnCode}"
    if ( this.returnCode != "0" ){
        sh "exit ${this.returnCode}"
    }
}


