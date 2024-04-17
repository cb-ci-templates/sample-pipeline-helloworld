library identifier: 'ci-shared-library@main', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/cb-ci-templates/ci-shared-library.git'])
def newSemanticVersionScript=""
pipeline {
    agent {
        kubernetes {
            yaml '''
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                  - name: shell
                    image: caternberg/ci-utils:1.0
                    command:
                    - sleep
                    args:
                    - infinity
                '''
            defaultContainer 'shell'
        }
    }

    stages {
        stage('customStep') {
            steps {
                script {
                    newSemanticVersionScript = libraryResource 'scripts/newSemanticVersion.sh'
                }
                sh "echo ${newSemanticVersionScript} > test.sh && cat test.sh"
                echo "#######################################################"
                newSemanticVersion (arg:"-M",version:"1.2.3")
                echo "${env.NEW_SEMANTIC_VERSION}"
            }
        }
    }
}
