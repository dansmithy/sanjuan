package com.github.dansmithy.spring;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Element;

import com.github.restdriver.serverdriver.http.Header;
import com.github.restdriver.serverdriver.http.response.Response;

public class SpringResponse implements Response {

	private Object responseObject;
	
	private static ObjectMapper mapper = new ObjectMapper();
	private final int code;
	
	public SpringResponse(Object responseObject, int code) {
		super();
		this.responseObject = responseObject;
		this.code = code;
	}
	
	public SpringResponse(Object responseObject) {
		this(responseObject, responseObject == null ? 204 : 200);
	}

	@Override
	public int getStatusCode() {
		return code;
	}

	@Override
	public String getContent() {
		return asText();
	}

	@Override
	public String asText() {
		try {
			return mapper.writeValueAsString(responseObject);
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize to Json", e);
		}
	}

	@Override
	public byte[] asBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Header> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Header> getHeaders(String headerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Header getHeader(String headerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getResponseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JsonNode asJson() {
		try {
			return mapper.readValue(asText(), JsonNode.class);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Cannot convert response [%s] to JsonNode.", asText()));
		}
	}

	@Override
	public Element asXml() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toCompactString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bigDump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tinyDump() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toBigString() {
		// TODO Auto-generated method stub
		return null;
	}

}
