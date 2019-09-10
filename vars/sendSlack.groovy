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
    slackSend color: "good", message: "${env.JOB_NAME} » #${env.BUILD_NUMBER} Started (<${env.BUILD_URL}|Open>) by " + getBuildUser() + "\nChanges:\n" + getChangeString()
  }
  else if ( buildResult == "SUCCESS" ) {
    slackSend color: 'good', message: "${env.JOB_NAME} - <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Success (<${env.BUILD_URL}|Open>)"
  }
  else if( buildResult == "FAILURE" ) {
    slackSend color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)"
  }
  else if( buildResult == "UNSTABLE" ) {
    slackSend color: 'warning', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Unstable (<${env.BUILD_URL}|Open>)"
  }
  else {
    slackSend color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Unknown (<${env.BUILD_URL}|Open>)"
  }
}
