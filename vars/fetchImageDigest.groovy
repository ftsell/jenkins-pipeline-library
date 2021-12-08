/**
 * Retrieve the digest of the specified image from the container registry.
 *
 * @param imageName The name of the image that is supposed to be inspected
 * @param registryBase Base URL of the container registry (used in podman login command)
 * @param registryCredentials The id for jenkins credentials that are used for container registry access
 * @return The digest of the image
 */
String call(String imageName, String registryBase, String registryCredentials) {
    // login to registry
    withCredentials([usernamePassword(
            credentialsId: registryCredentials,
            passwordVariable: 'registry_password',
            usernameVariable: 'registry_username'
    )]) {
        sh "podman login ${registryBase} -u ${registry_username} -p ${registry_password}"
    }

    // remove locally stored image because the digest might have changed
    sh "podman rmi ${imageName}"

    // fetch image
    sh "podman pull ${imageName}"

    script {
        return sh(returnStdout: true, script: "podman images --format='{{.Digest}}' ${imageName}")
    }
}
