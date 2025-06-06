package org.hcgames.hcfactions.lib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings("unchecked")
public class LongObjectHashMap<V> implements Cloneable, Serializable {
	static final long serialVersionUID = 2841537710170573815L;

	private static final long EMPTY_KEY = Long.MIN_VALUE;
	private static final int BUCKET_SIZE = 4096;

	private transient long[][] keys;
	private transient V[][] values;
	private transient int modCount;
	private transient int size;

	public LongObjectHashMap() {
		initialize();
	}

	public LongObjectHashMap(Map<? extends Long, ? extends V> map) {
		this();
		putAll(map);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean containsKey(long key) {
		return get(key) != null;
	}

	public boolean containsValue(V value) {
		for (V val : values()) if (val == value || val.equals(value)) return true;

		return false;
	}

	public V get(long key) {
		int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
		long[] inner = keys[index];
		if (inner == null) return null;

		for (int i = 0; i < inner.length; i++) {
			long innerKey = inner[i];
			if (innerKey == EMPTY_KEY) return null;
			else if (innerKey == key) return values[index][i];
		}

		return null;
	}

	public V put(long key, V value) {
		int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
		long[] innerKeys = keys[index];
		V[] innerValues = values[index];
		modCount++;

		if (innerKeys == null) {
			// need to make a new chain
			keys[index] = innerKeys = new long[8];
			Arrays.fill(innerKeys, EMPTY_KEY);
			values[index] = innerValues = (V[]) new Object[8];
			innerKeys[0] = key;
			innerValues[0] = value;
			size++;
		} else {
			int i;
			for (i = 0; i < innerKeys.length; i++) {
				// found an empty spot in the chain to put this
				if (innerKeys[i] == EMPTY_KEY) {
					size++;
					innerKeys[i] = key;
					innerValues[i] = value;
					return null;
				}

				// found an existing entry in the chain with this key, replace it
				if (innerKeys[i] == key) {
					V oldValue = innerValues[i];
					innerKeys[i] = key;
					innerValues[i] = value;
					return oldValue;
				}
			}

			// chain is full, resize it and add our new entry
			keys[index] = innerKeys = Arrays.copyOf(innerKeys, i << 1);
			Arrays.fill(innerKeys, i, innerKeys.length, EMPTY_KEY);
			values[index] = innerValues = Arrays.copyOf(innerValues, i << 1);
			innerKeys[i] = key;
			innerValues[i] = value;
			size++;
		}

		return null;
	}

	public V remove(long key) {
		int index = (int) (keyIndex(key) & (BUCKET_SIZE - 1));
		long[] inner = keys[index];
		if (inner == null) return null;

		for (int i = 0; i < inner.length; i++) {
			// hit the end of the chain, didn't find this entry
			if (inner[i] == EMPTY_KEY) break;

			if (inner[i] == key) {
				V value = values[index][i];

				for (i++; i < inner.length; i++) {
					if (inner[i] == EMPTY_KEY) break;

					inner[i - 1] = inner[i];
					values[index][i - 1] = values[index][i];
				}

				inner[i - 1] = EMPTY_KEY;
				values[index][i - 1] = null;
				size--;
				modCount++;
				return value;
			}
		}

		return null;
	}

	public void putAll(Map<? extends Long, ? extends V> map) {
		for (Map.Entry entry : map.entrySet()) put((Long) entry.getKey(), (V) entry.getValue());
	}

	public void clear() {
		if (size == 0) return;

		modCount++;
		size = 0;
		Arrays.fill(keys, null);
		Arrays.fill(values, null);
	}

	public Set<Long> keySet() {
		return new KeySet();
	}

	public Collection<V> values() {
		return new ValueCollection();
	}

	/**
	 * Returns a Set of Entry objects for the HashMap. This is not how the internal
	 * implementation is laid out so this constructs the entire Set when called. For
	 * this reason it should be avoided if at all possible.
	 *
	 * @return Set of Entry objects
	 * @deprecated
	 */
	@Deprecated
	public Set<Map.Entry<Long, V>> entrySet() {
		HashSet<Map.Entry<Long, V>> set = new HashSet<Map.Entry<Long, V>>();
		for (long key : keySet()) set.add(new Entry(key, get(key)));

		return set;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		LongObjectHashMap clone = (LongObjectHashMap) super.clone();
		// Make sure we clear any existing information from the clone
		clone.clear();
		// Make sure the clone is properly setup for new entries
		clone.initialize();

		// Iterate through the data normally to do a safe clone
		for (long key : keySet()) {
			V value = get(key);
			clone.put(key, value);
		}

		return clone;
	}

	private void initialize() {
		keys = new long[BUCKET_SIZE][];
		values = (V[][]) new Object[BUCKET_SIZE][];
	}

	private long keyIndex(long key) {
		key ^= key >>> 33;
		key *= 0xff51afd7ed558ccdL;
		key ^= key >>> 33;
		key *= 0xc4ceb9fe1a85ec53L;
		key ^= key >>> 33;
		return key;
	}

	private void writeObject(ObjectOutputStream outputStream) throws IOException {
		outputStream.defaultWriteObject();

		for (long key : keySet()) {
			V value = get(key);
			outputStream.writeLong(key);
			outputStream.writeObject(value);
		}

		outputStream.writeLong(EMPTY_KEY);
		outputStream.writeObject(null);
	}

	private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
		inputStream.defaultReadObject();
		initialize();

		while (true) {
			long key = inputStream.readLong();
			V value = (V) inputStream.readObject();
			if (key == EMPTY_KEY && value == null) break;

			put(key, value);
		}
	}

