pipeline {
    agent none
    parameters {
        extendedChoice multiSelectDelimiter: ',', name: 'myparam', propertyFile: 'extended_choice_params.properties', propertyKey: 'MYPROPKEY', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_SINGLE_SELECT', visibleItemCount: 5
    }

    stages {
        stage('Main') {
            steps {
                echo "Hello world"
                sleep 10
                echo "test1"
            }
        }
    }
}