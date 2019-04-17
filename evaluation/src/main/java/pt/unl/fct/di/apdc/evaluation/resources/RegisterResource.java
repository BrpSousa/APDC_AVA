package pt.unl.fct.di.apdc.evaluation.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

// use register v4
@Path("/register")
public class RegisterResource {
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() {}
	
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationV1(pt.unl.fct.di.apdc.evaluation.util.RegisterData data) {
		Entity user = new Entity("User", data.username);
		user.setProperty("user_pwd", data.password); // DigestUtils.sha512Hex(data.password) should be used instead of data.password
		user.setUnindexedProperty("user_creation_time", new Date());
		datastore.put(user);
		LOG.info("User registered " + data.username);
		return Response.ok().build();
	}
	
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationV2(pt.unl.fct.di.apdc.evaluation.util.RegisterData data) {
		if(!data.password.contentEquals(data.confirmation) || data.username.equals(data.email) || !data.email.contains("@") || data.name.isEmpty() || data.password.isEmpty() || data.email.isEmpty() || data.username.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Incorrect credentials").build();
		}
		// probably should handle one data attribute at a time each with their own response
		Entity user = new Entity("User");
		user.setProperty("username", data.username);
		user.setProperty("user_pwd", data.password);
		user.setUnindexedProperty("user_creation_time", new Date());
		user.setUnindexedProperty("user_email", data.email);
		user.setUnindexedProperty("user_name", data.name);
		datastore.put(user);
		LOG.info("User registered " + data.username);
		return Response.ok().build();
	}
	
	@POST
	@Path("/v3")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationV3(pt.unl.fct.di.apdc.evaluation.util.RegisterData data) {
		if(!data.password.contentEquals(data.confirmation) || data.username.equals(data.email) || !data.email.contains("@") || data.name.isEmpty() || data.password.isEmpty() || data.email.isEmpty() || data.username.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Incorrect credentials").build();
		}
	try {
		// If Entity does not exist an exception is thrown
		Key userKey = KeyFactory.createKey("User", data.username);
		Entity user = datastore.get(userKey);
		return Response.status(Status.BAD_REQUEST).entity("User already exists").build();
	} catch (EntityNotFoundException e)	{
		Entity user = new Entity("User", data.username);
		user.setProperty("user_name", data.name);
		user.setProperty("user_pwd", DigestUtils.sha512Hex(data.password));
		user.setProperty("user_email", data.email);
		user.setUnindexedProperty("user_creation_time", new Date());
		datastore.put(user);
		LOG.info("User registered " + data.username);
		return Response.ok().build();
	}
	}
	/*
	@GET
	@Path("/get/{username}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doGet(@PathParam("username") String username) {
		Query q = new Query("User");
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {
			String queryUser = (String) result.getProperty("username");
			if(queryUser == null) {
				LOG.info("User not found: " + username);
				return Response.ok().entity(g.toJson(false)).build();
			}
			System.out.println("user: " + queryUser);
			if(queryUser.equals(username)) {
				System.out.println("Found user: " + queryUser);
				LOG.info("User confirmed " + queryUser);
				return Response.ok().entity(g.toJson(true)).build();
			}
		}
		// probably won't reach this part
		LOG.info("User not found: " + username);
		return Response.ok().entity(g.toJson(false)).build();
	}
	*/
	
	@POST
	@Path("/v4")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationV4(pt.unl.fct.di.apdc.evaluation.util.RegisterData data) {
		if(!data.password.contentEquals(data.confirmation) || data.username.equals(data.email) || !data.email.contains("@") || data.name.isEmpty() || data.password.isEmpty() || data.email.isEmpty() || data.username.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity("Incorrect credentials").build();
		}
	Transaction txn = datastore.beginTransaction();
	try {
		// If Entity does not exist an exception is thrown
		Key userKey = KeyFactory.createKey("User", data.username);
		Entity user = datastore.get(userKey);
		txn.rollback();
		return Response.status(Status.BAD_REQUEST).entity("User already exists").build();
	} catch (EntityNotFoundException e)	{
		Entity user = new Entity("User", data.username);
		user.setProperty("user_name", data.name);
		user.setProperty("user_pwd", data.password);
		user.setProperty("user_email", data.email);
		user.setProperty("user_role", data.role);
		user.setUnindexedProperty("user_creation_time", new Date());
		user.setUnindexedProperty("user_status", data.profileStatus);
		user.setUnindexedProperty("user_cell_number", data.cellphoneNumber);
		user.setUnindexedProperty("user_phone_number", data.telephoneNumber);
		user.setUnindexedProperty("user_address", data.address);
		datastore.put(txn,user);
		LOG.info("User registered " + data.username);
		txn.commit();
		return Response.ok().build();
	} finally {
		if(txn.isActive()) {
			txn.rollback();
		}
	}
	}
	
	
}
	 
	
