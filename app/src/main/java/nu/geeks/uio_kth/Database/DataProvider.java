package nu.geeks.uio_kth.Database;

public class DataProvider  {

    private String projectName;
    private String projectPassword;
    private String projectId;

    public DataProvider(String projectName, String projectPassword, String projectId) {
        this.projectName = projectName;
        this.projectPassword = projectPassword;
        this.projectId = projectId;
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
}
