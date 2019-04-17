package pt.unl.fct.di.apdc.evaluation.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public class ComputationResource {
		private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
		private final Gson g = new Gson();
		private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		public ComputationResource() {}//nothing to be done here
		
		
		@GET
		@Path("/time")
		public Response getCurrentTime() {
			LOG.fine("Replying to date request.");
			return Response.ok().entity(g.toJson(fmt.format(new Date()))).build();
		}
		@POST
		@Path("/changeName/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeName(String newName ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_name", newName);
				datastore.put(user);
				return Response.ok("Name changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		@POST
		@Path("/changePassword/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changePassword(String newPass ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_pwd", newPass);
				datastore.put(user);
				return Response.ok("Password changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		@POST
		@Path("/changeEmail/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeEmail(String newEmail ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_email", newEmail);
				datastore.put(user);
				return Response.ok("Email changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		@POST
		@Path("/changeCellNumber/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeCellNumber(String newNumber ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_cell_number", newNumber);
				datastore.put(user);
				return Response.ok("Cell number changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		
		@POST
		@Path("/changeTelephoneNumber/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changePhoneNumber(String newNumber ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_phone_number", newNumber);
				datastore.put(user);
				return Response.ok("Phone number changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		
		@POST
		@Path("/changeAddress/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeAddress(String newAddress ,@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_address", newAddress);
				datastore.put(user);
				return Response.ok("Address changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		@POST
		@Path("/changeStatus/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeStatus(String newStatus ,@PathParam("username") String username) {
			if(newStatus.equals("public")) newStatus = "private";
			else newStatus = "public";
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_status", newStatus);
				datastore.put(user);
				return Response.ok("Address changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		
		@POST
		@Path("/changeRole/{username}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response changeSRole(String newRole ,@PathParam("username") String username) {
			if(!newRole.equals("USER") && !newRole.equals("GBO") && !newRole.equals("GS") && !newRole.contentEquals("AUSER")){
				return Response.status(Status.BAD_REQUEST).entity("Invalid Role").build();
			}
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				user.setIndexedProperty("user_role", newRole);
				datastore.put(user);
				return Response.ok("Role changed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		@GET
		@Path("/remove/{username}")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
		public Response doRemove(@PathParam("username") String username) {
			try {
				Key userKey = KeyFactory.createKey("User", username);
				Entity user = datastore.get(userKey);
				datastore.delete(userKey);
				return Response.ok("User removed").build();
			} catch (EntityNotFoundException e) {
				return Response.status(Status.BAD_REQUEST).entity("User does not exist").build();
			}
		}
		
		@POST
		@Path("/compute")
		public Response executeComputeTask() {
			LOG.fine("Starting to execute computation taks");
			try {
				Thread.sleep(60*1000*10); //10 min...
			} catch (Exception e) {
				LOG.logp(Level.SEVERE, this.getClass().getCanonicalName(), "executeComputeTask", "An exception has ocurred", e);
						return Response.serverError().build();
		} //Simulates 60s execution
		return Response.ok().build();
	}
		@GET
		@Path("/compute")
		public Response triggerExecuteComputeTask() {
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(TaskOptions.Builder.withUrl("/rest/utils/compute"));
		return Response.ok().build();
		}	
}