package com.ilya.scheduleapp.containers;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassDataTests {
    /**
     * Types:
     * 1)String without teacher and only one class name
     * 2)String with teacher and only one class name
     * 3)String with two teachers and class rooms, but without second class name
     * 4)Full string with two teachers, class rooms and class names
     * 5)String with symbols like '-', '/' and others
     * 6)String with uppercase class names
     * 7)String with english class names
     * 8)String without teacher's initials, but with abstract names like 'teachers'
     * 9)Malformed strings
     */

    @Test
    public void classData_TestConstructor_Type1() {
        String inputStr = "2) пр.Физическая культура 2-А03";

        ClassData result = new ClassData(inputStr);

        assertEquals("2", result.getPosition());
        assertEquals("пр", result.getType());
        assertArrayEquals(new String[] { "Физическая культура", null }, result.getNames());
        assertArrayEquals(new String[] { null, null }, result.getTeachers());
        assertArrayEquals(new String[] { "2-А03", null }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type2() {
        String inputStr = " 2) лек.Математический анализ Павлушкин П П 2-А03 ";

        ClassData result = new ClassData(inputStr);

        assertEquals("2", result.getPosition());
        assertEquals("лек", result.getType());
        assertArrayEquals(new String[] { "Математический анализ", null }, result.getNames());
        assertArrayEquals(new String[] { "Павлушкин П П", null }, result.getTeachers());
        assertArrayEquals(new String[] { "2-А03", null }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type3() {
        String inputStr = " 2) лек.Культурология Павлушкин П П 2-А03 Петров В В 2_2";

        ClassData result = new ClassData(inputStr);

        assertEquals("2", result.getPosition());
        assertEquals("лек", result.getType());
        assertArrayEquals(new String[] { "Культурология", null }, result.getNames());
        assertArrayEquals(new String[] { "Павлушкин П П", "Петров В В" }, result.getTeachers());
        assertArrayEquals(new String[] { "2-А03", "2_2" }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type4() {
        String inputStr = " 2) лек.Культурология Павлушкин П П 2-А03 История России Петров В В 2_2";

        ClassData result = new ClassData(inputStr);

        assertEquals("2", result.getPosition());
        assertEquals("лек", result.getType());
        assertArrayEquals(new String[] { "Культурология", "История России" }, result.getNames());
        assertArrayEquals(new String[] { "Павлушкин П П", "Петров В В" }, result.getTeachers());
        assertArrayEquals(new String[] { "2-А03", "2_2" }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type5() {
        String inputStr = " 2) Пр.Иностранный язык -2 п/г(русский) Редькин Д Д 6-606";

        ClassData result = new ClassData(inputStr);

        assertEquals("2", result.getPosition());
        assertEquals("Пр", result.getType());
        assertArrayEquals(new String[] { "Иностранный язык -2 п/г(русский)", null }, result.getNames());
        assertArrayEquals(new String[] { "Редькин Д Д", null }, result.getTeachers());
        assertArrayEquals(new String[] { "6-606", null }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type6() {
        String inputStr = " 7) Пр.Структура РЖД Редькин Д Д 6-606 Использование CAD Петров П П 4-4";

        ClassData result = new ClassData(inputStr);

        assertEquals("7", result.getPosition());
        assertEquals("Пр", result.getType());
        assertArrayEquals(new String[] { "Структура РЖД", "Использование CAD" }, result.getNames());
        assertArrayEquals(new String[] { "Редькин Д Д", "Петров П П" }, result.getTeachers());
        assertArrayEquals(new String[] { "6-606", "4-4" }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type7() {
        String inputStr = " 3) пр.Теория менеджмента/Theory of management Троцкий А П 2-С3";

        ClassData result = new ClassData(inputStr);

        assertEquals("3", result.getPosition());
        assertEquals("пр", result.getType());
        assertArrayEquals(new String[] { "Теория менеджмента/Theory of management", null }, result.getNames());
        assertArrayEquals(new String[] { "Троцкий А П", null }, result.getTeachers());
        assertArrayEquals(new String[] { "2-С3", null }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type8() {
        String inputStr = " 3) пр.Физическая культура Преподаватели кафедры 2-С3";

        ClassData result = new ClassData(inputStr);

        assertEquals("3", result.getPosition());
        assertEquals("пр", result.getType());
        assertArrayEquals(new String[] { "Физическая культура", null }, result.getNames());
        assertArrayEquals(new String[] { "Преподаватели кафедры", null }, result.getTeachers());
        assertArrayEquals(new String[] { "2-С3", null }, result.getClassRooms());
    }

    @Test
    public void classData_TestConstructor_Type9() {
        String inputStr1 = " пр.Физическая культура Преподаватели кафедры 2-С3";
        String inputStr2 = "1) пр.Физическая культура Преподаватели кафедры";
        String inputStr3 = "2) пр.Физическая культура";

        ClassData result1 = new ClassData(inputStr1);
        ClassData result2 = new ClassData(inputStr2);
        ClassData result3 = new ClassData(inputStr3);

        assertTrue(result1.getType().isEmpty());
        assertTrue(result2.getType().isEmpty());
        assertTrue(result3.getType().isEmpty());
    }
}
