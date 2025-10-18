package org.hcgames.hcfactions.manager;

import org.hcgames.hcfactions.faction.Faction;

/**
 * Represents a callback used for advanced faction search operations.
 * 
 * <p>Only one of the methods will be called when the search completes —
 * either {@link #onSuccess(Faction)} or {@link #onFail(FailReason)}.
 * 
 * <p>Optionally, this callback can be flagged to execute asynchronously.
 *
 * @param <T> the Faction type being searched
 */
public interface SearchCallback<T extends Faction> {

	/**
	 * Called when the search completes successfully.
	 *
	 * @param result the faction found
	 */
	void onSuccess(T result);

	/**
	 * Called when the search fails.
	 *
	 * @param reason the reason for failure
	 */
	void onFail(FailReason reason);

	/**
	 * Indicates whether this callback should run asynchronously.
	 *
	 * @return true if the callback is asynchronous
	 */
	default boolean isAsync() {
		return false;
	}

	/**
	 * Forces this callback to run asynchronously, overriding manager defaults.
	 *
	 * @return true if asynchronous execution is forced
	 */
	default boolean forceAsync() {
		return false;
	}

	/**
	 * Represents reasons for search failure.
	 */
	enum FailReason {

		/**
		 * When the search fails to find a faction.
		 */
		NOT_FOUND,

		/**
		 * When the search succeeds, but the faction found does not match the expected class.
		 */
		CLASS_CAST,

		/**
		 * When the search operation times out.
		 */
		TIMEOUT,

		/**
		 * When the search fails due to a database or data source issue.
		 */
		DATA_ERROR,

		/**
		 * When the search operation is cancelled before completion.
		 */
		CANCELLED,

		/**
		 * When the cause of failure is unknown.
		 */
		UNKNOWN
	}
}
