package dairyhub_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // PERSONAL INFORMATION
    // =========================================

    private String name;


    @Column(
            unique = true,
            nullable = false
    )
    private String email;


    private String password;


    private String phone;


    private String gender;


    // =========================================
    // PROFILE PHOTO
    // =========================================

    /*
     * Stores the profile photo as Base64 data.
     *
     * It is nullable so existing users are not
     * forced to have a profile photo.
     */

    @Lob
    @Column(
            name = "profile_photo",
            columnDefinition = "LONGTEXT"
    )
    private String profilePhoto;


    // =========================================
    // DELIVERY ADDRESS
    // =========================================

    private String address;


    private String city;


    private String state;


    private String pincode;


    // =========================================
    // ROLE
    // =========================================

    private String role;


    // =========================================
    // ADMIN MANAGEMENT
    // =========================================

    /*
     * false = normal customer / protected admin
     *
     * true = customer promoted to ADMIN
     */

    @Column(
            name = "admin_managed",
            nullable = false
    )
    private Boolean adminManaged = false;


    // =========================================
    // DELETE BIN / ACCOUNT LOCK
    // =========================================

    /*
     * false = active account
     *
     * true = account is inside Delete Bin
     */

    @Column(
            nullable = false
    )
    private Boolean deleted = false;


    // =========================================
    // DELETE DATE
    // =========================================

    private LocalDateTime deletedAt;


    // =========================================
    // EMPTY CONSTRUCTOR
    // =========================================

    public User() {
    }


    // =========================================
    // FULL CONSTRUCTOR
    // =========================================

    public User(
            Long id,
            String name,
            String email,
            String password,
            String phone,
            String gender,
            String profilePhoto,
            String address,
            String city,
            String state,
            String pincode,
            String role,
            Boolean adminManaged) {

        this.id =
                id;

        this.name =
                name;

        this.email =
                email;

        this.password =
                password;

        this.phone =
                phone;

        this.gender =
                gender;

        this.profilePhoto =
                profilePhoto;

        this.address =
                address;

        this.city =
                city;

        this.state =
                state;

        this.pincode =
                pincode;

        this.role =
                role;

        this.adminManaged =
                adminManaged != null
                        ? adminManaged
                        : false;

        this.deleted =
                false;

        this.deletedAt =
                null;
    }


    // =========================================
    // ID
    // =========================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id =
                id;
    }


    // =========================================
    // NAME
    // =========================================

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name =
                name;
    }


    // =========================================
    // EMAIL
    // =========================================

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email =
                email;
    }


    // =========================================
    // PASSWORD
    // =========================================

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password =
                password;
    }


    // =========================================
    // PHONE
    // =========================================

    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone =
                phone;
    }


    // =========================================
    // GENDER
    // =========================================

    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender =
                gender;
    }


    // =========================================
    // PROFILE PHOTO
    // =========================================

    public String getProfilePhoto() {
        return profilePhoto;
    }


    public void setProfilePhoto(
            String profilePhoto) {

        this.profilePhoto =
                profilePhoto;
    }


    // =========================================
    // ADDRESS
    // =========================================

    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address =
                address;
    }


    // =========================================
    // CITY
    // =========================================

    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city =
                city;
    }


    // =========================================
    // STATE
    // =========================================

    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state =
                state;
    }


    // =========================================
    // PINCODE
    // =========================================

    public String getPincode() {
        return pincode;
    }


    public void setPincode(String pincode) {
        this.pincode =
                pincode;
    }


    // =========================================
    // ROLE
    // =========================================

    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role =
                role;
    }


    // =========================================
    // ADMIN MANAGED
    // =========================================

    /*
     * IMPORTANT:
     *
     * Use getAdminManaged(), NOT isAdminManaged().
     */

    public Boolean getAdminManaged() {
        return adminManaged;
    }


    public void setAdminManaged(
            Boolean adminManaged) {

        this.adminManaged =
                adminManaged != null
                        ? adminManaged
                        : false;
    }


    // =========================================
    // DELETED
    // =========================================

    public Boolean getDeleted() {
        return deleted;
    }


    public void setDeleted(
            Boolean deleted) {

        this.deleted =
                deleted != null
                        ? deleted
                        : false;
    }


    // =========================================
    // DELETED AT
    // =========================================

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }


    public void setDeletedAt(
            LocalDateTime deletedAt) {

        this.deletedAt =
                deletedAt;
    }

}