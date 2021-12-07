/**
 * Build a container image with podman.
 *
 * @param imageName The name which the built image will have
 * @param gitStatusWrapperCredentials The id for jenkins credentials that grant access to the current GitHub
 *  repository in order to set commit and PR status.
 *  If null, no status is set on GitHub.
 */
void call(String imageName, String gitStatusWrapperCredentials = null) {
    if (gitStatusWrapperCredentials == null) {
        sh "podman build -t ${imageName} ."
    } else {
        gitStatusWrapper(
                credentialsId: gitStatusWrapperCredentials,
                description: "Builds the container image",
                failureDescription: "Container image failed to build",
                successDescription: "Container image was successfully built",
                gitHubContext: "build-container-image"
        ) {
            sh "podman build -t ${imageName} ."
        }
    }
}
