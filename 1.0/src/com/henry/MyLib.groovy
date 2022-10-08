def getTime() {
  return new Date().format('yyyy-MM-ddHHmmss')
}
def getJenkinsHome() {
  return "${JENKINS_HOME}"
}
