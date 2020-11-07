package io.parkwhere.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ping")
public class HeartbeatService {

    @GET
    @Path("/")
    public String heartbeat() {
        return "pong!";
    }
}