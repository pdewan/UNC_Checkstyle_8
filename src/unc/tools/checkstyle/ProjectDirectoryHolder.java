package unc.tools.checkstyle;

public class ProjectDirectoryHolder {
	static String currentProject = "vanilla-project";

	public static String getCurrentProjectDirectory() {
		return currentProject;
	}

	public static void setCurrentProjectDirectory(String currentProject) {
		ProjectDirectoryHolder.currentProject = currentProject;
	}

}
