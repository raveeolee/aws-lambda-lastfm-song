package example;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<Map<String,Object>, String> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DynamoDB initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        //standard.setRegion(Region.getRegion(Regions.EU_WEST_2).getName());
        return new DynamoDB(client);
    }

    @Override
    public String handleRequest(Map<String,Object> event, Context context) {
        logEnvironmentVariables(event, context);
        DynamoDB dynamoDB = initDynamoDbClient();
        Table access_key = dynamoDB.getTable(System.getenv().get("DDB_TABLE"));
        access_key.putItem(
                new Item()
                        .with("id", "4")
        );

        ItemCollection<ScanOutcome> scan = access_key.scan();
        List<String> result = new ArrayList<>();
        for (Item item : scan) {
            item.toJSON();
        }
        return gson.toJson(result);
    }

    //private

    private void logEnvironmentVariables(Map<String,Object> event, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));
        logger.log("EVENT: " + gson.toJson(event));
        logger.log("EVENT TYPE: " + event.getClass().toString());
    }

    private RuntimeException error(String errorMessage, int code, Context context) {
        Map<String, Object> errorPayload = new HashMap();
        errorPayload.put("errorType", "BadRequest");
        errorPayload.put("httpStatus", code);
        errorPayload.put("requestId", context.getAwsRequestId());
        errorPayload.put("message", errorMessage);
        String message = gson.toJson(errorPayload);
        return new RuntimeException(message);
    }
}