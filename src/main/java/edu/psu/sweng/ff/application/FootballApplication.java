package edu.psu.sweng.ff.application;

import javax.ws.rs.ApplicationPath;

import com.sun.jersey.api.core.PackagesResourceConfig;

@ApplicationPath("ws")
public class FootballApplication extends PackagesResourceConfig {

	public FootballApplication() {
		super("edu.psu.sweng.ff.controller");
	}
	
}
