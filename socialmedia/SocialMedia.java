package socialmedia;

import java.io.*;
import java.util.ArrayList;

public class SocialMedia implements SocialMediaPlatform{

    private ArrayList<Account> accounts = new ArrayList<>(); //Array list for accounts in the system
    private ArrayList<Post> posts = new ArrayList<>(); //Array list for posts in the system

    /**
     * Method to show the comments of a given post
     * @param id of the post to show the comments of
     * @param recursionNum how deep into the recursion the post is
     * @return StringBuilder containing the information of the comments of a given post with post ID 'id'.
     */

    public StringBuilder showComments(int id, int recursionNum){

        StringBuilder postChildrenDetails = new StringBuilder(); //Creates a StringBuilder for the comments.

        for(int i = 0; i < posts.size(); i++){
            // for each of the posts.
            if(posts.get(i) instanceof Comment){ //Check if the post is a comment.
                if(((Comment) posts.get(i)).getReference() == id){ //Checks if the comment is a comment on the original post (given by ID 'id').
                    // Appends the information about the comments of the post onto the StringBuilder.
                    // "ID: " + id +"\nAccount: " + handle + "\nNo. endorsements: " + endorsements + " | No. comments: " + comments + "\n" + content;
                    postChildrenDetails.append("    ".repeat(recursionNum-1) + "| > ID: " + posts.get(i).getId() + "\n");
                    postChildrenDetails.append("    ".repeat(recursionNum) + "Account: " + posts.get(i).getHandle() + "\n");
                    postChildrenDetails.append("    ".repeat(recursionNum) + "No. endorsements: " + posts.get(i).getEndorsements() + " | No. comments: " + posts.get(i).getComments() + "\n");
                    postChildrenDetails.append("    ".repeat(recursionNum) + posts.get(i).getContent() + "\n");
                    // The code '"    ".repeat(recursionNum)' adds the indentation for each set of comments on a post.
                    if (posts.get(i).getComments() > 0){
                        // if the comment has comments
                        postChildrenDetails.append("    ".repeat(recursionNum) + "| \n");
                        postChildrenDetails.append(showComments(posts.get(i).getId(), recursionNum+1)); // Calls itself to get the information on the comments of the comments.
                    }
                }
            }
        }
        return postChildrenDetails; //returns StringBuilder
    }

