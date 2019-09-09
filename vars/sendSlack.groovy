#!/usr/bin/env groovy

def call(String buildResult) {
  if ( buildResult == "SUCCESS" ) {

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

      slackSend color: "good", message: "Changes:\n " + getChangeString() + "\n By: ${entry.author}"
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
