package ng.com.hybrid.followup.Model;

public class UserModel {

   private String userid;
   private String displayname;
   private String dob;
   private String userstate;
   private String usergender;
   private String firstname;
   private String lastname;
   private String profileimage;

    public UserModel() {
    }

    public UserModel(String displayname, String dob, String userstate, String usergender, String firstname, String lastname) {
        this.displayname = displayname;
        this.dob = dob;
        this.userstate = userstate;
        this.usergender = usergender;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserstate() {
        return userstate;
    }

    public void setUserstate(String userstate) {
        this.userstate = userstate;
    }

    public String getUsergender() {
        return usergender;
    }

    public void setUsergender(String usergender) {
        this.usergender = usergender;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
