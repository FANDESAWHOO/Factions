package org.hcgames.hcfactions.exception;

public class NoFactionFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L; // ADDED BECAUSE ECLIPSE RECOMMENDATION.

	public NoFactionFoundException(String name) {
		super("No faction found: " + name);
	}
}