    /**
     * Method to return if a given handle is unique
     * @param handle to check if unique
     * @return boolean stating if the handle is unique or not
     */
    public boolean isHandleUnique(String handle){
        boolean isUnique = true;
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){ //Checks if indexed handle is equal to entered handle
                isUnique = false; //If so then the handle is not unique as it already exists in the system
            }
        }
        return isUnique;
    }

    /**
     * Method to determine if a post is valid
     * @param content to be checked if valid
     * @return boolean indicating if content is valid
     */
    public static boolean isPostValid(String content){
        boolean isValid = true;
        if(content.isEmpty() || content.length() > 100){ //Checks if content is empty or contains more than 100 characters
            isValid = false;
        }
        return isValid;
    }

    @Override
    public int createAccount(String handle) throws IllegalHandleException, InvalidHandleException {
        int id = 0;

        if(!isHandleUnique(handle)){ //Checks if the handle is unique
            throw new IllegalHandleException();
        }

        if(accounts.size() > 0){
            /*Assigns the id of the new account by getting the id of the last account in the list of accounts and
            * adding one to it to create the new account id. If the size of the accounts array list is 0 then the
            * id of the new account is set to be 0.*/
            id = accounts.get(accounts.size() - 1).getId() + 1;
        }

        Account newAccount = new Account(handle, id); //Creates a new account using given handle and id
        accounts.add(newAccount); //Adds new account to the array list of accounts
        return newAccount.getId();
    }

    @Override
    public void removeAccount(int id) throws AccountIDNotRecognisedException {
        Account removedAccount = null;

        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getId() == id){ //Checks if indexed account id is equal to id entered
                removedAccount = accounts.get(i); //If so set account being deleted to the indexed account
            }
        }

        if(removedAccount == null){ //Checks if an account has been found
            throw new AccountIDNotRecognisedException();
        }

        for(Post post : new ArrayList<>(posts)){ //Creates a copy of posts to iterate over, copy is needed as posts are being removed from the array list in the loop
            if(post.getHandle().equals(removedAccount.getHandle())){ //Checks if the handle of the indexed post is equal to the handle of the account being deleted
                try{
                    deletePost(post.getId()); //Calls the delete post method on indexed post
                } catch (PostIDNotRecognisedException e) {
                    //Catches exception and ignores it as it means it has already been deleted
                }
            }
        }

        accounts.remove(removedAccount); //Removes the given account from the array list of accounts

    }

    @Override
    public void changeAccountHandle(String oldHandle, String newHandle) throws HandleNotRecognisedException, IllegalHandleException, InvalidHandleException {
        Account account = null;
        boolean handleRecognised = false;

        if(!isHandleUnique(newHandle)){ //Checks if handle is unique
            throw new IllegalHandleException();
        }

        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(oldHandle)){ //Checks if the indexed accounts handle is equal to the old handle
                account = accounts.get(i);
                handleRecognised = true;
                account.setHandle(newHandle); //Sets the account handle of the indexed account to the new handle
                break;
            }
        }
        if(!handleRecognised){ //If the account with the given old handle has not been recognised, throw an exception
            throw new HandleNotRecognisedException();
        }

        //Code to change handle associated with all posts of the user
        for(Post post : posts){
            if(post.getUserid() == account.getId()){ //Checks if the id of the post is equal to the id of the account
                post.setHandle(newHandle); //Sets the handle of the post to the new handle
            }
        }
    }

    @Override
    public String showAccount(String handle) throws HandleNotRecognisedException {
        boolean handleRecognised = false;
        Account currentAccount = null;
        for(int i = 0; i < accounts.size(); i++){
            currentAccount = accounts.get(i); //Gets the indexed account
            if(currentAccount.getHandle().equals(handle)){ //Checks if the handle of the indexed account is equal to the handle provided
                handleRecognised = true;
                break;
            }
        }
        if(!handleRecognised){ //If an account with the given handle is not in the Accounts array list throw an exception
            throw new HandleNotRecognisedException();
        }
        return currentAccount.toString();
    }

    @Override
    public int createPost(String handle, String message) throws HandleNotRecognisedException, InvalidPostException {
        Account posterAccount = null;
        boolean handleRecognised = false;
        int postId = 0;

        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){ //Checks if the handle of the indexed account is equal to the handle provided
                handleRecognised = true;
                posterAccount = accounts.get(i);
            }
        }

        if(!handleRecognised){ //If an account with the given handle is not in the Accounts array list throw an exception
            throw new HandleNotRecognisedException();
        }

        if(!isPostValid(message)){ //Checks if the post is valid
            throw new InvalidPostException();
        }

        if(posts.size() > 0){
            //If there are posts in the system already then get the id of the last post in the array list and add one to it to create the new post id
            postId = posts.get(posts.size() - 1).getId() + 1;
        }

        Post newPost = new Post(postId, message, posterAccount.getId()); //Creates a new post object with data
        newPost.setHandle(handle); //Sets the handle of the new post

        posts.add(newPost); //Adds the new post to the array list of posts
        posterAccount.incrementPostCount(); //Increments post count of the user creating the post

        return newPost.getId();
    }

    @Override
    public int endorsePost(String handle, int id) throws HandleNotRecognisedException, PostIDNotRecognisedException, NotActionablePostException {
        int endorsedUserId = 0;
        int endorsementId = 0;
        boolean handleRecognised = false;
        boolean postIdRecognised = false;
        boolean previouslyEndorsed = false;
        String content;
        Account endorsingAccount = null;
        Endorsement newEndorsement = null;
        Post endorsingPost = null;

        //Gets the account of the user associated with the handle entered
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){
                handleRecognised = true;
                endorsingAccount = accounts.get(i);
            }
        }

        //Gets the post being endorsed
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getId() == id){
                postIdRecognised = true;
                endorsingPost = posts.get(i);
            }
        }

        //Checks if the post has already been endorsed by the user
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i) instanceof Endorsement){
                if(((Endorsement) posts.get(i)).getReference() == id && posts.get(i).getUserid() == endorsingAccount.getId()){
                    previouslyEndorsed = true;
                }
            }
        }


        if(!handleRecognised){
            throw new HandleNotRecognisedException(); // if handle is not recognised, a HandleNotRecognisedException is thrown.
        }

        if(!postIdRecognised){
            throw new PostIDNotRecognisedException(); // if post id is not recognised, a PostIDNotRecognisedException is thrown.
        }

        if(endorsingPost instanceof Endorsement || previouslyEndorsed || endorsingPost.getUserid() == -1){ // if post is already an endorsement OR The post has been previously endorsed OR the post has been deleted, a NotActionablePostException is thrown.
            throw new NotActionablePostException();
        }

        if(posts.size() > 0){
            endorsementId = posts.get(posts.size() - 1).getId() + 1; // set for for new endorsement/
        }

        content = endorsingAccount.getHandle() + " : " + endorsingPost.getContent(); // Creates the content of the endorsement.

        endorsingAccount.incrementPostCount(); // Increments the post count of the account endorsing.

        newEndorsement = new Endorsement(endorsementId, content, endorsingAccount.getId(), id); // Creates a new endorsement.

        newEndorsement.setHandle(handle); // Sets the endorsement handle to be the handle of the user.

        posts.add(newEndorsement); // Adds the endorsement to the array list of posts.

        endorsedUserId = endorsingPost.getUserid(); // Gets the id of the user who's post is being endorsed.

        // Code to increment the endorse count of the user who's post is endorsed.
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getId() == endorsedUserId){
                accounts.get(i).incrementEndorseCount(); // Increments the endorse count on the profile.
            }
        }

        endorsingPost.incrementEndorsements(); // Increments the endorse count of the post.

        return newEndorsement.getId();
    }

    @Override
    public int commentPost(String handle, int id, String message) throws HandleNotRecognisedException, PostIDNotRecognisedException, NotActionablePostException, InvalidPostException {
        int postId = 0;
        boolean postRecognised = false;
        boolean handleRecognised = false;
        Account account = null;
        Post post = null;


        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){ // Checks if the handle of the indexed account is equal to the handle provided.
                handleRecognised = true;
                account = accounts.get(i); // Gets the account of the person creating the comment.
            }
        }

        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getId() == id){ // Checks if the id of the post is equal to the given post id.
                postRecognised = true;
                post = posts.get(i); // Gets the post being referenced by the comment.
            }
        }

        if(!handleRecognised){ // Checks if the handle is associated with an account in the array list
            throw new HandleNotRecognisedException();
        }

        if(!postRecognised){ // Checks if the post being referenced is in the array list.
            throw new PostIDNotRecognisedException();
        }

        if(!isPostValid(message)){ // Checks if the post content is valid or not.
            throw new InvalidPostException();
        }

        if(post instanceof Endorsement || post.getUserid() == -1){ // Checks if post is an endorsement or is a deleted post.
            throw new NotActionablePostException();
        }

        if(posts.size() > 0){
            // If there are posts in the system already then get the id of the last post in the array list and add one to it to create the new post id.
            postId = posts.get(posts.size() - 1).getId() + 1;
        }

        Comment newComment = new Comment(message, account.getId(), id, postId); //Creates a new comment using the data
        newComment.setHandle(handle);
        posts.add(newComment); // Adds the comment to the list of all Posts.

        // Code to increment number of comments of every previous post linked to the comment.
        Post currentPost = newComment;
        while(currentPost instanceof Comment){ // Repeats until the current post is not a comment.
            int reference = ((Comment) currentPost).getReference(); // Gets the reference ID of the post that the comment is pointing to.
            for(int i = 0; i < posts.size(); i++){
                if(posts.get(i).getId() == reference){ // Checks if the indexed post has the same ID as the post that the comment references.
                    currentPost = posts.get(i); // If so then set the current post to be the post that the comment references.
                    currentPost.incrementComments(); // Increments the comments counter for the current post.
                }
            }
        }

        account.incrementPostCount(); // Increments the post count.

        return newComment.getId();
    }


    @Override
    public void deletePost(int id) throws PostIDNotRecognisedException {
        Post post = null;

        // Code to get the post being deleted given the id of the post.
        for(Post selectedPost : new ArrayList<>(posts)){
            if(selectedPost.getId() == id){ // Checks if the id of the indexed post is equal to the id provided.
                post = selectedPost; // Sets the post to be deleted as the selected post.
            }
        }

        if(post == null){ // Checks if post has been found
            throw new PostIDNotRecognisedException();
        }


        // Code to decrement post count of account which made the post
        for(Account account : new ArrayList<>(accounts)){
            if(account.getId() == post.getUserid()){
                account.decrementPostCount();
            }
        }

        // Code to account for if the post being deleted is an endorsement and to reduce endorsement counts
        if(post instanceof Endorsement){

            Post referencedPost = null;
            int referencedPostId = ((Endorsement) post).getReference(); // Gets the id of the post being referenced by the endorsement

            // Code to get post being endorsed and reduce the number of endorsements by 1
            for(Post endorsedPost : new ArrayList<>(posts)){
                if(endorsedPost.getId() == referencedPostId){ // Checks if post id is equal to referenced id
                    referencedPost = endorsedPost; // Sets the referenced post as the current indexed post
                    referencedPost.decrementEndorsements();
                }
            }

            // Code to get account of endorsed post and reduce endorsement count by 1
            if(!(referencedPost == null)){
                for(Account endorsedAccount : new ArrayList<>(accounts)){
                    if(endorsedAccount.getId() == referencedPost.getUserid()){
                        endorsedAccount.decrementEndorseCount();
                    }
                }
            }
        }

        // Code to delete all endorsements of a post being deleted
        for(Post endorsement : new ArrayList<>(posts)){
            if(endorsement instanceof Endorsement){ //Code to remove all endorsements of the post being deleted
                if(((Endorsement) endorsement).getReference() == post.getId()){
                    int endorsedUserId = 0;

                    post.decrementEndorsements(); //Decrements number of endorsements of the post

                    int endorsingUserId = endorsement.getUserid(); //Gets user id of the user who endorsed the post
                    posts.remove(endorsement); //Deletes the endorsement
                    for(Account endorsingUser : new ArrayList<>(accounts)){ //Code to get the account of the endorsing user
                        if(endorsingUser.getId() == endorsingUserId){
                            endorsingUser.decrementPostCount(); //Reduces their post count by 1
                        }
                    }

                    //Code to get ID of user who's post was endorsed
                    for(Post endorsedPost : new ArrayList<>(posts)){
                        if(endorsedPost.getId() == ((Endorsement) endorsement).getReference()){
                            endorsedUserId = endorsedPost.getUserid();
                        }
                    }

                    //Gets users account and decrements their endorsement count
                    for(Account endorsed : new ArrayList<>(accounts)){
                        if(endorsed.getId() == endorsedUserId){
                            endorsed.decrementEndorseCount();
                        }
                    }
                }
            }
        }

        //Code to change the post being deleted into a placeholder post
        post.setUserid(-1); //Sets the post being deleted user id to -1 so that it is not associated with any account
        post.setHandle("NONE"); //Sets the handle displayed of the deleted post to be NONE
        post.setContent("The original content was removed from the system and is no longer available.");

    }

    @Override
    public String showIndividualPost(int id) throws PostIDNotRecognisedException {
        Post post = null;
        boolean postRecognised = false;
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getId() == id){ //Checks if indexed post id is equal to the one entered
                postRecognised = true;
                post = posts.get(i); //Gets the post to be shown
                break;
            }
        }

        if(!postRecognised){ //Checks if the post id is associated with a post in the system
            throw new PostIDNotRecognisedException();
        }
        return post.toString();
    }

    @Override
    public StringBuilder showPostChildrenDetails(int id) throws PostIDNotRecognisedException, NotActionablePostException {
        StringBuilder postChildrenDetails = new StringBuilder();

        Post post = null;
        boolean postRecognised = false;
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getId() == id){ //Checks if indexed post id is equal to the one entered
                postRecognised = true;
                post = posts.get(i); //Gets the post to be shown
                break;
            }
        }

        if(!postRecognised){ //Checks if the post id is associated with a post in the system
            throw new PostIDNotRecognisedException();
        } else if(post instanceof Endorsement){ //Checks if the post given is an endorsement
            throw new NotActionablePostException(); //If it is then throw an error
        }

        postChildrenDetails.append(post.toString()  + "\n"); //Adds the original post to the string builder

        int postComments = post.getComments(); //Gets the number of comments of the original post
        if (postComments > 0){
            postChildrenDetails.append("| \n");
            postChildrenDetails.append(showComments(id, 1));
        }

        return postChildrenDetails;
    }

    @Override
    public int getMostEndorsedPost() {
        Post mostEndorsed = null;
        int highestEndorsement = -1;
        //Increments over every post and finds the post with the most endorsements
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getEndorsements() > highestEndorsement && posts.get(i).getUserid() != -1){ //Checks if the number of endorsements of the post is greater than the highest endorsements
                //Also checks if the user id of the post is -1 to check if the post is a deleted post
                highestEndorsement = posts.get(i).getEndorsements(); //Sets the highest number of endorsements to the number of endorsements of the post
                mostEndorsed = posts.get(i);
            }
        }
        return mostEndorsed.getId();
    }

    @Override
    public int getMostEndorsedAccount() {
        Account mostEndorsed = null;
        int highestEndorsement = -1;
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getEndorseCount() > highestEndorsement){ //Checks if the indexed accounts endorsement count is higher than current highest
                mostEndorsed = accounts.get(i); //If so then set new highest account and new highest endorsement amount
                highestEndorsement = accounts.get(i).getEndorseCount();
            }
        }
        return mostEndorsed.getId();
    }

    @Override
    public void erasePlatform() {
        accounts = new ArrayList<>(); //Clears the array list of accounts
        posts = new ArrayList<>(); //Clears the array list of posts
    }

    @Override
    public void savePlatform(String filename) throws IOException {
        //Creates a new file and object output stream to write to the file
        FileOutputStream fileOut = new FileOutputStream(filename);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(accounts); //Writes the array list of accounts to the file
        objectOut.writeObject(posts); //Writes the array list of posts to the file
        //Closes output streams
        objectOut.close();
        fileOut.close();
    }

    @Override
    public void loadPlatform(String filename) throws IOException, ClassNotFoundException {
        //Creates new file input and object input streams to load data from the file
        FileInputStream fileInput = new FileInputStream(filename);
        ObjectInputStream objectInput = new ObjectInputStream(fileInput);
        accounts = (ArrayList<Account>) objectInput.readObject(); //Reads the array list of accounts to the account variable
        posts = (ArrayList<Post>) objectInput.readObject(); //Reads the array list of posts to the account variable
        //Closes input streams
        objectInput.close();
        fileInput.close();
    }

    @Override
    public int createAccount(String handle, String description) throws IllegalHandleException, InvalidHandleException {
        int id = 0;

        if(!isHandleUnique(handle)){ //Checks if handle is unique
            throw new IllegalHandleException();
        }

        if(accounts.size() > 0){
            //If there are accounts in the system already then get the id of the last account in the array list and add one to it to create the new account id
            id = accounts.get(accounts.size() - 1).getId() + 1;
        }

        Account newAccount = new Account(handle, id, description); //Creates a new account with given data
        accounts.add(newAccount); //Adds the account to the array list of accounts

        return newAccount.getId();
    }

    @Override
    public void removeAccount(String handle) throws HandleNotRecognisedException {
        Account removedAccount = null;

        //Code to get the account being removed given the handle provided in the method
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){ //Checks if indexed account handle is equal to the handle entered
                removedAccount = accounts.get(i); //Gets the account to be removed
                break;
            }
        }

        if(removedAccount == null){ //Checks if an account with the given handle was found
            throw new HandleNotRecognisedException();
        }

        for(Post post : new ArrayList<>(posts)){ //Creates a copy of posts to iterate over
            if(post.getHandle().equals(removedAccount.getHandle())){ //Checks if the handle associated with the post is equal to the handle of the account being deleted
                try{
                    deletePost(post.getId()); //Calls the delete post method on the indexed post
                } catch (PostIDNotRecognisedException e) {
                    //Ignores exception as it just means that the post has already been deleted
                }
            }
        }

        accounts.remove(removedAccount); //Removes the account from the array list
    }

    @Override
    public void updateAccountDescription(String handle, String description) throws HandleNotRecognisedException {
        boolean isHandleRecognised = false;
        for(int i = 0; i < accounts.size(); i++){
            if(accounts.get(i).getHandle().equals(handle)){ //Checks if the indexed account handle matches handle provided
                isHandleRecognised = true;
                accounts.get(i).setDescription(description); //Sets the description of the account to the new description
                break;
            }
        }

        if(!isHandleRecognised){ //If an account with the given handle is not found then throw an exception
            throw new HandleNotRecognisedException();
        }
    }

    @Override
    public int getNumberOfAccounts() {
        return accounts.size(); //Gets the number of accounts in the accounts array list
    }

    @Override
    public int getTotalOriginalPosts() {
        int total = 0;
        for(int i = 0; i < posts.size(); i++){
            if(!(posts.get(i) instanceof Endorsement) && !(posts.get(i) instanceof Comment) && posts.get(i).getUserid() != -1){ //Checks if the post is not a comment or an endorsement
                //Also checks if the post is deleted by checking if the user id is equal to -1
                total++; //Increment total count
            }
        }
        return total;
    }

    @Override
    public int getTotalEndorsmentPosts() {
        int total = 0;
        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i) instanceof Endorsement && posts.get(i).getUserid() != -1){ //Checks if post is an endorsement and not a deleted post
                total++; //Increment total count
            }
        }
        return total;
    }

    @Override
    public int getTotalCommentPosts() {
        int total = 0;
        for(int i = 0; i < posts.size(); i++){
            if((posts.get(i) instanceof Comment) && posts.get(i).getUserid() != -1){ //Checks if the post is a comment and not a deleted post
                total++; //Increments total count
            }
        }
        return total;
    }
}