	private class ValueIterator implements Iterator<V> {
		private int count;
		private int index;
		private int innerIndex;
		private int expectedModCount;
		private long lastReturned = EMPTY_KEY;

		long prevKey = EMPTY_KEY;
		V prevValue;

		ValueIterator() {
			expectedModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return count < size;
		}

		@Override
		public void remove() {
			if (modCount != expectedModCount) throw new ConcurrentModificationException();

			if (lastReturned == EMPTY_KEY) throw new IllegalStateException();

			count--;
			LongObjectHashMap.this.remove(lastReturned);
			lastReturned = EMPTY_KEY;
			expectedModCount = modCount;
		}

		@Override
		public V next() {
			if (modCount != expectedModCount) throw new ConcurrentModificationException();

			if (!hasNext()) throw new NoSuchElementException();

			long[][] keys = LongObjectHashMap.this.keys;
			count++;

			if (prevKey != EMPTY_KEY) innerIndex++;

			for (; index < keys.length; index++)
				if (keys[index] != null) {
					for (; innerIndex < keys[index].length; innerIndex++) {
						long key = keys[index][innerIndex];
						V value = values[index][innerIndex];
						if (key == EMPTY_KEY) break;

						lastReturned = key;
						prevKey = key;
						prevValue = value;
						return prevValue;
					}
					innerIndex = 0;
				}

			throw new NoSuchElementException();
		}
	}

	private class KeyIterator implements Iterator<Long> {
		final ValueIterator iterator;

		public KeyIterator() {
			iterator = new ValueIterator();
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Long next() {
			iterator.next();
			return iterator.prevKey;
		}
	}

	private class KeySet extends AbstractSet<Long> {
		@Override
		public void clear() {
			LongObjectHashMap.this.clear();
		}

		@Override
		public int size() {
			return LongObjectHashMap.this.size();
		}

		@Override
		public boolean contains(Object key) {
			return key instanceof Long && containsKey((Long) key);

		}

		@Override
		public boolean remove(Object key) {
			return LongObjectHashMap.this.remove((Long) key) != null;
		}

		@Override
		public Iterator<Long> iterator() {
			return new KeyIterator();
		}
	}

	private class ValueCollection extends AbstractCollection<V> {
		@Override
		public void clear() {
			LongObjectHashMap.this.clear();
		}

		@Override
		public int size() {
			return LongObjectHashMap.this.size();
		}

		@Override
		public boolean contains(Object value) {
			return containsValue((V) value);
		}

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}
	}

	private class Entry implements Map.Entry<Long, V> {
		private final Long key;
		private V value;

		Entry(long k, V v) {
			key = k;
			value = v;
		}

		@Override
		public Long getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V v) {
			V old = value;
			value = v;
			put(key, v);
			return old;
		}
	}
}
