// Uses Declarative syntax to run commands inside a container.
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: shell
    image: ubuntu
    command:
    - sleep
    args:
    - infinity
'''
            defaultContainer 'shell'
        }
    }
    stages {
        stage('1') {
            steps {
                sh 'hostname'
                sh "exit 0"
            }
            post{
                failure{
                    echo "stage 1 failed"
                }
            }
        }
        stage('2') {
            /*
            We know the quality gates so we can use static parallel structure
             */
            parallel {
                stage("2a") {
                    steps {
                        //catchError(message: 'Pipeline Stage 2a failed', buildResult:'FAILURE', stageResult: 'FAILURE') {
                            sh "echo  2a"
                            sh "exit 1"
                        //}
                    }
                    post{
                        failure{
                            echo "stage 1a failed"
                        }
                    }
                }
                stage("2b") {
                    steps {
                        sh "echo 1b"
                    }
                      post{
                        failure{
                            echo "stage 2a failed"
                        }
                    }
                }
            }
            post {
                failure{
                    echo "fail stage 2"
                }
            }
        }
        stage('3') {
            steps {
                sh 'hostname'
            }
        }

    }
    post {
        failure {
            echo "failure main"
            //sh "exit 1"
        }
    }
}
