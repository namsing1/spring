
package io.helidon.examples.quickstart.se;

import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.media.jsonp.JsonpSupport;

import io.helidon.webclient.WebClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple service to greet you. Examples:
 *
 * Get default greeting message:
 * curl -X GET http://localhost:8080/greet
 *
 * Get greeting message for Joe:
 * curl -X GET http://localhost:8080/greet/Joe
 *
 * Change greeting
 * curl -X PUT -H "Content-Type: application/json" -d '{"greeting" : "Howdy"}' http://localhost:8080/greet/greeting
 *
 * The message is returned as a JSON object
 */

public class GreetService implements Service {

    /**
     * The config value for the key {@code greeting}.
     */
    private final AtomicReference<String> greeting = new AtomicReference<>();

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private static final Logger LOGGER = Logger.getLogger(GreetService.class.getName());

    GreetService(Config config) {
        greeting.set(config.get("app.greeting").asString().orElse("Ciao"));
    }

    /**
     * A service registers itself by updating the routing rules.
     * @param rules the routing rules.
     */
    @Override
    public void update(Routing.Rules rules) {
        rules
            .get("/", this::getDefaultMessageHandler)
                .get("/saas",this::getSaasResponse)
                .get("/onHand/{name}",this::getOnhand)
            //.get("/{name}", this::getMessageHandler)
                .get("/{name}", this::getSaasResponse)
            .put("/greeting", this::updateGreetingHandler)
            .post("/reserve",this::reserve);
    }

    /**
     * Return a worldly greeting message.
     * @param request the server request
     * @param response the server response
     */
    private void getDefaultMessageHandler(ServerRequest request, ServerResponse response) {
        sendResponse(response, "World");
    }

    /**
     * Return a greeting message using the name that was provided.
     * @param request the server request
     * @param response the server response
     */
    private void getMessageHandler(ServerRequest request, ServerResponse response) {
        String name = request.path().param("name");
        //getSaasResponse(request,response);
        sendResponse(response, name);
    }

    private void reserve(ServerRequest request, ServerResponse response){
        //CreateReservation createReservation = (CreateReservation) request.content().as(CreateReservation.class);
        try {
            System.out.println("Reached");

            //System.out.println("Reserve >> " + request.content().as(JsonObject.class).get());
            request.content().as(JsonObject.class);
            JsonObject jo = request.content().as(JsonObject.class).get();
            System.out.println("Jo "+jo);
            jo.getString("ReservationQuantity");
            //System.out.println("Reserve >> " + request.content().as(JsonObject.class)
                    //.thenAccept(reservation->System.out.println("Reservation "+ reservation)));
            System.out.println("ReservationQty "+jo.getString("ReservationQuantity"));
            //
            sendResponse(response, "reserve");
        }
        catch(Exception e){
            System.out.println("Exception "+e);
        }
    }
    /**
     * Method that gets the data from SaaS. The itemId is obtained as a parameter and sent as a query string to get
     * the response.
     * @param request
     * @param response
     */
    private void getSaasResponse(ServerRequest request, ServerResponse response){
        try {
            String itemId = request.path().param("name");



            WebClient webclient = WebClient.builder()
                    .baseUri("https://edrx-test.fa.us2.oraclecloud.com/fscmRestApi/resources/11.13.18.05/itemsV2")
                    .addMediaSupport(JsonpSupport.create())
                    .addHeader("Authorization", "Basic U0NNVVNFUjpPcmFjbGUxMjM=")
                    .build();
            System.out.println("Webclient "+webclient);
            JsonObject content = webclient
                    .get()
                    .queryParam("fields","ItemId,ItemNumber,ItemDescription,OrganizationCode")
                    .queryParam("onlyData","true")
                    //.queryParam("q","ItemNumber=IBM1")
                    .queryParam("q","ItemNumber="+itemId)
                    .request(JsonObject.class).get();
            System.out.println("Json content :: "+content);
            JsonArray itemArray = content.getJsonArray("items");
            System.out.println("Array content :: "+itemArray);

            //content.forEach()
            sendResponse(response,itemArray);
        }catch (Exception e){
            System.out.println(e);
        }
        //sendResponse(response,"Brati");

    }

