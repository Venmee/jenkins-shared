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
    slackSend color: "#80D2DE", message: "Started Job: ${env.JOB_NAME} >> ${env.BUILD_NUMBER} >> " + getBuildUser() + "\n" getChangeString() + "\nby: " author()
  }
  else if ( buildResult == "SUCCESS" ) {
    slackSend color: "good", message: "Successful Job: ${env.JOB_NAME} >> ${env.BUILD_NUMBER}"
  }
  else if( buildResult == "FAILURE" ) {
    slackSend color: "danger", message: "Failure Job: ${env.JOB_NAME} >> ${env.BUILD_NUMBER}"
  }
  else if( buildResult == "UNSTABLE" ) {
    slackSend color: "warning", message: "Unstable Job: ${env.JOB_NAME} >> ${env.BUILD_NUMBER}"
  }
  else {
    slackSend color: "danger", message: "Unknown Job: ${env.JOB_NAME} >> ${env.BUILD_NUMBER}"
  }
}
