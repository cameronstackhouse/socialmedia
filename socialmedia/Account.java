package socialmedia;

import java.io.Serializable;

/**
 * Class to represent a users account on the social media platform
 */
public class Account implements Serializable {
    private int id;
    private String handle;
    private String description;
    private int postCount; // Number of posts the user has made.
    private int endorseCount; // Number of endorsements of the users posts.

    public Account(String handle, int id) throws InvalidHandleException {
        setId(id);
        setHandle(handle);
        this.postCount = 0;
        this.endorseCount = 0;
    }

    public Account(String handle, int id, String description) throws InvalidHandleException {
        setId(id);
        setHandle(handle);
        setDescription(description);
        this.postCount = 0;
        this.endorseCount = 0;
    }

    //Getters and setters

    private void setId(int id){
        this.id = id;
    }

    public void setHandle(String name) throws InvalidHandleException {
        if(name.contains(" ") || name.isEmpty() || name.length() > 30){
            throw new InvalidHandleException(); //Throws an exception if the handle is invalid
        }
        this.handle = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getHandle() {
        return handle;
    }

    public int getId() {
        return id;
    }

    public int getEndorseCount() {
        return endorseCount;
    }


    /**
     * Method to increment the post count of the account
     */
    public void incrementPostCount(){
        this.postCount++;
    }

    /**
     * Method to decrement the post count of the account
     */
    public void decrementPostCount(){
        this.postCount--;
    }

    /**
     * Method to increment the endorse count of the account
     */
    public void incrementEndorseCount(){
        this.endorseCount++;
    }

    /**
     * Method to decrement the endorse count of the account
     */
    public void decrementEndorseCount(){
        this.endorseCount--;
    }

    @Override
    // toString method:
    public String toString() {
        return "ID: " + this.id + "\nHandle: " + this.handle + "\nDescription: " + description + "\nPost Count: " + postCount + "\nEndorse Count: " + endorseCount;
    }
}
