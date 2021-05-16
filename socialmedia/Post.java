package socialmedia;

import java.io.Serializable;

/**
 * Class to represent a Post in the system. Implements serializable so that the system is able to be saved
 */
public class Post implements Serializable {
    private int id;
    private int userid;
    private String handle;
    private String content;
    private int endorsements; // Number of endorsements of the post
    private int comments; // Number of comments of the comment

    public Post(int id, String content, int userid){
        setId(id);
        setContent(content);
        setUserid(userid);
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getEndorsements() {
        return endorsements;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public int getComments() {
        return comments;
    }

    /**
     * Method to increment the endorsement count
     */
    public void incrementEndorsements(){
        this.endorsements++;
    }

    /**
     * Method to decrement the endorsement count
     */
    public void decrementEndorsements(){
        this.endorsements--;
    }

    /**
     * Method to increment the comments count
     */
    public void incrementComments(){
        this.comments++;
    }

    @Override
    public String toString() {
        return "ID: " + id +"\nAccount: " + handle + "\nNo. endorsements: " + endorsements + " | No. comments: " + comments + "\n" + content;
    }
}
