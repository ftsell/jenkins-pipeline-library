/**
 * Generate kubernetes pod yaml with the specified containers
 *
 * @param kustomizeKubeval Whether docker.io/nekottyo/kustomize-kubeval should be included or not
 * @param podman Whether quay.io/podman/stable should be included or not
 */
String call(boolean kustomizeKubeval, boolean podman) {
    String result = """
kind: Pod
spec:
  containers:
"""
    if (kustomizeKubeval) {
        result += """
    - name: kustomize
      image: docker.io/nekottyo/kustomize-kubeval
      tty: true
      command:
        - cat
"""
    }

    if (podman) {
        result += """
    - name: podman
      image: quay.io/podman/stable
      tty: true
      securityContext:
        privileged: true
      command:
        - cat
"""
    }

    return result
}
