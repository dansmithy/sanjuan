package com.github.dansmithy.sanjuan.security;

/**
 * Generates a JSON String representing the currently authenticated user. If no user is authenticated, it should return an empty JSON object.
 */
public interface JsonUserDetailsProducer {

	String generateUserDetailsJson();

}
