package edu.lehigh.cse216.mlc325.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

// Import Google's JSON library
import com.google.gson.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import org.apache.log4j.*;


import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;



public class App {
    // This logger is used across the App class, providing debug and other useful information in the Heroku log
    final private static Logger logger = LogManager.getLogger(App.class);
    public static void main(String[] args) {
        // Basic Configuration for log4j logger
        BasicConfigurator.configure();
        // Retrieves a key value map of Config Vars from Heroku
        Map<String, String> env = System.getenv();
        // Receives Postgres Database URL from Heroku Config Vars (found in settings of Heroku app)
        String db_url = env.get("DATABASE_URL");

        //key generated session id, value is google user id
        // This will be replaced with a caching solution 
        Hashtable<Integer, String> usersHT = new Hashtable<>();
        usersHT.put(-1, "107590165278581716154"); // TODO remove later, for testing purposes only

        final String CLIENT_ID_1 = "429689065020-h43s75d9jahb8st0jq8cieb9bctjg850.apps.googleusercontent.com";
        final String CLIENT_ID_2 = "429689065020-f2b4001eme5mmo3f6gtskp7qpbm8u5vv.apps.googleusercontent.com";
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2)).build();

        // Get a fully-configured connection to the database, or exit immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // gson provides us with a way to turn JSON into objects, and objects into JSON.
        // NB: it must be final, so that it can be accessed from our lambdas
        // NB: Gson is thread-safe.  See 
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));

        // Set up the location for serving static files.  If the STATIC_LOCATION
        // environment variable is set, we will serve from it.  Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        // Set up the location for serving static files
        Spark.staticFileLocation("/web");

        String cors_enabled = env.get("CORS_ENABLED");

        if ("True".equalsIgnoreCase(cors_enabled)) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }

        // // Set up a route for serving the main page
        // Spark.get("/", (req, res) -> {
        //     res.redirect("/index.html");
        //     return "";
        // });

        // GET route that returns all message titles and Ids.  All we do is get 
        // the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/messages", (request, response) -> {
            int sesId;
            try {
                sesId = Integer.parseInt(request.headers("Session-ID"));
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "could not get sessionID, parse error on " + request.headers("Session-ID"), null));
            }
            if(!usersHT.containsKey(sesId)){
                return gson.toJson(new StructuredResponse("error", "invalid user session: " + sesId, null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            ArrayList<Database.PostData> posts = db.selectAllPosts();
            if(posts==null){
                return gson.toJson(new StructuredResponse("error", "no posts selected", null));
            }
            return gson.toJson(new StructuredResponse("ok", null, posts));
        });

        // GET route that returns everything for a single row in the database.
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested row ID.  If 
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            int sesId;
            try {
                sesId = Integer.parseInt(request.headers("Session-ID"));
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "could not get sessionID, parse error on " + request.headers("Session-ID"), null));
            }
            if(!usersHT.containsKey(sesId)){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // Pulling record from ideas table
            Database.PostData message = db.selectOnePost(idx);
            if (message == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, message));
            }
        });

        //get all comments and single post from post id
        Spark.get("/comments/:id", (request, response) -> {
            int idx;
            try {
                idx = Integer.parseInt(request.params("id"));
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "could not parse int from route", null));
            }
            int sesId;
            sesId = Integer.parseInt(request.headers("Session-ID"));
            if(!usersHT.containsKey(sesId)){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            } 
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectPostComments(idx)));
        });

        // GET route that returns someone's profile information
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested user ID.
        Spark.get("/profile/:id", (request, response) -> {
            int sesId;
            try {
                sesId = Integer.parseInt(request.headers("Session-ID"));
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "could not get sessionID, parse error on " + request.headers("Session-ID"), null));
            }
            if(!usersHT.containsKey(sesId)){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            String id = request.params("id");
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            Database.ProfileData data = db.selectOneProfile(id);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", id + " not found", null));
            } else {
                data.mGI = data.mSO = ""; //delete hidden data
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });
        
        // GET route that returns your profile information
        Spark.get("/profile", (request, response) -> {
            int sesId;
            try {
                sesId = Integer.parseInt(request.headers("Session-ID"));
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "could not get sessionID, parse error on " + request.headers("Session-ID"), null));
            }
            String id = usersHT.get(sesId);
            if(id==null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            Database.ProfileData data = db.selectOneProfile(id);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", id + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });
        
        // Put route that updates someone's own profile information
        Spark.put("/profile", (request, response) -> {
            ProfileRequest req = gson.fromJson(request.body(), ProfileRequest.class);
            String id = usersHT.get(req.mSessionId);
            if(id==null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            //ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result;
            try {
                result = db.updateOneProfile(id, req.mGI, req.mSO, req.mUsername, req.mNote);
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "error updating", null));
            }
            if (result<0) {
                return gson.toJson(new StructuredResponse("error", id + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        // POST route for adding a new element to the database.  This will read
        // JSON from the body of the request, turn it into a IdeaRequest 
        // object, extract the title and message, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/messages", (request, response) -> {
            String link = "";
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server Error
            IdeaRequest req = gson.fromJson(request.body(), IdeaRequest.class);
            String userId = usersHT.get(req.mSessionId);
            if(userId==null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that describes the error.
            response.status(200);
            response.type("application/json");
            // NB: createEntry checks for null title and message
            // TODO: Caching file into database?
            if(req.mBase64Image != null) {
                link = GoogleDriveUpload(req.mBase64Image, env.get("GOOGLE_SERVICE_ACCOUNT_SECRET"));
            }
            int postid;
            if(link == "") {
                postid = db.insertRowIdea(req.mTitle, req.mMessage, userId, null);
            } else {
                postid = db.insertRowIdea(req.mTitle, req.mMessage, userId, link);
                db.insertRowLink(userId, postid, new Date().toString());
            }
            if (postid == -1) {
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + postid, link));
            }
        });
        
        // POST route for retreving the user token
        Spark.post("/signin", (request, response) -> {
            TokenRequest req = gson.fromJson(request.body(), TokenRequest.class);
            String tokenString = req.mToken;
            Map<String,String> map = new HashMap<String, String>();
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            GoogleIdToken idToken = verifier.verify(tokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                
                //User identifier
                String userId = payload.getSubject();
                
                // Get profile information from payload
                String email = payload.getEmail();
                //boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                //String pictureUrl = (String) payload.get("picture");
                //String locale = (String) payload.get("locale");
                // String familyName = (String) payload.get("family_name");
                // String givenName = (String) payload.get("given_name");
                if(db.selectOneProfile(userId)==null){
                    db.insertRowProfile(userId, "", "", email, name, "");
                }
                
                if(!db.safeUser(userId)){
                    // if(db.selectOneProfile(userId)==null){
                        //     return gson.toJson(new StructuredResponse("error", "Could not find userid: " + userId, null));
                        // }
                        return gson.toJson(new StructuredResponse("error", "User blocked by administrator", null));
                    }
                    
                    Integer userSession = (int)(Math.random()*Integer.MAX_VALUE);
                    while(usersHT.containsKey(userSession)){ //make sure session ID is unique
                        userSession = (int)(Math.random()*Integer.MAX_VALUE);
                    }
                    usersHT.put(userSession, userId);
                    map.put("User-ID", userId);
                    map.put("Session-ID", userSession.toString());
                    return gson.toJson(new StructuredResponse("ok", "Signed in " + name, map));
            } else {
                return gson.toJson(new StructuredResponse("error", "user could not be verified", null));
            }
        });
        
        // POST route for adding a new comment to the database.
        Spark.post("/comment/:id", (request, response) -> {
            // postId pulled from /comment/:id path
            int postId = Integer.parseInt(request.params("id"));
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server Error
            CommentRequest req = gson.fromJson(request.body(), CommentRequest.class);
            String link = "";
            String userId = usersHT.get(req.mSessionId);
            if(userId==null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that describes the error.
            response.status(200);
            response.type("application/json");
            if(req.mBase64Image != null) {
                link = GoogleDriveUpload(req.mBase64Image, env.get("GOOGLE_SERVICE_ACCOUNT_SECRET"));
                //db.insertRowLink(userId, )
            }
            int result;
            try {
                result = db.insertRowComment(userId, postId, req.mComment, link);
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error", "error inserting comment", null));
            }
            if (result == 0) {
                return gson.toJson(new StructuredResponse("error", "problem inserting comment", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + result, link));
            }
        });

        // PUT route for modifying a comment from its comment id
        Spark.put("/comment", (request, response) -> {
            CommentRequest req;
            String userId;
            String link = "";
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server Error
            try {
                req = gson.fromJson(request.body(), CommentRequest.class);
                userId = usersHT.get(req.mSessionId);
                if(userId==null){
                    return gson.toJson(new StructuredResponse("error", "invalid user session", null));
                }
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error","error getting and validating request: " + request.body(),  null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            //     describes the error.
            response.status(200);
            response.type("application/json");
            int result;
            try {
                if(req.mBase64Image != null) {
                    link = GoogleDriveUpload(req.mBase64Image, env.get("GOOGLE_SERVICE_ACCOUNT_SECRET"));
                }
                if(link == ""){
                    result = db.updateOneComment(req.mCommentId, req.mComment, null, 0);
                } else{
                    result = db.updateOneComment(req.mCommentId, req.mComment, link, 0);
                }
                
            } catch (Exception e) {
                return gson.toJson(new StructuredResponse("error","error updating",  null));
            }
            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "error updating comment: " + req.mCommentId, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + result, link));
            }
        });

        // PUT route for updating a row in the database.  This is almost 
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            IdeaRequest req = gson.fromJson(request.body(), IdeaRequest.class);
            if(!usersHT.containsKey(req.mSessionId)){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int votes = db.selectOnePost(idx).mVotes; //keep the votes the same
            int result = db.updateOneIdea(idx, req.mMessage, votes);
            if (result < 0) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        Spark.put("/messages/:id/upvote", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SessionRequest req = gson.fromJson(request.body(), SessionRequest.class);
            String userId = usersHT.get(req.mSessionId);
            if(userId == null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result;
            Database.UserVotesData vote = db.selectOneVote(idx, userId);
            if(vote==null){ //not voted yet
                result = db.like(idx,1);
                db.insertRowVote(idx, userId, 1);
            }else{
                if(vote.mVotes==-1){//downvoted
                    result = db.like(idx, 2);
                    db.updateOneVote(idx, userId, 1);
                }else{ //1, already upvoted
                    db.deleteRowVote(idx, userId);
                    result = db.dislike(idx, 1); //undo vote
                }
            }
            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        Spark.put("/messages/:id/downvote", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SessionRequest req = gson.fromJson(request.body(), SessionRequest.class);
            String userId = usersHT.get(req.mSessionId);
            if(userId == null){
                return gson.toJson(new StructuredResponse("error", "invalid user session", null));
            }
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result;
            Database.UserVotesData vote = db.selectOneVote(idx, userId);
            if(vote==null){ //not voted yet
                result = db.dislike(idx,1);
                db.insertRowVote(idx, userId, -1);
            }else{
                if(vote.mVotes==1){//upvoted
                    result = db.dislike(idx, 2);
                    db.updateOneVote(idx, userId, -1);
                }else{ //-1, already downvoted
                    db.deleteRowVote(idx, userId);
                    result = db.like(idx,1);//undo vote
                }
            }
            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

    }

    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * 
     * @envar      The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends.  This only needs to be called once.
     * 
     * @param origin The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any 
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
    private static String GoogleDriveUpload(String mBase64Image, String service_account_info) throws Exception {
        logger.info("Decoding and uploading Base64 File...");
        String[] fileParams = mBase64Image.split("-");
        // Decoding Base64 file from HTTP Request
        byte[] fileBytes = Base64.getDecoder().decode(fileParams[2]);
        java.io.File filePath = new java.io.File("files/" + fileParams[1]);
        // saving file content to files/___.___
        FileUtils.writeByteArrayToFile(filePath, fileBytes);
        // Building new authorized API client service for Google Drive
        InputStream google_service_secret = new ByteArrayInputStream(service_account_info.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(google_service_secret).createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        Drive service = new Drive.Builder(new NetHttpTransport(), 
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive Upload")
            .build();
        logger.info("JSON of current files in service account: \n\n" + service.files().list().execute());
        // Upload File to Drive
        File fileMetadata = new File();
        fileMetadata.setName(fileParams[1]); // What the file will be stored as
        FileContent mediaContent = new FileContent(fileParams[0], filePath);
        try { 
            File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();
            // Creating permission for anyone to read file at webViewLink
            Permission newPermission = new Permission();
            newPermission.setType("anyone");
            newPermission.setRole("reader");
            service.permissions().create(file.getId(), newPermission).execute();
            logger.info("File ID: " + file.getId());
            logger.info("Link: "+ file.getWebViewLink());
            return file.getWebViewLink();
        } catch(GoogleJsonResponseException e) {
            logger.error("Unable to upload file: " + e.getDetails());
        }
        return ""; // Returns empty string if failed
    }
}