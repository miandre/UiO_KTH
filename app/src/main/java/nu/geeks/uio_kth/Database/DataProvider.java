package nu.geeks.uio_kth.Database;

// DTO for project data
public class DataProvider  {

    private String projectName;
    private String projectPassword;
    private String projectId;
    private String projectIcon;

    public DataProvider() {

    }

    public DataProvider(String projectName, String projectPassword, String projectId, String projectIcon) {
        this.projectName = projectName;
        this.projectPassword = projectPassword;
        this.projectId = projectId;
        this.projectIcon=projectIcon;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectPassword() {
        return projectPassword;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectPassword(String projectPassword) {
        this.projectPassword = projectPassword;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectIcon() {
        return projectIcon;
    }

    public void setProjectIcon(String projectIcon) {
        this.projectIcon = projectIcon;
    }
}
