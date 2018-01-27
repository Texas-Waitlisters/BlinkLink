package assign.services;

import assign.domain.Project;

public interface CourseStudentService {
    
    public Project addProject(Project project) throws Exception;
    
    public void deleteProject(int project_ID) throws Exception;
    
    public Project updateProject(int project_ID, Project project) throws Exception;
    
    public Project getProject(int project_ID) throws Exception;
}
