def call(String buildStatus) {
  def status = buildStatus ?: 'SUCCESS'
  def color = '#e3e4e6'
  def statusMessage = status
  MAX_MSG_LEN = 100
  def changeString = ""

  if (status == 'STARTED') {
    color = '#e3e4e6'
    statusMessage = 'Started'
  }
  if (status == 'SUCCESS') {
    color = 'good'
    statusMessage = 'Success'
  }
  if (status == 'FAILURE') {
    color = 'danger'
    statusMessage = 'FAILURE'
  }
  if (status == 'ABORTED') {
    color = 'warning'
    statusMessage = 'Aborted'
  }
  if (status == 'NOT_BUILT') {
    color = 'warning'
    statusMessage = 'Not built'
  }
  if (status == 'UNSTABLE') {
    color = 'danger'
    statusMessage = 'Unstable'
  }

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
  def sendSlack(status) {
    slackSend (color: '#80D2DE', message: "Changes:\n " + getChangeString() + "\n\n")
  }
  // def message = "${env.JOB_NAME} <${env.BUILD_URL}|#${env.BUILD_NUMBER}> ${statusMessage}"
  //   // slackSend (color: '#80D2DE', message: "Changes:\n " + getChangeString() + "\n\n")
  //   slackSend (color: color, message: message + "Changes:\n " + getChangeString() )
}



