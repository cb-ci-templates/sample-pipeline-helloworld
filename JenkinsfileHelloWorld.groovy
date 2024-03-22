node {
                sh "curl -O https://raw.githubusercontent.com/pipeline-demo-caternberg/pipeline-helloworld/main/extended_choice_params.properties"
                sh "pwd && ls -l"
                sh "cat extended_choice_params.properties"
}

pipeline {
    agent none
    parameters {
        extendedChoice multiSelectDelimiter: ',', name: 'myparam', propertyFile: '/var/jenkins_home/workspace/ParamHelloWorld/extended_choice_params.properties', propertyKey: 'mychoice_values', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_SINGLE_SELECT', visibleItemCount: 5
    }

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                echo "${myparam}"
            }
        }
    }
}