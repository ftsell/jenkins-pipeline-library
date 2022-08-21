/**
 * Update the deployed image in the orchestration repository.
 *
 * This works by bumping the image digest in the relevant kustomize config.
 * ArgoCd then does the actual deploying.
 *
 * @param k8sBaseDir The directory inside the orchestration repositories k8s/base.
 *  The kustomize config inside k8s/base/${k8sBaseDir} is the one being updated.
 * @param imageName Name of the image that is supposed to be updated
 * @param imageDigest New digest which the given image should be
 * @param giteaCredentialsId Id of jenkins credentials which grant access to gitea (where the orchestration repo is kept)
 */
void call(String k8sBaseDir, String imageName, String imageDigest, String giteaCredentialsId = "jenkins-gitea-credentials") {
    milestone(ordinal: 200)
    lock(resource: "orchestrationRepository", inversePrecedence: true) {
        milestone(ordinal: 210)

        // checkout orchestration repository
        container("jnlp") {

            //checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'orchestration']], userRemoteConfigs: [[credentialsId: 'jenkins-gitea-credentials', url: 'https://git.finn-thorben.me/sharedsrv/orchestration.git']]]

            checkout changelog: false,
                    poll: false,
                    scm: [$class           : 'GitSCM',
                          branches         : [[name: "*/main"]],
                          extensions       : [
                                  [$class: 'RelativeTargetDirectory', relativeTargetDir: 'orchestration'],
                                  [$class: 'LocalBranch', localBranch: 'main']
                          ],
                          userRemoteConfigs: [[credentialsId: giteaCredentialsId, url: 'https://git.finn-thorben.me/sharedsrv/orchestration.git']]]
        }

        // bump deployed image digest
        dir("orchestration/k8s/base/${k8sBaseDir}") {
            sh "kustomize edit set image ${imageName}@${imageDigest}"
        }

        // commit update to git
        dir("orchestration") {
            withCredentials([usernamePassword(
                    credentialsId: giteaCredentialsId,
                    usernameVariable: "GIT_USERNAME",
                    passwordVariable: "GIT_PASSWORD"
            )]) {
                sh "git config --global --add safe.directory \$(pwd)"
                sh "git config --local user.email admin+jenkins@finn-thorben.me"
                sh "git config --local user.name jenkins"
                sh "git config --local credential.helper '!p() { echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD; }; p'"
                sh "git commit -a -m 'update ${k8sBaseDir} ${imageName} image version\n\nnew digest is ${imageDigest}'"
                sh "git push origin main"
            }
        }
    }
}