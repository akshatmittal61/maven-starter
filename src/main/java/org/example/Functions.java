package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    }

    public static Student selectStudentFromList(ArrayList<Student> students, String fieldName, String value) {
        if (CollectionUtils.isEmpty(students)) {
            return null;
        }

        // Handle index-based selection
        if ("index".equalsIgnoreCase(fieldName) || "i".equalsIgnoreCase(fieldName)) {
            try {
                int index = Integer.parseInt(value);
                return (index >= 0 && index < students.size()) ? students.get(index) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        // Get all fields from Student class including private ones
        Field[] fields = Student.class.getDeclaredFields();

        // Find the matching field (case-insensitive)
        Field targetField = null;
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                targetField = field;
                targetField.setAccessible(true); // Allow access to private fields
                break;
            }
        }

        if (targetField == null) {
            return null; // No such field found
        }

        // Search through students
        for (Student student : students) {
            if (student == null) continue;

            try {
                Object fieldValue = targetField.get(student);
                if (fieldValue == null) continue;

                // Handle different field types
                if (fieldValue instanceof String) {
                    if (((String) fieldValue).equalsIgnoreCase(value)) {
                        return student;
                    }
                } else {
                    // Try to convert the input string to the field's type
                    try {
                        Object convertedValue = convertStringToType(value, targetField.getType());
                        if (fieldValue.equals(convertedValue)) {
                            return student;
                        }
                    } catch (Exception e) {
                        // Ignore conversion errors
                    }
                }
            } catch (IllegalAccessException e) {
                // Shouldn't happen since we made it accessible
            }
        }

        return null;
    }

    // Helper method to convert string to various types
    private static Object convertStringToType(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException("Unsupported field type: " + targetType.getName());
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

        Student selectedStudent = selectStudentFromList(students, "name", "John");
        System.out.println(selectedStudent);
        Student selectedStudent2 = selectStudentFromList(students, "age", "2");
        System.out.println(selectedStudent2);
        Student selectedStudent3 = selectStudentFromList(students, "isGraduated", "true");
        System.out.println(selectedStudent3);
        Student selectedStudent4 = selectStudentFromList(students, "index", "3");
        System.out.println(selectedStudent4);
    }
}