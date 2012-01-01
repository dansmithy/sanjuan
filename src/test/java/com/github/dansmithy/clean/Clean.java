package com.github.dansmithy.clean;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

public class Clean {

	@Test
	public void cleanGamesFromDb() throws UnknownHostException, MongoException {
		Mongo m = new Mongo();
		DB testDatabase = m.getDB("test");
		
		DBCollection gameCollection = testDatabase.getCollection("game");
		BasicDBObject removeGamesObject = new BasicDBObject();
		Pattern gamesWithAliceAsPlayerPattern = Pattern.compile("alice.*", Pattern.CASE_INSENSITIVE);
		removeGamesObject.append("players.name", gamesWithAliceAsPlayerPattern);
		WriteResult removeGamesResult = gameCollection.remove(removeGamesObject);
		System.out.println(String.format("Have removed %d games.", removeGamesResult.getN()));
		
		DBCollection userCollection = testDatabase.getCollection("user");
		BasicDBObject removeUsersObject = new BasicDBObject();
		Pattern users = Pattern.compile(".+-.+-.+-.+-.+-.+", Pattern.CASE_INSENSITIVE);
		removeUsersObject.append("username", users);
		WriteResult removeUsersResult = userCollection.remove(removeUsersObject);
		System.out.println(String.format("Have removed %d users.", removeUsersResult.getN()));
		
	}
}
