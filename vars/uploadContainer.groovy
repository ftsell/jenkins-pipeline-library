/**
 * Upload the container with the given name
 *
 * @param imageName The current name of the image that is supposed to be uploaded
 * @param registryBase Base URL of the container registry (used in podman login command)
 * @param registryCredentials The id for jenkins credentials that are used for container registry access.
 * @param gitStatusWrapperCredentials The id for jenkins credentials that grant access to the current GitHub
 *  repository in order to set commit and PR status.
 *  If null, no status is set on GitHub.
 */
void call(String imageName, String registryBase, String registryCredentials = null, String gitStatusWrapperCredentials = null) {
    if (gitStatusWrapperCredentials == null) {
        doUpload(imageName, registryBase, registryCredentials)
    } else {
        gitStatusWrapper(
                credentialsId: gitStatusWrapperCredentials,
                description: "Uploads the container image",
                failureDescription: "Could not upload the container image",
                successDescription: "Container image was uploaded",
                gitHubContext: "upload-container-image"
        ) {
            doUpload(imageName, registryBase, registryCredentials)
        }
    }
}


private void doUpload(String imageName, String registryBase, String registryCredentials) {
    milestone(ordinal: 100)

    script {
        withCredentials([usernamePassword(
                credentialsId: registryCredentials,
                passwordVariable: 'registry_password',
                usernameVariable: 'registry_username'
        )]) {
            sh "podman login ${registryBase} -u ${registry_username} -p ${registry_password}"
            sh "podman push ${imageName}"
        }
    }
}
