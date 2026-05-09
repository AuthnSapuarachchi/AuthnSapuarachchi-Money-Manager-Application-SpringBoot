pipeline {
    agent any
    
    environment {
        DOCKER_HUB_REPO = 'authnbroo' 
        BACKEND_IMAGE = "${DOCKER_HUB_REPO}/smart-money-backend"
        DOCKER_CREDENTIALS = 'dockerhub-credentials'
    }
    
    tools {
        maven 'Maven-3.9'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Checking out Spring Boot Backend...'
                checkout scm
            }
        }
        
        stage('Test Backend') {
            steps {
                echo '🧪 Running Maven Tests...'
                sh 'mvn clean test'
            }
        }

        stage('Build JAR') {
            steps {
                echo '📦 Packaging Spring Boot Application...'
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🏗️ Building Backend Docker Image...'
                script {
                    def backendImage = docker.build("${BACKEND_IMAGE}:${BUILD_NUMBER}")
                    backendImage.tag('latest')
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo '☁️ Pushing to Docker Hub...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_CREDENTIALS) {
                        docker.image("${BACKEND_IMAGE}:${BUILD_NUMBER}").push()
                        docker.image("${BACKEND_IMAGE}:latest").push()
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up dangling images...'
            sh 'docker image prune -f || true'
        }
    }
}