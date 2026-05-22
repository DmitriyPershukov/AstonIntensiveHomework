import java.util.*;

public class CustomHashMap<K, V> extends AbstractMap<K, V> {
    private int size = 0;
    private int capacity = 16;
    private float loadFactor = 0.75f;
    private LinkedList<Entry<K, V>>[] buckets = new LinkedList[capacity];

    public CustomHashMap(){}
    public CustomHashMap(int initialCapacity){
        if (initialCapacity > 16){
            capacity = roundUpToPowerOfTwo(initialCapacity);
        }
    }
    public CustomHashMap(int initialCapacity, float loadFactor){
        if (initialCapacity > 16){
            capacity = roundUpToPowerOfTwo(initialCapacity);
        }
        this.loadFactor = loadFactor;
    }

    public CustomHashMap(Map<K, V> map){
        map
        .entrySet()
        .iterator()
        .forEachRemaining(entry -> put(entry.getKey(), entry.getValue()));
    }

    private int roundUpToPowerOfTwo(int x){
        x--;
        x |= x>>1;
        x |= x>>2;
        x |= x>>4;
        x |= x>>8;
        x |= x>>16;
        x++;
        return x;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.new HashMapEntrySet();
    }

    @Override
    public V put(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        if (buckets[bucketIndex] == null){
            buckets[bucketIndex] = new LinkedList<>();
        }
        for (Entry<K, V> entry: buckets[bucketIndex]){
            if (Objects.equals(entry.getKey(), key)){
                entry.setValue(value);
                return value;
            }
        }
        buckets[bucketIndex].addLast(new AbstractMap.SimpleEntry<>(key, value));
        size++;
        if (size > loadFactor * capacity){
            resize();
        }
        return value;
    }

    private int getBucketIndex(Object key){
        if (key == null){
            return 0;
        }
        int hashcode = key.hashCode();
        return (capacity - 1) & (hashcode ^ (hashcode >>> 16));
    }

    private void resize(){
        List<Entry<K, V>> currentEntries = new ArrayList<>();
        entrySet().iterator().forEachRemaining(currentEntries::add);
        capacity *= 2;
        buckets = new LinkedList[capacity];
        for (Entry<K, V> entry: currentEntries){
            K key = entry.getKey();
            V value = entry.getValue();
            int bucketIndex = getBucketIndex(key);
            if (buckets[bucketIndex] == null){
                buckets[bucketIndex] = new LinkedList<>();
            }
            buckets[bucketIndex].addLast(new AbstractMap.SimpleEntry<>(key, value));
        }
    }

    @Override
    public V get(Object key) {
        int bucketIndex = getBucketIndex(key);
        if (buckets[bucketIndex] == null){
            return null;
        }
        for (Entry<K, V> entry: buckets[bucketIndex]){
            if (Objects.equals(entry.getKey(), key)){
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        int bucketIndex = getBucketIndex(key);
        if (buckets[bucketIndex] == null){
            return false;
        }
        for (Entry<K, V> entry: buckets[bucketIndex]){
            if (Objects.equals(entry.getKey(), key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V remove(Object key) {
        int bucketIndex = getBucketIndex(key);
        if (buckets[bucketIndex] == null){
            return null;
        }
        int entryId = 0;
        for (Entry<K, V> entry: buckets[bucketIndex]){
            if (Objects.equals(entry.getKey(), key)){
                size--;
                return buckets[bucketIndex].remove(entryId).getValue();
            }
            entryId++;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private class HashMapEntrySet extends AbstractSet<Entry<K, V>>{

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return this.new HashMapEntrySetIterator();
        }

        @Override
        public void clear() {
            size = 0;
            buckets = new LinkedList[capacity];
        }

        @Override
        public int size() {
            return size;
        }

        private class HashMapEntrySetIterator implements Iterator<Entry<K, V>>{
            private int lastReturnedBucketIndex = 0;
            private int lastReturnedEntryIndex = -1;
            private boolean ableToRemoveLastReturnedEntry = false;

            @Override
            public boolean hasNext() {
                int bucketIndex = lastReturnedBucketIndex;
                int entryIndex = lastReturnedEntryIndex;
                while(bucketIndex < capacity){
                    if (buckets[bucketIndex] == null || buckets[bucketIndex].isEmpty()){
                        bucketIndex++;
                        entryIndex = -1;
                        continue;
                    }
                    entryIndex++;
                    if(entryIndex < buckets[bucketIndex].size()){
                        return true;
                    }
                    bucketIndex++;
                    entryIndex = -1;
                }
                return false;
            }

            @Override
            public Entry<K, V> next() {
                int bucketIndex = lastReturnedBucketIndex;
                int entryIndex = lastReturnedEntryIndex;
                while(bucketIndex < capacity){
                    if (buckets[bucketIndex] == null || buckets[bucketIndex].isEmpty()){
                        bucketIndex++;
                        entryIndex = -1;
                        continue;
                    }
                    entryIndex++;
                    if(entryIndex < buckets[bucketIndex].size()){
                        lastReturnedBucketIndex = bucketIndex;
                        lastReturnedEntryIndex = entryIndex;
                        ableToRemoveLastReturnedEntry = false;
                        return buckets[bucketIndex].get(entryIndex);
                    }
                    bucketIndex++;
                    entryIndex = -1;
                }
                return null;
            }

            @Override
            public void remove() {
                if (ableToRemoveLastReturnedEntry){
                    buckets[lastReturnedBucketIndex].remove(lastReturnedEntryIndex);
                    lastReturnedEntryIndex--;
                    ableToRemoveLastReturnedEntry = false;
                    size--;
                }
            }
        }
    }
}
