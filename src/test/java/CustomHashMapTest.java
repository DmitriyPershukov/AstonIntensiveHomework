import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomHashMapTest {
    @Test
    void testPutAndGet(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        for(int i = 0; i < 200; i++){
            map.put("Simon" + i, i);
        }
        for(int i = 0; i < 200; i++){
            Assertions.assertEquals(i, map.get("Simon" + i));
        }
    }

    @Test
    void testGetNonPresentEntry(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        Assertions.assertNull(map.get("Gary"));
    }

    @Test
    void testRemove(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("Helen", 23);
        map.put("Mary", 25);
        map.put("George", 24);
        Assertions.assertEquals(25, map.remove("Mary"));
        Assertions.assertNull(map.get("Mary"));
        Assertions.assertEquals(23, map.remove("Helen"));
        Assertions.assertNull(map.get("Helen"));
        Assertions.assertEquals(1, map.size());
    }

    @Test
    void testRemoveNonPresentEntry(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        Assertions.assertNull(map.remove("Mary"));
    }

    @Test
    void testClear(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("Helen", 23);
        map.put("Mary", 25);
        map.put("George", 24);
        map.clear();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void testContainsKey(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put("Mary", 25);
        Assertions.assertTrue(map.containsKey("Mary"));
        Assertions.assertFalse(map.containsKey("Mike"));
    }

    @Test
    void testPutAndGetNullKey(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put(null, 25);
        Assertions.assertEquals(25, map.get(null));
        map.put(null, 28);
        Assertions.assertEquals(28, map.get(null));
    }

    @Test
    void testRemoveNullKey(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put(null, 25);
        Assertions.assertEquals(25, map.remove(null));
        Assertions.assertNull(map.get(null));
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void testCotnainsNullKey(){
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        map.put(null, 25);
        Assertions.assertTrue(map.containsKey(null));
        map.remove(null);
        Assertions.assertFalse(map.containsKey(null));
    }

    @Test
    void testInitialCapacityConstructor() throws IllegalAccessException, NoSuchFieldException {
        CustomHashMap<String, Integer> map = new CustomHashMap<>();
        var field = map.getClass().getDeclaredField("capacity");
        field.setAccessible(true);
        map = new CustomHashMap<>(-16);
        Assertions.assertEquals(16, field.get(map));
        map = new CustomHashMap<>(32);
        Assertions.assertEquals(32, field.get(map));
        map = new CustomHashMap<>(80);
        Assertions.assertEquals(128, field.get(map));

    }
}
