#!/usr/bin/env groovy

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

def getBuildUser() {
        return currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
    }

def author() {
  sh(returnStdout: true, script: "git --no-pager show -s --format='%an'").trim()
}

def call(String buildResult) {
  if ( buildResult == "STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Started by " + getBuildUser() + "\nChanges:\n" + "\t" + getChangeString()
  }

  //Dev Stage notification
  else if ( buildResult == "DEV_STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage:<${env.RUN_DISPLAY_URL}|DEV Deployment> Started"
  }
  else if ( buildResult == "DEV_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage:<${env.RUN_DISPLAY_URL}|DEV Deployment> Success"
  }
  else if ( buildResult == "DEV_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage:<${env.RUN_DISPLAY_URL}|DEV Deployment> Failed"
  }

  else if ( buildResult == "SUCCESS" ) {
    slackSend color: 'good', message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Success"
  }
  else if( buildResult == "FAILURE" ) {
    slackSend color: 'danger', message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Failure"
  }
  else if( buildResult == "UNSTABLE" ) {
    slackSend color: 'warning', message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Unstable"
  }
  else {
    slackSend color: 'danger', message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Unknown"
  }
}
