package com.neu.prattle.controllertests;

import com.neu.prattle.controller.CorsFilter;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CorsFilterTests {

    @Test
    public void testCorsFilter() {
        CorsFilter filter = new CorsFilter();
        ContainerRequestContext req = mock(ContainerRequestContext.class);
        ContainerResponseContext res = mock(ContainerResponseContext.class);
        MultivaluedMap map = mock(MultivaluedMap.class);
        when(res.getHeaders()).thenReturn(map);
        try {
            filter.filter(req, res);
            verify(map, atLeastOnce()).add(any(), any());
        } catch (IOException e) {
            fail("Exception shouldn't have been thrown");
        }

    }
}
