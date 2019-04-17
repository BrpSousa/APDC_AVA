package pt.unl.fct.di.apdc.evaluation.resources;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.evaluation.util.RegisterData;
import pt.unl.fct.di.apdc.evaluation.util.AuthToken;
import pt.unl.fct.di.apdc.evaluation.util.LoginData;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

// Use login v2
@Path("/login")
@Produces(MediaType.APPLICATION_JSON +";charset=utf-8")
public class LoginResource {
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final Gson g = new Gson();
	
	public LoginResource() {} // Nothing to be done here
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response dologin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);
		
		if(data.username.equals("jleitao") && data.password.equals("password")) {
			AuthToken at = new AuthToken(data.username);
			return Response.ok(g.toJson(at)).build();
		}
		return Response.status(Status.FORBIDDEN).entity("Incorrect username or password.").build();
	}
	
	@GET
	@Path("/{username}")
	public Response checkUsernameAvailable(@PathParam("username") String username) {
		if(!username.equals("jleitao")) {
			return Response.ok().entity(g.toJson(false)).build();
		} else {
			return Response.ok().entity(g.toJson(true)).build();
		}
	}
	
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response login(pt.unl.fct.di.apdc.evaluation.util.LoginData data) {
		LOG.fine("Attempt to login user: " + data.username);
		
		Key userKey = KeyFactory.createKey("User", data.username);
		try {
			Entity user = datastore.get(userKey);
			String hashedPWD = (String) user.getProperty("user_pwd");
			if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				AuthToken token = new AuthToken(data.username);
				LOG.info("User '" + data.username + "'loggin in sucessfully.");
				return Response.ok(g.toJson(token)).build();
			} else {
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
			
			} catch (EntityNotFoundException e) {
				LOG.warning("Failed login attempt for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLoginV2(pt.unl.fct.di.apdc.evaluation.util.LoginData data,
								@Context HttpServletRequest request, @Context HttpHeaders headers) {
		LOG.fine("Attempt to login user: +" + data.username);
		
		Transaction txn = datastore.beginTransaction();
		Key userkey = KeyFactory.createKey("User", data.username);
		try {
			Entity user = datastore.get(userkey);
			
			// Obtain the login statistics
			Query ctrQuery = new Query("UserStats").setAncestor(userkey);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity ustats = null;
			if(results.isEmpty()) {
				ustats = new Entity("UserStats", user.getKey());
				ustats.setProperty("user_stats_logins", 0L);
				ustats.setProperty("user_stats_failed", 0L);
			} else {
				ustats = results.get(0);
			}
			String hashedPWD = (String) user.getProperty("user_pwd");
			if(hashedPWD.contentEquals(data.password)) { // should be hashed
				// Password correct
				
				// Build the logs
				Entity log = new Entity("UserLog", user.getKey());
				log.setProperty("user_login_ip", request.getRemoteAddr());
				log.setProperty("user_login_host", request.getRemoteHost());
				log.setProperty("user_login_latlon", headers.getHeaderString("X-AppEngine-CityLatLong"));
				log.setProperty("user_login_city", headers.getHeaderString("X-AppEngine-City"));
				log.setProperty("user_login_country", headers.getHeaderString("X-AppEngine-Country"));
				log.setProperty("user_login_time", new Date());
				
				// Batch Operation
				List<Entity> logs = Arrays.asList(log,ustats);
				datastore.put(txn,logs);
				txn.commit();
				
				// Return token
				AuthToken token = new AuthToken(data.username);
				LOG.info("User '" + data.username + "' logged in sucessfully.");
				return Response.ok(g.toJson(token)).build();
			} else {
				// Incorrect password
				ustats.setProperty("user_stats_failed", 1L + (long) ustats.getProperty("user_stats_failed"));
				datastore.put(txn,ustats);
				txn.commit();
				
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch (EntityNotFoundException e) {
			// Username does not exist
			LOG.warning("Failed login attempt for username " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
			
		}
	}
		
	/*
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response login(pt.unl.fct.di.apdc.firstwebapp.util.LoginData data) {
		Query q = new Query("User");
		q.setFilter(FilterOperator.EQUAL.of("user_pwd",data.password));
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {
			String queryUser = (String) result.getProperty("username");
			if(queryUser == null) {
				LOG.info("Wrong Password for: " + data.username);
				return Response.ok().entity(g.toJson(false)).build();
			}
			System.out.println("user: " + queryUser);
			if(queryUser.equals(data.username)) {
				System.out.println("Found user: " + data.username);
				LOG.info("User login " + data.username);
				return Response.ok().entity(g.toJson(true)).build();
			}
		}
		// probably won't reach this part
		LOG.info("Invalid Credentials: " + data.username);
		return Response.ok().entity(g.toJson(false)).build();
	}
	*/
}
