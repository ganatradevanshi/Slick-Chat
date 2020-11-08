package com.neu.prattle.servicetests;

import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.SpecificProfilePictureService;
import fse.team2.common.models.mongomodels.SpecificProfilePicture;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpecficProfilePictureServiceTests {
    private DatabaseService<SpecificProfilePicture> service = SpecificProfilePictureService.getInstance();

    @Test
    public void findByTest() {
        ObjectId id = new ObjectId();
        ObjectId forUser = new ObjectId();
        ObjectId userId = new ObjectId();
        String url = "http:///";

        SpecificProfilePicture model = new SpecificProfilePicture(id, userId, forUser, url);
        service.add(model);
        SpecificProfilePicture result = service.findBy(Filters.and
                (Filters.eq(SpecificProfilePicture.USER_ID_FIELD, userId),
                        Filters.eq(SpecificProfilePicture.FOR_USER_FIELD, forUser)))
                .next();
        assertEquals(id.toString(), result.getId().toString());
    }
}
