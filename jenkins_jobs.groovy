// Job DSL script to create Jenkins jobs for Numbers microservices

multibranchPipelineJob('number-generator') {
  displayName('Number Generator Service')
  description('Builds and deploys the number-generator microservice')
  
  branchSources {
    github {
      id('number-generator-repo')
      repoOwner('dotstone')
      repository('numbers')
      scanCredentialsId('github-pat-numbers')
    }
  }
  
  factory {
    workflowBranchProjectFactory {
      scriptPath('number-generator/Jenkinsfile')
    }
  }
  
  orphanedItemStrategy {
    discardOldItems {
      daysToKeep(-1)
      numToKeep(10)
    }
  }
  
  triggers {
    periodicFolderTrigger {
      interval('1m')
    }
  }
}

multibranchPipelineJob('number-calculator') {
  displayName('Number Calculator Service')
  description('Builds and deploys the number-calculator microservice')
  
  branchSources {
    github {
      id('number-calculator-repo')
      repoOwner('dotstone')
      repository('numbers')
      scanCredentialsId('github-pat-numbers')
    }
  }
  
  factory {
    workflowBranchProjectFactory {
      scriptPath('number-calculator/Jenkinsfile')
    }
  }
  
  orphanedItemStrategy {
    discardOldItems {
      daysToKeep(-1)
      numToKeep(10)
    }
  }
  
  triggers {
    periodicFolderTrigger {
      interval('1m')
    }
  }
}
