package hudson.plugins.junitannotator;

import hudson.FilePath;
import hudson.model.DirectoryBrowserSupport;
import hudson.tasks.junit.TestAction;
import hudson.tasks.test.TestObject;
import jenkins.model.Jenkins;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class AnnotatorTestAction extends TestAction {

	private final FilePath storage;
	private final List<String> attachments;
	private final TestObject testObject;

	public AnnotatorTestAction(TestObject testObject, FilePath storage, List<String> attachments) {
		this.storage = storage;
		this.testObject = testObject;
		this.attachments = attachments;
	}

	public String getDisplayName() {
		return "Annotator";
	}

	public String getIconFileName() {
		return "package.gif";
	}

	public String getUrlName() {
		return "annotator";
	}

	public DirectoryBrowserSupport doDynamic() {
		return new DirectoryBrowserSupport(this, storage, "Annotator", "package.gif", true);
	}

	@Override
	public String annotate(String text) {
		String url = Jenkins.getActiveInstance().getRootUrl()
				+ testObject.getRun().getUrl() + "testReport"
				+ testObject.getUrl() + "/annotator/";
		for (String attachment : attachments) {
			text = text.replace(attachment, "<a href=\"" + url + attachment
					+ "\">" + attachment + "</a>");
		}
		return text;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public TestObject getTestObject() {
		return testObject;
	}

	public static boolean isImageFile(String filename) {
		return filename.matches("(?i).+\\.(gif|jpe?g|png)$");
	}

	public static String getUrl(String filename) throws UnsupportedEncodingException {
		return "annotator/" + URLEncoder.encode(filename, "UTF-8");
	}

}
