package com.o4.observatory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.PostConstruct;

@SpringBootTest
class ObservatoryApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	@PostConstruct
	public void testDatabaseConnection() {
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:astronomy.db");
			Statement stmt = conn.createStatement();
			
			// Test if users table exists
			ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
			if (!rs.next()) {
				System.out.println("USERS TABLE DOES NOT EXIST!");
			} else {
				System.out.println("USERS TABLE EXISTS");
			}
			
			conn.close();
		} catch (SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
