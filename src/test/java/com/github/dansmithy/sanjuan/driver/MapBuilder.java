package com.github.dansmithy.sanjuan.driver;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K, V> {

	private Map<K, V> map = new HashMap<K, V>();

	public MapBuilder() {
		super();
	}

	private MapBuilder(Map<K, V> map) {
		this.map = map;
	}

	public static MapBuilder<String, String> simple() {
		return new MapBuilder<String, String>();
	}

	public static <K, V> MapBuilder<K, V> linked(Class<K> key, Class<V> value) {
		return new MapBuilder<K, V>(new LinkedHashMap<K, V>());
	}

	public MapBuilder<K, V> linked() {
		map = new LinkedHashMap<K, V>();
		return this;
	}

	public MapBuilder<K, V> add(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> build() {
		return map;
	}
	
	public Map<K, V> buildUnmodifiable() {
		return Collections.unmodifiableMap(map);
	}
}
