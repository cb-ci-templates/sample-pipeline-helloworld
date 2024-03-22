node {
                sh """
                    file="extended_choice_params.properties"
                    if [ -e "\$file" ]; then
                        rm -f "\$file"
                        echo "File '\$file' deleted."
                    fi    
                    curl -O https://raw.githubusercontent.com/pipeline-demo-caternberg/pipeline-helloworld/main/\$file
                    pwd && ls -l
                    cat \$file
                """
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