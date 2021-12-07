/**
 * Check kubernetes config validity by calling kustomize build and then kubeval on the generated manifests
 *
 * @param gitStatusWrapperCredentials The id for jenkins credentials that grant access to the current GitHub
 *  repository in order to set commit and PR status.
 *  If null, no status is set on GitHub.
 */
void call(String gitStatusWrapperCredentials = null) {
    if (gitStatusWrapperCredentials == null) {
        doCheck()
    } else {
        gitStatusWrapper(
                credentialsId: gitStatusWrapperCredentials,
                description: "Check k8s",
                failureDescription: "k8s config is invalid",
                successDescription: "k8s config is valid",
                gitHubContext: "check-k8s"
        ) {
            doCheck()
        }
    }
}

void doCheck() {
    sh "kustomize build . > /tmp/k8s.yml"
    sh "kubeval /tmp/k8s.yml --strict"
}
