package org.hcgames.hcfactions.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapSorting {
	private static final Function EXTRACT_KEY = (Function<Map.Entry<Object, Object>, Object>) input -> input == null ? null : input.getKey();
	private static final Function EXTRACT_VALUE = (Function<Map.Entry<Object, Object>, Object>) input -> input == null ? null : input.getValue();

	public static <T, V extends Comparable<V>> List<Map.Entry<T, V>> sortedValues(Map<T, V> map) {
		return MapSorting.sortedValues(map, Ordering.natural());
	}

	public static <T, V> List sortedValues(Map<T, V> map, Comparator<V> valueComparator) {
		return Ordering.from(valueComparator).onResultOf(extractValue()).sortedCopy((Iterable) map.entrySet());
	}

	public static <T, V> Iterable<T> keys(List<Map.Entry<T, V>> entryList) {
		return Iterables.transform(entryList, MapSorting.extractKey());
	}

	public static <T, V> Iterable<V> values(List<Map.Entry<T, V>> entryList) {
		return Iterables.transform(entryList, MapSorting.extractValue());
	}

	private static <T, V> Function<Map.Entry<T, V>, T> extractKey() {
		return EXTRACT_KEY;
	}

	private static <T, V> Function<Map.Entry<T, V>, V> extractValue() {
		return EXTRACT_VALUE;
	}

}

