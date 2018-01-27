package assign.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import assign.domain.Project;
import assign.domain.Project;
import assign.services.CourseStudentService;
import assign.services.CourseStudentServiceImpl;

@Path("/")
public class UTCoursesResource {

	CourseStudentService databaseService;
	String password;
	String username;
	String dburl;	

	public UTCoursesResource(@Context ServletContext servletContext) {
		String host = servletContext.getInitParameter("DBHOST");
		String name = servletContext.getInitParameter("DBNAME");
		dburl = "jdbc:mysql://" + host + ":3306/" + name;
		username = servletContext.getInitParameter("DBUSERNAME");
		password = servletContext.getInitParameter("DBPASSWORD");
		this.databaseService = new CourseStudentServiceImpl(dburl, username, password);		
	}

	@GET
	@Path("/helloworld")
	@Produces("text/html")
	public Response helloWorld() {
		String out="";
		System.out.println("Inside helloworld");
		System.out.println("DB creds are:");
		System.out.println("DBURL:" + dburl);
		System.out.println("DBUsername:" + username);
		System.out.println("DBPassword:" + password);	

		return Response.ok("Response Body").build();
	}

	@POST
	@Path("/projects")
	@Consumes("application/xml")
	public Response create(InputStream is) throws Exception {
		System.out.println("Create new Project");	
		Project newProject = readNewProject(is);
		try {
			newProject = this.databaseService.addProject(newProject);
			System.out.println("New Project ID: " + newProject.getProjectID());
			return Response.created(URI.create("/projects/" + newProject.getProjectID())).build();
		} catch (Exception e) {
			System.out.println("Exception: "+ e.getMessage());
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@Path("/projects/{projectID}")
	@Consumes("application/xml")
	public Response update(@PathParam("projectID") String projectID, InputStream is) {
		System.out.println("Updating Project "+projectID);	
		try {
			int project_ID = Integer.parseInt(projectID);
			Project updatedProject = readNewProject(is);
			try {
				updatedProject = this.databaseService.updateProject(project_ID, updatedProject);
				System.out.println(projectToString(updatedProject));
				return Response.status(204).build();
			} catch (Exception e) {
				if (e.getMessage().equals("Project Not Found")) {
					return Response.status(404).build();
				}
				else {
					return Response.status(Status.BAD_REQUEST).build();
				}
			}
		} catch (Exception e) {
			return Response.status(400).build();
		}
	}

	@GET
	@Path("/projects/{projectID}")
	public Response get(@PathParam("projectID") String projectID) {
		System.out.println("Getting Project "+projectID);	
		try {
			int project_ID = Integer.parseInt(projectID);
			try {
				Project getProject = this.databaseService.getProject(project_ID);
				String xml = projectToXMLString(getProject);
				System.out.println(projectToString(getProject));
				return Response.ok(xml).build();
				//or we can do Response.status(Status.ok).entity(project).build();
			} catch (Exception e) {
				return Response.status(404).build();			
			}			
		} catch (Exception e) {
			return Response.status(404).build();
		}
	}

	@DELETE
	@Path("/projects/{projectID}")
	public Response delete(@PathParam("projectID") String projectID) {
		System.out.println("Deleting Project "+projectID);	
		try {
			int project_ID = Integer.parseInt(projectID);
			try {
				databaseService.deleteProject(project_ID);
				return Response.status(200).build();
			} catch (Exception e){
				return Response.status(404).build();
			}
		} catch (Exception e) {
			return Response.status(404).build();
		}
	}

	protected String projectToXMLString(Project project) {
		return "<project id=" + project.getProjectID() + ">" +
				"<name>" + project.getName() + "</name>" +
				"<description>" + project.getDescription() + "</description>" +
				"</project>";
	}

	protected String projectToString(Project project) {
		return "<project id=" + project.getProjectID() + ">\n" +
				"    <name>" + project.getName() + "</name>\n" +
				"    <description>" + project.getDescription() + "</description>\n" +
				"</project>\n";
	}

	protected Project readNewProject(InputStream is) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(is);
			Element root = doc.getDocumentElement();
			Project project = new Project();
			NodeList nodes = root.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);
				if (element.getTagName().equals("name")) {
					project.setName(element.getTextContent());
				}
				else if (element.getTagName().equals("description")) {
					project.setDescription(element.getTextContent());
				}
			}
			return project;
		} catch (Exception e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
	}

	protected void outputCourses(OutputStream os, Project project) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(project, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}
}
