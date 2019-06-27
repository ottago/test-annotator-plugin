# Jenkins Test Annotator

The idea behind this plugin is two fold.
Firstly it adds more details about where and when the test ran to the description of each test.  This is helpful when diagnosing issues caused by a particular node or OS flavour.
Secondly it can post details of the test to an ES instance allowing offline data to be captures well after the PR or run has been deleted from Jenkins.
