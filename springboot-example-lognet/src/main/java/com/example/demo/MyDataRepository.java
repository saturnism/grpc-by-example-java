package com.example.demo;

import com.google.common.collect.Maps;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MyDataRepository {
	private final JdbcTemplate jdbcTemplate;

	public MyDataRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insert(String key, String value) {
		jdbcTemplate.update("insert into kv_table values (?, ?)", key, value);
	}

	public List<String> findAll() {
		return jdbcTemplate.query("select * from kv_table", (rs, rowNum) -> {
			return rs.getString(1);
		});
	}
}
