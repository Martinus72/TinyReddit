package com.reddit;

import com.reddit.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Persistent;

@Api(name = "messageendpoint", namespace = @ApiNamespace(ownerDomain = "mycompany.com", ownerName = "mycompany.com", packagePath = "services"))
public class MessageEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listMessage")
	public CollectionResponse<Message> listMessage(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Message> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Message.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Message>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Message obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Message> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getMessage")
	public Message getMessage(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Message message = null;
		try {
			message = mgr.getObjectById(Message.class, id);
		} finally {
			mgr.close();
		}
		return message;
	}
	
	/**
	 * This method gets the entities list with hotest votes. It uses HTTP GET method.
	 * list contains 10 entities
	 *
	 * @return The entities list with primary key id.
	 */
	@ApiMethod(name = "getAllMessages")
	public List<Entity> getAllMessages() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message").addSort("hot", SortDirection.DESCENDING);
		
		PreparedQuery pq= ds.prepare(q);
		
		List<Entity> results=pq.asList(FetchOptions.Builder.withLimit(10));
		
		return results;
	}
	
	/**
	 * This method gets the last entities list. It uses HTTP GET method.
	 * list contains 10 entities
	 * 
	 * @return The entities list with primary key id.
	 */
	@ApiMethod(name="getLastMessages", path="getLastMessages")
	public List<Entity> getLastMessages() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message").addSort("pubDate", SortDirection.DESCENDING);
		
		PreparedQuery pq= ds.prepare(q);
		
		List<Entity> results=pq.asList(FetchOptions.Builder.withLimit(10));
		
		return results;
	}
	
	/**
	 * This method gets the last entities list. It uses HTTP GET method.
	 * list contains 10 entities
	 * 
	 * @return The entities list with primary key id.
	 */
	@ApiMethod(name="getMyVotesMessages", path="getMyVotesMessages")
	public Collection<Entity> getMyVotesMessages(@Named("userID") String userID) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Filter p = new FilterPredicate("voters", FilterOperator.EQUAL, userID);
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query ("MessageIndex").setFilter(p);
		q.setKeysOnly();

		PreparedQuery pq = ds.prepare(q);

		List<Entity> results = pq.asList(FetchOptions.Builder.withLimit(100));
		List<Key> pk = new ArrayList<Key>();
		for (Entity r : results) {
			pk.add(r.getParent());
		}
		
		Map<Key, Entity> map = ds.get(pk);
		return map.values();	
	}
	
	/**
	 * This method gets the user's messages entities list. It uses HTTP GET method.
	 * list contains 10 entities
	 *
	 * @return The user's messages entities list with primary key id.
	 */
	@ApiMethod(name = "getMyMessages")
	public List<Entity> getMyMessages(@Named("userID") String userID) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Filter f = new FilterPredicate("owner", FilterOperator.EQUAL, userID);
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("Message").setFilter(f);
		
		PreparedQuery pq= ds.prepare(q);
		
		List<Entity> results=pq.asList(FetchOptions.Builder.withLimit(10));
		
		return results;
	}


	/**
	 * This inserts a new entity into App Engine datastore.
	 * It uses HTTP POST method.
	 *
	 * @param message the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertMessage")
	public Entity insertMessage(@Named("userID") String userID, @Named("fullName") String fullName, @Named("title") String title, @Named("body") String body, @Named("imgUrl") String imgUrl) throws ParseException {
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Random r = new Random();
		int rand = r.nextInt();
		
        Entity message = new Entity("Message", "m" + rand);
        
        message.setProperty("owner", userID);
        message.setProperty("fullName", fullName);
        message.setProperty("title", title);
        message.setProperty("body", body);
        message.setProperty("imgUrl", imgUrl);
        
        message.setProperty("hot", 0);
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        message.setProperty("pubDate", dateFormat.format(cal.getTime()).toString());
        
        ds.put(message);
        
        Entity messageIndex = new Entity("MessageIndex","im"+rand, message.getKey());
        
        ds.put(messageIndex);        
        return message;
	}
	
	
	/**
	 * This method is used for updating property voters of an existing entity.
	 * It uses HTTP PUT method.
	 *
	 * @param name the message entity key name.
	 * @param userID the ID of the user who updated the entity.
	 * @param kindVote the kind of the vote.
	 * @return The updated entity.
	 * @throws com.google.appengine.api.datastore.EntityNotFoundException 
	 */
	@ApiMethod(name = "likeMessage")
	public Entity likeMessage(@Named("name") String name, @Named("userID") String userID, @Named("kindVote") int kindVote) throws com.google.appengine.api.datastore.EntityNotFoundException {
				
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Key keyMess = KeyFactory.createKey("Message", name);
		
		Entity message = ds.get(keyMess);
			
		Key keyMessIndex = KeyFactory.createKey(message.getKey(),"MessageIndex","i" + name);
		
		Entity messageIndex = ds.get(keyMessIndex);
		
		ArrayList<String> voters;
		
		if (messageIndex.getProperty("voters") != null){
			voters = (ArrayList<String>) messageIndex.getProperty("voters");
		}
		else {
			voters = new ArrayList<String>();
			messageIndex.setProperty("voters", voters);
		}
		
		if (!voters.contains(userID)) {
		
	    	voters.add(userID);
	    	    	
	        messageIndex.setProperty("voters", voters);
	        
	        long hot = (long) message.getProperty("hot");
	        
	        if (kindVote > 0)
	        	message.setProperty("hot", hot + 1);
	        else
	        	message.setProperty("hot", hot - 1);
	        
	        ds.put(message);
	        
	        ds.put(messageIndex);
	        
	        return messageIndex;
		}
		else {
			return null;
		}
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param message the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateMessage")
	public Message updateMessage(Message message) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsMessage(message)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(message);
		} finally {
			mgr.close();
		}
		return message;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeMessage")
	public void removeMessage(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Message message = mgr.getObjectById(Message.class, id);
			mgr.deletePersistent(message);
		} finally {
			mgr.close();
		}
	}

	private boolean containsMessage(Message message) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Message.class, message.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
