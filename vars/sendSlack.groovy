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

def call(String buildResult) {
  if ( buildResult == "STARTED" ) {
    slackSend color: "#80D2DE", message: "Started Job: ${env.JOB_NAME} - ${env.BUILD_NUMBER} wiht below changes\n" + getChangeString() + "\nBy:" + entry.author
  }
  else if ( buildResult == "SUCCESS" ) {
    slackSend color: "good", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful"
  }
  else if( buildResult == "FAILURE" ) {
    slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was failed"
  }
  else if( buildResult == "UNSTABLE" ) {
    slackSend color: "warning", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was unstable"
  }
  else {
    slackSend color: "danger", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} its resulat was unclear"
  }
}
