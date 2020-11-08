package com.neu.prattle.service.mediaservice;

import org.junit.Test;

import static org.junit.Assert.*;

public class AWSMediaServiceTest {

    @Test
    public void testSingleton() {
        assertEquals(AWSMediaService.getInstance(), AWSMediaService.getInstance());
    }

    @Test
    public void testUpload() {
        byte[] bytes = new byte[1];
        AWSMediaService.getInstance().upload(bytes, "File.txt");
        assertTrue(AWSMediaService.getInstance().mediaExists("File.txt"));
        AWSMediaService.getInstance().delete("File.txt");
        assertFalse(AWSMediaService.getInstance().mediaExists("File.txt"));
    }

}