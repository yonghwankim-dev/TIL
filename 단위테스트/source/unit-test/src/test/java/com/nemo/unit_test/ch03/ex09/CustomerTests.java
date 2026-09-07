package com.nemo.unit_test.ch03.ex09;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 공통 초기화 코드
 * <pre>
 *     테스트간에 공통적으로 데이터베이스 객체를 사용하기 때문에 @BeforeAll을 사용하여 데이터베이스 객체를 초기화하고
 *     연결을 수행한다.
 *     전체 테스트가 끝나면 @AfterAll을 통해서 데이터베이스 연결을 끊는다.
 * </pre>
 *
 */
class CustomerTests {

	static class Database{
		public void connect(){
			System.out.println("connect the Database");
		}

		public void dispose() {
			System.out.println("dispose the Database");
		}
	}

	private static Database database;

	@BeforeAll
	static void beforeAll() {
		database = new Database();
		System.out.println("create database object : " + database);
		database.connect();
	}

	@AfterAll
	static void afterAll() {
		database.dispose();
	}

	@Test
	void purchase_succeeds_when_enough_inventory() {
		// when
		System.out.println("use database : " + database);
	}

	@Test
	void purchase_succeeds_when_enough_inventory_2() {
		// when
		System.out.println("use database2 : " + database);
	}
}
