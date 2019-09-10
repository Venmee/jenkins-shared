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


  def getAbortUser()
  {
      def causee = ''
      def actions = currentBuild.getRawBuild().getActions(jenkins.model.InterruptedBuildAction)
      for (action in actions) {
          def causes = action.getCauses()

          // on cancellation, report who cancelled the build
          for (cause in causes) {
              causee = cause.getUser().getDisplayName()
              cause = null
          }
          causes = null
          action = null
      }
      actions = null

      return causee
  }

def call(String buildResult) {
  if ( buildResult == "STARTED" ) {

    def SCMTriggerCause
    def UserIdCause
    def GitHubPRCause
    def PRCause = currentBuild.rawBuild.getCause(org.jenkinsci.plugins.github.pullrequest.GitHubPRCause)
    def SCMCause = currentBuild.rawBuild.getCause(hudson.triggers.SCMTrigger$SCMTriggerCause)
    def UserCause = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)

    if (PRCause) {
        slackSend color: "good", message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Started by " + PRCause.getShortDescription() + "\nChanges:\n" + "\t" + getChangeString()
    } else if (SCMCause) {
        slackSend color: "good", message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Started by " + author() + "\nChanges:\n" + "\t" + getChangeString()
    } else if (UserCause) {
        slackSend color: "good", message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Started by " + agetBuildUser() + "\nChanges:\n" + "\t" + getChangeString()
    }else {
       println "unknown cause"
    }
    // slackSend color: "good", message: "${env.JOB_NAME} - Build:<${env.BUILD_URL}|#${env.BUILD_NUMBER}> Started by " + getBuildUser() + "\nChanges:\n" + "\t" + getChangeString()
  }

  //DEV Stage notification
  else if ( buildResult == "DEV_STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|DEV Deployment> Started"
  }
  else if ( buildResult == "DEV_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|DEV Deployment> Success"
  }
  else if ( buildResult == "DEV_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|DEV Deployment> Failed"
  }

  //INT Stage notification
  else if ( buildResult == "INT_STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|INT Deployment> Started"
  }
  else if ( buildResult == "INT_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|INT Deployment> Success"
  }
  else if ( buildResult == "INT_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|INT Deployment> Failed"
  }

  //UAT Stage notification
  else if ( buildResult == "UAT_STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|UAT Deployment> Started"
  }
  else if ( buildResult == "UAT_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|UAT Deployment> Success"
  }
  else if ( buildResult == "UAT_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|UAT Deployment> Failed"
  }

  //PRD Stage notification
  else if ( buildResult == "PRD_STARTED" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|PRD Deployment> Started"
  }
  else if ( buildResult == "PRD_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|PRD Deployment> Success"
  }
  else if ( buildResult == "PRD_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|PRD Deployment> Failed"
  }

  //PRD Stage notification
  else if ( buildResult == "E2E_SUCCESS" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|E2E Tesing> Success"
  }
  else if ( buildResult == "E2E_FAILURE" ) {
    slackSend color: "good", message: "${env.JOB_NAME} - Stage: <${env.RUN_DISPLAY_URL}|E2E Tesing> Failed"
  }

  else if ( buildResult == "SUCCESS" ) {
    slackSend color: 'good', message: "${env.JOB_NAME} - Build: <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Success"
  }
  else if( buildResult == "FAILURE" ) {
    slackSend color: 'danger', message: "${env.JOB_NAME} - Build: <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Failure"
  }
  else if( buildResult == "ABORTED" ) {
    slackSend color: 'warning', message: "${env.JOB_NAME} - Build: <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Aborted by " + getAbortUser()
  }
  else if( buildResult == "UNSTABLE" ) {
    slackSend color: 'warning', message: "${env.JOB_NAME} - Build: <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Unstable"
  }
  else {
    slackSend color: 'danger', message: "${env.JOB_NAME} - Build: <${env.BUILD_URL}|#${env.BUILD_NUMBER}> Unknown"
  }
}
