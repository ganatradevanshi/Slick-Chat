package fse.team2.common.models;

import fse.team2.common.models.mongomodels.Poll;
import fse.team2.common.models.mongomodels.UserResponse;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PollTests {

    @Test
    public void UserResponsesTest() {
        ObjectId userId = new ObjectId();
        String responseString = "yes";

        UserResponse response = new UserResponse();
        response.setResponse("yes");
        response.setUserId(userId);

        assertEquals(responseString, response.getResponse());
        assertEquals(userId, response.getUserId());
    }

    @Test
    public void pollTest() {
        ObjectId messageId = new ObjectId();
        List<String> options = new ArrayList<>();
        options.add("yes");
        options.add("no");

        Poll poll = new Poll();
        poll.setMessageId(messageId);
        poll.setOptions(options);

        ObjectId userId = new ObjectId();
        String responseString = "yes";

        UserResponse response = new UserResponse();
        response.setResponse("yes");
        response.setUserId(userId);

        List<UserResponse> responses = new ArrayList<>();
        responses.add(response);
        poll.setResponses(responses);

        assertEquals(poll.getMessageId(), messageId);
        assertEquals(poll.getOptions(), options);
        assertEquals(poll.getResponses(), responses);
    }
}
