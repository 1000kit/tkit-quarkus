package org.tkit.quarkus.jpa.test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class UserSearchCriteriaDTO {

    @QueryParam("name")
    public String name;

    @QueryParam("email")
    public String email;

    @QueryParam("city")
    public String city;

    @QueryParam("index")
    @DefaultValue("0")
    public int index;

    @QueryParam("size")
    @DefaultValue("10")
    public int size;

}
