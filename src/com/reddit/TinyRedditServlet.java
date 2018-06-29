package com.reddit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;



import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;

@SuppressWarnings("serial")
public class TinyRedditServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		
		// Create reddit msg 
		Random r=new Random();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		
		resp.getWriter().println("populating store...");
		
		int maxmessage=100;
		int maxuser=10;
		for (int i = 0; i < maxmessage; i++) {
			Entity e = new Entity("rmsg", "m" + i);
			e.setProperty("body", "Hello " + i );
			e.setProperty("owner","u"+r.nextInt(maxuser+1));
			datastore.put(e);
			
			Entity index=new Entity("rmsgIndex","i"+i,e.getKey());
			ArrayList<String> voters = new ArrayList<String>();
			for (int j = 0; j < 5000; j++) {
				int r1 = r.nextInt(maxuser+1);
				if (r1 != 1)
					voters.add("u"+r1);
			}
			voters.add("u1");
			index.setProperty("voters", voters);
			datastore.put(index);
		}
		resp.getWriter().println("populating store done !!");
		
		
		// Query
		MessageEndpoint me = new MessageEndpoint();
		
		resp.getWriter().println("getting index timeline for: u1");
		long t1 = System.currentTimeMillis();
		Collection<Entity> test = me.getMyVotesMessages("u1");
		resp.getWriter().println("done in " + (System.currentTimeMillis() - t1));

		resp.getWriter().println("Number = "+ test.size());
		
		resp.getWriter().println("finished");
		
	}
}
