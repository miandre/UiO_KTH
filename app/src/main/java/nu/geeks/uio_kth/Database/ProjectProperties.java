package nu.geeks.uio_kth.Database;

/**
 * Created by Micke on 2016-02-17.
 */
public class ProjectProperties {

    public static abstract class NewProjectData{
        public static final String PROJECT_NAME = "project_name";
        public static final String PROJECT_PASSWORD = "project_password";
        public static final String PROJECT_ID = "project_id";
        public static final String PROJECT_ICON = "project_icon";
        public static final String TABLE_NAME = "current_projects";
    }

    public static abstract class NewTransactionData{
        public static final String PROJECT_ID = "project_id";
        public static final String TABLE_NAME = "transactions";
        public static final String PERSON = "person";
        public static final String amount = "amount";
        public static final String object = "object";
    }

}
