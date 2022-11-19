package edu.lehigh.cse216.mlc325.backend;

    /**
     * CommentRequest object used to convert incoming JSON requests from frontend
     */
public class CommentRequest {
    /**
     * The post ID that is being commented on, provided by the client.
     */
    public int mCommentId;

    /**
     * The user-defined comment being provided by the client.
     */
    public String mComment;

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