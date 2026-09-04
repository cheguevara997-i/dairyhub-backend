package dairyhub_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================
    // USER INFORMATION
    // =========================================

    private String name;


    @Column(
            unique = true,
            nullable = false
    )
    private String email;


    private String password;


    private String phone;


    private String role;


    // =========================================
    // ADMIN MANAGEMENT
    // =========================================

    /*
     * false = normal customer / protected admin
     *
     * true = customer promoted to ADMIN
     *
     * Boolean is used instead of primitive boolean
     * so login/register JSON is allowed to omit this
     * property.
     */

    @Column(
            name = "admin_managed",
            nullable = false
    )
    private Boolean adminManaged = false;


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
            String role,
            Boolean adminManaged) {

        this.id = id;

        this.name = name;

        this.email = email;

        this.password = password;

        this.phone = phone;

        this.role = role;

        this.adminManaged =
                adminManaged != null
                        ? adminManaged
                        : false;
    }


    // =========================================
    // ID
    // =========================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    // =========================================
    // NAME
    // =========================================

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    // =========================================
    // EMAIL
    // =========================================

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    // =========================================
    // PASSWORD
    // =========================================

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    // =========================================
    // PHONE
    // =========================================

    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    // =========================================
    // ROLE
    // =========================================

    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    // =========================================
    // ADMIN MANAGED
    // =========================================

    /*
     * IMPORTANT:
     *
     * Use getAdminManaged(), NOT isAdminManaged().
     *
     * This prevents Jackson from interpreting the
     * property as primitive boolean.
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

}