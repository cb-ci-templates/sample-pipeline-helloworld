def call(){
    if ( ${env.globalReturnCode} != "0" ){
        sh "exit \${env.globalReturnCode}"
    }
}