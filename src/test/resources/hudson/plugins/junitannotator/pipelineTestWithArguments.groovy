node {
    step([$class: 'JUnitResultArchiver',
          testResults: "*.xml",
          testDataPublishers: [[$class: 'TestAnnotator',
                                msg: 'Pipeline provided message',
                                param2: 1.5]]])
}