    private void getOnhand(ServerRequest request,ServerResponse response){
        //System.out.println("Name "+request.path().param("name"));// + "Org code "+request.path().param("org-code"));
        //https://edrx-test.fa.us2.oraclecloud.com/fscmRestApi/resources/11.13.18.05/onhandQuantityDetails?
        // onlyData=true&q=ItemNumber="FILTER, OIL";OrganizationCode=RAK_MAINT
        String name = request.path().param("name");
        System.out.println("Name "+name);
        String [] param = name.split(";;");
        System.out.println("param "+param[0]+" && "+param[1]);
        try {
            WebClient webclient = WebClient.builder()
                    .baseUri("https://edrx-test.fa.us2.oraclecloud.com/fscmRestApi/resources/11.13.18.05/onhandQuantityDetails")
                    .addMediaSupport(JsonpSupport.create())
                    .addHeader("Authorization", "Basic U0NNVVNFUjpPcmFjbGUxMjM=")
                    .build();
            System.out.println("Webclient " + webclient);
            JsonObject content = webclient
                    .get()
                    .queryParam("onlyData", "true")
                    //.queryParam("q","ItemNumber=IBM1")
                    .queryParam("q", "ItemNumber=\""+param[0]+"\";OrganizationCode=\""+param[1]+"\"")
                    .request(JsonObject.class).get();
            System.out.println("Json content :: " + content);
            JsonArray itemArray = content.getJsonArray("items");
            System.out.println("Array content :: " + itemArray);
            sendResponse(response, itemArray);
        }
        catch (Exception e){
            System.out.println("Exception in onhand "+e);
        }
    }
    private void sendResponse(ServerResponse response, String name) {
        String msg = String.format("%s %s!", greeting.get(), name);

        JsonObject returnObject = JSON.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }

    /**
     * Overloaded sendResponse for sending back saas response.
     * @param response
     * @param jArray
     */
    private void sendResponse(ServerResponse response, JsonArray jArray) {
        //String msg = String.format("%s %s!", greeting.get(), name);
        JsonObjectBuilder returnObjectBuilder = Json.createObjectBuilder();
        /*JsonObject returnObject = JSON.createObjectBuilder()
                .add("message", msg)
                .build();*/
        returnObjectBuilder.add("items", jArray);
        JsonObject returnObject = returnObjectBuilder.build();

        response.send(returnObject);
    }

    private static <T> T processErrors(Throwable ex, ServerRequest request, ServerResponse response) {

         if (ex.getCause() instanceof JsonException){

            LOGGER.log(Level.FINE, "Invalid JSON", ex);
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                .add("error", "Invalid JSON")
                .build();
            response.status(Http.Status.BAD_REQUEST_400).send(jsonErrorObject);
        }  else {

            LOGGER.log(Level.FINE, "Internal error", ex);
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                .add("error", "Internal error")
                .build();
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send(jsonErrorObject);
        }

        return null;
    }

    private void updateGreetingFromJson(JsonObject jo, ServerResponse response) {
        if (!jo.containsKey("greeting")) {
            JsonObject jsonErrorObject = JSON.createObjectBuilder()
                    .add("error", "No greeting provided")
                    .build();
            response.status(Http.Status.BAD_REQUEST_400)
                    .send(jsonErrorObject);
            return;
        }

        greeting.set(jo.getString("greeting"));
        response.status(Http.Status.NO_CONTENT_204).send();
    }

    /**
     * Set the greeting to use in future messages.
     * @param request the server request
     * @param response the server response
     */
    private void updateGreetingHandler(ServerRequest request,
                                       ServerResponse response) {
        request.content().as(JsonObject.class)
            .thenAccept(jo -> updateGreetingFromJson(jo, response))
            .exceptionally(ex -> processErrors(ex, request, response));
    }
}