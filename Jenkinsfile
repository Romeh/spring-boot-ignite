pipeline {
    // run on jenkins nodes tha has java 8
    agent { label 'jdk8' }
    // global env variables
    environment {
        EMAIL_RECIPIENTS = 'mahmoud.romih@test.com'
    }
    stages {
        stage('Build With Unit Testing') {
            steps {
                // Run the maven build
                script {
                    // Get the Maven tool.
                    // ** NOTE: This 'M3' Maven tool must be configured
                    // **       in the global configuration.
                    echo 'Pulling...' + env.BRANCH_NAME
                    def mvnHome = tool 'Maven 3.3.9'
                    if (isUnix()) {
                        sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
                        def pom = readMavenPom file: 'pom.xml'
                        print pom.version
                        junit '**/target/surefire-reports/TEST-*.xml'
                        archive 'target/*.jar'
                    } else {
                        bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
                        def pom = readMavenPom file: 'pom.xml'
                        print pom.version
                        junit '**/target/surefire-reports/TEST-*.xml'
                        archive 'target/*.jar'
                    }
                }

            }
        }
        stage('Integration Tests') {
            // Run the maven build
            steps {
                script {
                    def mvnHome = tool 'Maven 3.3.9'
                    if (isUnix()) {
                        sh "'${mvnHome}/bin/mvn'  verify -Dunit-tests.skip=true"
                    } else {
                        bat(/"${mvnHome}\bin\mvn" verify -Dunit-tests.skip=true/)
                    }

                }
            }
        }
        stage('Sonar Check') {
            // Run the maven build
            steps {
                script {
                    def mvnHome = tool 'Maven 3.3.9'
                    // replace it with your sonar server
                    sh "'${mvnHome}/bin/mvn'  verify sonar:sonar -Dsonar.host.url=http://romehjava.bc/sonar/ -Dmaven.test.failure.ignore=true"

                }
            }
        }

        stage('ITT Deploy Approval and deployment') {
            when {
                // check if the build was successful
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' && env.BRANCH_NAME == 'master' }
            }
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    //input message:'Approve deployment?', submitter: 'it-ops'
                    input message: 'Approve deployment?'
                }
                timeout(time: 2, unit: 'MINUTES') {
                    // call another jenkins job which do ssh deployment
                    build job: 'AlertManagerToITT'
                    echo 'the application is deployed !'
                }
            }
        }
    }
    post {
        // Always runs. And it runs before any of the other post conditions.
        always {
            // Let's wipe out the workspace before we finish!
            deleteDir()
        }
        success {
            sendEmail("Successful");
        }
        unstable {
            sendEmail("Unstable");
        }
        failure {
            sendEmail("Failed");
        }
    }

// The options directive is for configuration that applies to the whole job.
    options {
        // For example, we'd like to make sure we only keep 10 builds at a time, so
        // we don't fill up our storage!
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // And we'd really like to be sure that this build doesn't hang forever, so
        // let's time it out after an hour.
        timeout(time: 20, unit: 'MINUTES')
    }

}


@NonCPS
def getChangeString() {
    MAX_MSG_LEN = 100
    def changeString = ""

    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " - No new changes"
    }
    return changeString
}

def sendEmail(status) {
    mail(
            to: "$EMAIL_RECIPIENTS",
            subject: "Build $BUILD_NUMBER - " + status + " (${currentBuild.fullDisplayName})",
            body: "Changes:\n " + getChangeString() + "\n\n Check console output at: $BUILD_URL/console" + "\n")
}