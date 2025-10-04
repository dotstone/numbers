// Job DSL script to create Jenkins jobs for Numbers microservices

organizationFolder('numbers-services') {
  displayName('Numbers Microservices')
  description('Parent folder for all Numbers microservices')
  
  organizations {
    github {
      repoOwner('dotstone')
      apiUri('https://api.github.com')
      credentialsId('github-pat-numbers')
      
      traits {
        gitHubBranchDiscovery {
          strategyId(1)
        }
        gitHubPullRequestDiscovery {
          strategyId(1)
        }
        gitHubTagDiscovery()
      }
    }
  }
  
  projectFactories {
    workflowMultiBranchProjectFactory {
      scriptPath('Jenkinsfile')
    }
  }
  
  configure { node ->
    def traits = node / navigators / 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' / traits
    traits << 'jenkins.scm.impl.trait.WildcardSCMHeadFilterTrait' {
      includes('numbers')
      excludes('')
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

multibranchPipelineJob('number-generator') {
  displayName('Number Generator Service')
  description('Builds and deploys the number-generator microservice')
  
  branchSources {
    git {
      id('number-generator-repo')
      remote('https://github.com/dotstone/numbers.git')
      credentialsId('github-pat-numbers')
      
      traits {
        gitBranchDiscovery()
        gitTagDiscovery()
      }
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
    git {
      id('number-calculator-repo')
      remote('https://github.com/dotstone/numbers.git')
      credentialsId('github-pat-numbers')
      
      traits {
        gitBranchDiscovery()
        gitTagDiscovery()
      }
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
