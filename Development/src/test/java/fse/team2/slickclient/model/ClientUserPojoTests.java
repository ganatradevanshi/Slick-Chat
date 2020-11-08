package fse.team2.slickclient.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fse.team2.common.models.mongomodels.preferences.Preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientUserPojoTests {
    private ClientUserPojo clientUserPojo;

    @Before
    public void init() {
        clientUserPojo = new ClientUserPojo();
    }

    @Test
    public void testId() {
        this.clientUserPojo.setId("user123");
        assertEquals("user123", this.clientUserPojo.getId());
    }

    @Test
    public void testUsername() {
        this.clientUserPojo.setUsername("user123");
        assertEquals("user123", this.clientUserPojo.getUsername());
    }

    @Test
    public void testName() {
        this.clientUserPojo.setName("user123");
        assertEquals("user123", this.clientUserPojo.getName());
    }

    @Test
    public void testIsDeleted() {
        this.clientUserPojo.setDeleted(true);
        assertTrue(this.clientUserPojo.isDeleted());
    }

    @Test
    public void testIsHidden() {
        this.clientUserPojo.setHidden(true);
        assertTrue(this.clientUserPojo.isHidden());
    }

    @Test
    public void testPreferences() {
        List<Preference> preferences = new ArrayList<>();
        this.clientUserPojo.setPreferences(preferences);
        assertEquals(preferences, this.clientUserPojo.getPreferences());
    }

    @Test
    public void testFollowers() {
        List<String> preferences = new ArrayList<>();
        this.clientUserPojo.setFollowers(preferences);
        assertEquals(preferences, this.clientUserPojo.getFollowers());
    }

    @Test
    public void testFollowing() {
        List<String> preferences = new ArrayList<>();
        this.clientUserPojo.setFollowing(preferences);
        assertEquals(preferences, this.clientUserPojo.getFollowing());
    }

    @Test
    public void testMessages() {
        List<String> preferences = new ArrayList<>();
        this.clientUserPojo.setMessages(preferences);
        assertEquals(preferences, this.clientUserPojo.getMessages());
    }

    @Test
    public void testGroups() {
        List<String> preferences = new ArrayList<>();
        this.clientUserPojo.setGroups(preferences);
        assertEquals(preferences, this.clientUserPojo.getGroups());
    }

    @Test
    public void testToken() {
        this.clientUserPojo.setToken("authToken");
        assertEquals("authToken", this.clientUserPojo.getToken());
    }

    @Test
    public void testToString() {
        this.clientUserPojo.setId("123");
        this.clientUserPojo.setUsername("johnDoe");
        this.clientUserPojo.setName("John");
        assertTrue(this.clientUserPojo.toString().contains("ClientUserPojo{id='123', username='johnDoe', name='John'}"));
    }

    @Test
    public void testEquals() {
        this.clientUserPojo.setId("123");
        this.clientUserPojo.setUsername("johnDoe");
        this.clientUserPojo.setName("John");

        ClientUserPojo client2 = new ClientUserPojo();
        client2.setId("123");
        client2.setUsername("johnDoe");
        client2.setName("John");

        assertTrue(this.clientUserPojo.equals(client2));
    }
}
