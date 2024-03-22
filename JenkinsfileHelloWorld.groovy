pipeline{
    agent none
    stages {
        stage('Main') {
            steps {
                sh "ls -l"
            }
        }
    }
}

pipeline {
    agent none
    parameters {
        extendedChoice multiSelectDelimiter: ',', name: 'myparam', propertyFile: 'extended_choice_params.properties', propertyKey: 'MYPROPKEY', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_SINGLE_SELECT', visibleItemCount: 5
    }

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                echo "test1"
            }
        }
    }
}