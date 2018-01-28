package assign.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
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

import assign.domain.Device;
import assign.services.DeviceDBService;
import assign.services.DeviceDBServiceImpl;

@Path("/")
public class RedtoothResource {

	DeviceDBService databaseService;
	String password;
	String username;
	String dburl;	

	public RedtoothResource(@Context ServletContext servletContext) {
		String host = servletContext.getInitParameter("DBHOST");
		String name = servletContext.getInitParameter("DBNAME");
		dburl = "jdbc:mysql://" + host + ":3306/" + name;
		username = servletContext.getInitParameter("DBUSERNAME");
		password = servletContext.getInitParameter("DBPASSWORD");
		this.databaseService = new DeviceDBServiceImpl(dburl, username, password);		
	}
	
	@GET
	@Path("/test/{deviceID}")
	@Produces("text/html")
	public Response test(@PathParam("deviceID") String deviceID) throws Exception {
		databaseService.testDatabase(deviceID);
		return Response.ok("Response Body").build();
	}
	
	@GET
	@Path("/hello")
	@Produces("text/html")
	public String hello() {
		return "Success";
	}
	
	@GET
	@Path("/report/{deviceID}/{status}")
	@Produces("text/html")
	public String report(@PathParam("deviceID") String deviceID, @PathParam("status") String statusString) throws SQLException {
		try {
			int priority = 0;
			boolean status = statusString.toLowerCase().equals("true");
			try {
				boolean deviceIsInDatabase = this.databaseService.hasDevice(deviceID);
				if (!deviceIsInDatabase) {
					this.databaseService.addDevice(deviceID, priority, status);
				}
				else {
					Device currentDevice = this.databaseService.getDevice(deviceID);
					if (currentDevice.getStatus()!=status || currentDevice.getPriority() != priority) {
						this.databaseService.updateDevice(deviceID, priority, status);
						System.out.println("Updating");
					}
				}
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}	
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String result = this.databaseService.makeDecision();
		System.out.println(result);
		return result;
	}
	
	@GET
	@Path("analytics")
	@Produces("text/html")
	public String printAnalytics() throws SQLException {
		ArrayList<Device> devices = this.databaseService.analytics();
		StringBuilder resultSB = new StringBuilder("<!DOCTYPE html>\n" + 
				"<html style='padding:2%;'>\n" + 
				"<head>\n" + 
				"	<meta http-equiv=\"refresh\" content=\"1\">"
				+ "</head><body><h1>Redtooth Analytics</h1>"
				+ "<table><tr><th style='padding:0px 5px'>Device MAC Address</th><th style='padding:0px 5px'>Timestamp of Change</th><th style='padding:0px 5px'>Status</th></tr>");
		for (Device d : devices) {
			resultSB.append("<tr><td style='padding:0px 5px'>");
			resultSB.append(d.getID());
			resultSB.append("</td><td style='padding:0px 5px'>");
			resultSB.append(d.getTimestamp());
			resultSB.append("</td ><td style='padding:0px 5px'>");
			resultSB.append(d.getStatus());
			resultSB.append("</td></tr>");
		}
		resultSB.append("</table><br>Chosen: ");
		resultSB.append(this.databaseService.makeDecision());
		resultSB.append("</body></html>");
		return resultSB.toString();
	}

}