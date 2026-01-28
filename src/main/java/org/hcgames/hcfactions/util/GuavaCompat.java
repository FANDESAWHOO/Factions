package org.hcgames.hcfactions.util;


import com.google.common.base.Preconditions;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;




public class GuavaCompat {

	public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
		Preconditions.checkNotNull(enumClass);
		Preconditions.checkNotNull((Object) value);
		try {
			return Optional.of(Enum.valueOf(enumClass, value));
		} catch (IllegalArgumentException iae) {
			return Optional.empty();
		}
	}

	public static <T> T firstNonNull(@Nullable T first, @Nullable T second) {
		return first != null ? first : Preconditions.checkNotNull(second);
	}
}