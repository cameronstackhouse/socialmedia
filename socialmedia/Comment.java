package socialmedia;

/**
 * Class to represent a comment in the system
 */
public class Comment extends Post{
    private int reference; //Id of the post the comment is commenting on

    public Comment(String content, int userId, int reference, int postId){
        super(postId, content, userId);
        setReference(reference);
    }

    //Getters and setters

    public void setReference(int reference) {
        this.reference = reference;
    }

    public int getReference() {
        return reference;
    }
}
