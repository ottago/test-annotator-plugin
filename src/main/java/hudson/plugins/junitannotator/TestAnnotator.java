package hudson.plugins.junitannotator;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.junit.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TestAnnotator extends TestDataPublisher {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private String message;


    @DataBoundConstructor
    public TestAnnotator(String msg) {
        message = msg;
    }

//    public static FilePath getAttachmentPath(Run<?, ?> build) {
//        return new FilePath(new File(build.getRootDir().getAbsolutePath()))
//                .child("junit-annotator");
//    }
//
//    public static FilePath getAttachmentPath(FilePath root, String child) {
//        FilePath dir = root;
//        if (!StringUtils.isEmpty(child)) {
//            dir = dir.child(TestObject.safe(child));
//        }
//        return dir;
//    }

    @Override
    public TestResultAction.Data contributeTestData(Run<?, ?> build, FilePath workspace, Launcher launcher,
                                   TaskListener listener, TestResult testResult)
            throws IOException, InterruptedException {

//        listener.getLogger().println("NKW contributeTestData build=" + build.getClass() +
//                " workspace=" + workspace.getBaseName() +
//                " testResult=" + testResult.getClass());


        // Check which Suite's were actually run in this session.
        for (SuiteResult suite : testResult.getSuites()) {
            String suiteResultFile = suite.getFile();
            if (suiteResultFile == null || !workspace.child(suiteResultFile).exists()) {
                continue;
            }

//            listener.getLogger().println("\tsuiteResultFile=" + suiteResultFile + " id=" + suite.getId());


            for (CaseResult test: suite.getCases()) {
                // SuiteTime: 2018-08-12T09:17:01 BuildTime: 4 min 54 sec  GIT:${buildInfo['GIT_COMMIT']} on 172.18.0.1  Module:${buildInfo['module_name']}  Group:${buildInfo['module_group']}
                String msg =
                        DATE_FORMAT.format(build.getTimestamp().getTime()) +
                        " on " + workspace.toComputer().getDisplayName();
                if (message != null) {
                    msg += message;
                }

                // Append to any existing description.
                if (test.getDescription() != null) {
                    msg = test.getDescription() + '\n' + msg;
                }
                test.setDescription(msg);

//                listener.getLogger().println("NKW test=" + test.getName() + " desc=[" + test.getDescription() + "]");
            }
        }

/*
        final GetTestDataMethodObject methodObject = new GetTestDataMethodObject(build, workspace, launcher, listener, testResult);
        Map<String, Map<String, List<String>>> attachments = methodObject.getAttachments();

        if (attachments.isEmpty()) {
            return null;
        }

        return new Data(attachments);
        */
        return null;
    }


//    public static class Data extends TestResultAction.Data {
//
//        @Deprecated
//        private transient Map<String, List<String>> attachments;
//        private Map<String, Map<String, List<String>>> attachmentsMap;
//
//        /**
//         * @param attachmentsMap { fully-qualified test class name → { test method name → [ attachment file name ] } }
//         */
//        public Data(Map<String, Map<String, List<String>>> attachmentsMap) {
//            this.attachmentsMap = attachmentsMap;
//        }
//
//        @Override
//        @SuppressWarnings("deprecation")
//        public List<TestAction> getTestAction(hudson.tasks.junit.TestObject t) {
//            TestObject testObject = (TestObject) t;
//
//            final String packageName;
//            final String className;
//            final String testName;
//
//            if (testObject instanceof ClassResult) {
//                // We're looking at the page for a test class (i.e. a single TestCase)
//                packageName = testObject.getParent().getName();
//                className = testObject.getName();
//                testName = null;
//            } else if (testObject instanceof CaseResult) {
//                // We're looking at the page for an individual test (i.e. a single @Test method)
//                packageName = testObject.getParent().getParent().getName();
//                className = testObject.getParent().getName();
//                testName = testObject.getName();
//            } else {
//                // Otherwise, we don't want to show any attachments (e.g. at the package level)
//                return Collections.emptyList();
//            }
//
//            // Determine the fully-qualified test class (i.e. com.example.foo.MyTestCase)
//            String fullName = "";
//            if (!packageName.equals("(root)")) {
//                fullName += packageName;
//                fullName += ".";
//            }
//            fullName += className;
//
//            // Get the mapping of individual test -> attachment names
//            Map<String, List<String>> tests = attachmentsMap.get(fullName);
//            if (tests == null) {
//                return Collections.emptyList();
//            }
//
//            List<String> attachmentPaths;
//            if (testName == null) {
//                // If we're looking at the page for the test class, rather than an individual test
//                // method, then gather together the set of attachments from all of its test methods
//                LinkedHashSet<String> paths = new LinkedHashSet<String>();
//
//                // Ensure attachments are shown in the same order as the tests
//                TreeMap<String, List<String>> sortedTests = new TreeMap<String, List<String>>(tests);
//                for (List<String> testList : sortedTests.values()) {
//                    paths.addAll(testList);
//                }
//                attachmentPaths = new ArrayList<String>(paths);
//            } else {
//                attachmentPaths = tests.get(testName);
//            }
//
//            // If we didn't find anything for this test class or test method, give up
//            if (attachmentPaths == null || attachmentPaths.isEmpty()) {
//                return Collections.emptyList();
//            }
//
//            // Return a single TestAction which will display the attached files
//            FilePath root = getAttachmentPath(testObject.getRun());
//            AnnotatorTestAction action = new AnnotatorTestAction(testObject,
//                    getAttachmentPath(root, fullName), attachmentPaths);
//            return Collections.<TestAction> singletonList(action);
//        }
//
//        /** Handles migration from the old serialisation format. */
//        private Object readResolve() {
//            if (attachments != null && attachmentsMap == null) {
//                // Migrate from the flat list per test class to a map of <test method, attachments>
//                attachmentsMap = new HashMap<String, Map<String, List<String>>>();
//
//                // Previously, there was no mapping between individual tests and their attachments,
//                // so here we just associate all attachments with an empty-named test method.
//                //
//                // This means that all attachments will appear on the test class page as before,
//                // but they won't also be repeated on each individual test method's page
//                for (Map.Entry<String,List<String>> entry : attachments.entrySet()) {
//                    HashMap<String, List<String>> testMap = new HashMap<String, List<String>>();
//                    testMap.put("", entry.getValue());
//                    attachmentsMap.put(entry.getKey(), testMap);
//                }
//                attachments = null;
//            }
//
//            return this;
//        }
//
//    }

    @Extension
    public static class DescriptorImpl extends Descriptor<TestDataPublisher> {

        @Override
        public String getDisplayName() {
            return "Publish test attachments";
        }

    }

}
