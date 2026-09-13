package prelim.group.model;


import prelim.exercises.MyDoublyLinkedList;
import prelim.exercises.MyList;


public class EmailCategory {
    private String categoryName;
    private MyList<Email> emailList;


    public EmailCategory(String categoryName) {
        this.categoryName = categoryName;
        this.emailList = new MyDoublyLinkedList<>();
    }


    public String getCategoryName() {
        return categoryName;
    }


    public MyList<Email> getEmailList() {
        return emailList;
    }


    public void addEmail(Email email) {
        try {
            emailList.insert(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addEmailToTop(Email email) {
        try {
            if (emailList instanceof MyDoublyLinkedList) {
                ((MyDoublyLinkedList<Email>) emailList).insertFirst(email);
            } else {
                emailList.insert(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean removeEmail(Email email) {
        return emailList.delete(email);
    }


    @Override
    public String toString() {
        return categoryName + " (" + emailList.getSize() + " emails)";
    }
}

