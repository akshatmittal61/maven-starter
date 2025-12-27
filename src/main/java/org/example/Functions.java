package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class Functions {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    @Builder
    public static class Student {
        private String name;
        private int age;
        private boolean isGraduated;
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("age", age);
            map.put("isGraduated", isGraduated);
            return map;
        }
    }

    /**
     * Selects an object from a list based on the specified field and value.
     *
     * @param <T> The type of elements in the list
     * @param list The input list (can be List<Object>, List<String>, List<Map>, etc.)
     * @param fieldOrKey The field name (for objects), key (for maps), or "index" for list index
     * @param value The value to match against (as String, will be converted to target type)
     * @return The first matching element, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T selectFromList(List<T> list, String fieldOrKey, String value) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        // Handle index-based selection
        if ("index".equalsIgnoreCase(fieldOrKey) || "i".equalsIgnoreCase(fieldOrKey)) {
            try {
                int index = Integer.parseInt(value);
                return (index >= 0 && index < list.size()) ? list.get(index) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Get the first non-null element to determine the type
        T sample = list.stream().filter(Objects::nonNull).findFirst().orElse(null);
        if (sample == null) {
            return null;
        }

        // Handle different types of list elements
        if (sample instanceof Map) {
            return findInMapList((List<Map>) list, fieldOrKey, value);
        } else if (isWrapperOrPrimitive(sample)) {
            return findInPrimitiveList(list, value);
        } else {
            return findInObjectList(list, fieldOrKey, value);
        }
    }

    // Helper method to handle Map elements
    private static <T> T findInMapList(List<Map> list, String key, String value) {
        for (Map map : list) {
            if (map == null) continue;

            Object mapValue = map.get(key);
            if (mapValue != null && mapValue.toString().equalsIgnoreCase(value)) {
                return (T) map;
            }
        }
        return null;
    }

    // Helper method to handle primitive/wrapper types
    private static  <T> T findInPrimitiveList(List<T> list, String value) {
        for (T item : list) {
            if (item != null && item.toString().equals(value)) {
                return item;
            }
        }
        return null;
    }

    // Helper method to handle custom objects using reflection
    private static  <T> T findInObjectList(List<T> list, String fieldName, String value) {
        if (list.isEmpty()) return null;

        T sample = list.get(0);
        Class<?> clazz = sample.getClass();

        try {
            // Try to find a field with the given name (case-insensitive)
            Field field = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                .findFirst()
                .orElse(null);

            if (field != null) {
                field.setAccessible(true);
                for (T item : list) {
                    if (item == null) continue;

                    Object fieldValue = field.get(item);
                    if (fieldValue != null && fieldValue.toString().equalsIgnoreCase(value)) {
                        return item;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            // Ignore and return null if there's an access issue
        }
        return null;
    }

    // Check if the object is a primitive or wrapper type
    private static boolean isWrapperOrPrimitive(Object obj) {
        return obj instanceof String ||
            obj instanceof Number ||
            obj instanceof Boolean ||
            obj instanceof Character;
    }

    public static void main(String[] args) {
        Student st1 = Student.builder().name("John").age(1).isGraduated(false).build();
        Student st2 = Student.builder().name("Jane").age(2).isGraduated(true).build();
        Student st3 = Student.builder().name("Mike").age(3).isGraduated(false).build();
        Student st4 = Student.builder().name("Alice").age(4).isGraduated(true).build();
        Student st5 = Student.builder().name("Bob").age(5).isGraduated(true).build();
        Student st6 = Student.builder().name("Charlie").age(6).isGraduated(false).build();
        
        ArrayList<Student> students = new ArrayList<>();
        students.add(st1);
        students.add(st2);
        students.add(st3);
        students.add(st4);
        students.add(st5);
        students.add(st6);

        Student selectedStudent = selectFromList(students, "name", "John");
        System.out.println(selectedStudent);
        Student selectedStudent2 = selectFromList(students, "age", "2");
        System.out.println(selectedStudent2);
        Student selectedStudent3 = selectFromList(students, "isGraduated", "true");
        System.out.println(selectedStudent3);
        Student selectedStudent4 = selectFromList(students, "index", "3");
        System.out.println(selectedStudent4);

        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(st1.toMap());
        mapList.add(st2.toMap());
        mapList.add(st3.toMap());
        mapList.add(st4.toMap());
        mapList.add(st5.toMap());
        mapList.add(st6.toMap());

        System.out.println(selectFromList(mapList, "name", "John"));
        System.out.println(selectFromList(mapList, "age", "2"));
        System.out.println(selectFromList(mapList, "isGraduated", "true"));
        System.out.println(selectFromList(mapList, "index", "3"));

        ArrayList<String> names = new ArrayList<>();
        names.add("John");
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        names.add("Mike");
        names.add("David");

        System.out.println(selectFromList(names, "age", "John"));
        System.out.println(selectFromList(names, "index", "3"));
    }
}