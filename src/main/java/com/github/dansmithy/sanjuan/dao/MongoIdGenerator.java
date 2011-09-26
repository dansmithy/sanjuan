package com.github.dansmithy.sanjuan.dao;

public interface MongoIdGenerator {

	Long getNextLongId(String type);
}
