package socialmedia;

/**
 * Endorsement class to represent an endorsement in the system
 */
public class Endorsement extends Post{ // extends post
    private int reference; // id of the post the endorsement is referencing

    public Endorsement(int id, String content, int userid, int reference){
        super(id, content, userid);
        setReference(reference);
    }

    //Getters and setters
    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }
}
