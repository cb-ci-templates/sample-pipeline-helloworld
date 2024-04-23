def call(Map params){
    writeFile encoding: 'utf-8', file: "${WORKSPACE}/sampleFailFast.sh", text: libraryResource ('scripts/sampleFailFast.sh')
    catchError(catchInterruptions: false,message: "Pipeline Stage 1 failed with returnCode ${env.globalReturnCode}", buildResult:'FAILURE', stageResult: 'FAILURE') {
        //Instead of using "script" step in a pipeline, we should better move it to a custom step in a shared Library.
        //We should better externalize the script. For demo purpose we make the script here inline:
        env.globalReturnCode=sh label: 'exitStatus',
                returnStatus: true,
                script: """chmod a+x ${WORKSPACE}/nsv.sh && \
                            ${WORKSPACE}/sampleFailFast.sh
                         """
        exitOrContinue ()
        env.MYJSON=sh label: 'stdOut', returnStdout: true, script: "cat ${WORKSPACE}/mytest.json"

    }
    sh "echo ${env.MYJSON}"
    exitOrContinue ()
}


