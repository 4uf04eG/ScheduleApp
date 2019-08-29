package com.ilya.scheduleapp.containers;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupsContainerTests {
    @Test
    public void groupsContainer_TestAdd() {
        GroupsContainer container = new GroupsContainer();

        container.add("Some", "Value");
        container.add("Another", "Value");

        assertEquals(container.size(), 2);
    }

    @Test
    public void groupsContainer_TestFindLink() {
        GroupsContainer container = new GroupsContainer();
        container.add("Some", "Data");

        String actual = container.findLink("Some");

        assertEquals("Data", actual);
    }

    @Test
    public void groupsContainer_TestToSortedList() {
        GroupsContainer container = new GroupsContainer();
        container.add("name1", "value1");
        container.add("name3", "value3");
        container.switchToNextRow();
        container.add("name2", "value2");
        container.switchToNextRow();
        container.add("name0", "value0");
        container.switchToNextRow();
        List<String> expected = Arrays.asList("1", "name0", "name1", "name2", "2", "name3");

        List<String> actual = container.toSortedLinearList();

        assertEquals(expected, actual);
    }

    @Test
    public void groupsContainer_TestFindAllLinks() {
        GroupsContainer container = new GroupsContainer();
        container.add("name1", "value1");
        container.add("name2", "value2");
        container.add("name3", "value3");
        List<String> expected = Arrays.asList("value1", "value2", "value3");

        List<String> actual = container.getAllLinks();

        assertEquals(expected, actual);
    }


}
