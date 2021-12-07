# Jenkins Pipeline Library
> Personal utility library for writing jenkins pipelines 

This is a small project aimed to ease the creation of Jenkins pipelines by defining a common set of operation (any by common i mean the things I find myself doing often).
This library is intended to be used as a [shared pipeline library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
and not a Jenkins plugin.

**Disclaimer**
Until jenkins issue [JENKINS-67322](https://issues.jenkins.io/browse/JENKINS-67322) is
resolved, GitHub statuses will not work correctly.


## How to Use

The library needs to be loaded in a Jenkinsfile by placing the following line at the top
```groovy
library changelog: false, identifier: 'github.com/ftsell/jenkins-pipeline-library@main', retriever: modernSCM([$class: 'GitSCMSource', credentialsId: '', remote: 'https://github.com/ftsell/jenkins-pipeline-library.git', traits: [gitBranchDiscovery()]])
```

Afterwards, all commands defined in [vars](./vars) can be called.
These commands have docstrings describing their intended usage.

