package edu.lehigh.cse216.mlc325.backend;

    /**
     * IdeaRequest object used to convert incoming JSON requests from frontend 
     */
public class IdeaRequest {
    /**
     * The user-defined title being provided by the client.
     */
    public String mTitle;

    /**
     * The user-defined message being provided by the client.
     */
    public String mMessage;

    /**
     * An optional base64-encoded file to be uploaded to Google Drive.
     * If this attribute is present in the request the response will have a 
     * corresponding Drive link.
     */
    public String mBase64Image;

    /**
     * The user-defined (optional) link being provided by the client.
     */
    public String mLink;

    /**
     * The user's session ID provided by the client.
     */
    public int mSessionId;
}