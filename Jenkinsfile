pipeline {
    agent any

    environment {
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Clone repository') {
            steps {
                checkout scm
            }
        }

        stage('Build Project') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('MVN SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonarToken', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN -Dsonar.java.binaries=target/classes'
                }
            }
        }

        stage('Mockito Tests') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('JaCoCo Report') {
            steps {
                jacoco execPattern: 'target/jacoco.exec',
                       classPattern: 'target/classes',
                       sourcePattern: 'src/main/java',
                       exclusionPattern: '**/generated/**'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexusCredentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh 'mvn deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::http://localhost:8081/repository/maven-releases/ -Dusername=$NEXUS_USERNAME -Dpassword=$NEXUS_PASSWORD'
                }
            }
        }

        stage('Build image') {
            steps {
                script {
                    def app = docker.build("medlas/foyer:${env.DOCKER_TAG}")
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        def app = docker.image("medlas/foyer:${env.DOCKER_TAG}")
                        app.push("${env.DOCKER_TAG}")
                    }
                }
            }
        }

        stage('Docker Compose') {
            steps {
                script {
                    sh "DOCKER_TAG=${env.DOCKER_TAG} docker compose up -d"
                }
            }
        }

        stage('Verify Docker Compose') {
            steps {
                sh 'docker ps -a'
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                script {
                    def deploymentYaml = readFile('k8s-deployment.yaml')
                    def updatedYaml = deploymentYaml.replace('${DOCKER_TAG}', env.DOCKER_TAG)
                    writeFile file: 'updated-k8s-deployment.yaml', text: updatedYaml
                    sh "microk8s kubectl apply -f updated-k8s-deployment.yaml"
                }
            }
        }


        stage('Verify Kubernetes Deployment') {
            steps {
                script {
                    sh 'microk8s kubectl get pods'
                }
            }
        }
    }
}
