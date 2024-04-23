
def call(Map params){
    def returnCode= 0
    catchError(catchInterruptions: false,message: "Pipeline Stage failed", buildResult:'FAILURE', stageResult: 'FAILURE') {
        writeFile encoding: 'utf-8', file: "${WORKSPACE}/sampleFailFast.sh", text: libraryResource ('scripts/sampleFailFast.sh')
        returnCode=sh label: 'exitStatus',
                returnStatus: true,
                script: """chmod a+x \${WORKSPACE}/sampleFailFast.sh && \
                            \${WORKSPACE}/sampleFailFast.sh ${params.arg} ${params.version}
                         """
        env.globalReturnCode="${returnCode}"
        echo "RETURNCODE: ${returnCode} "
        if ( returnCode != 0 ){
            echo "EXIT 1"
            sh "exit 1"
        }else {
            env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat \${WORKSPACE}/mytest.json"
            sh "echo ${env.MYJSON}"
        }
    }
}



