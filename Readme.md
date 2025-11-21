<!-- @format -->

# Notes API - Full Spec (What We're Building)

### Tech Stack:

- Backend: Spring Boot + MySQL + JWT
- Frontend: React + Tailwind (simple UI, no fancy stuff)
- Deploy: Backend on Railway, Frontend on Vercel

### Features:

- User register/login (JWT auth)
- Create/Read/Update/Delete notes (authenticated users only)
- Each user sees only their own notes
- Simple React UI to interact with API

### Timeline:

- Day 1: Spring Boot setup + User auth (register/login) + JWT
- Day 2: Notes CRUD endpoints + connect to MySQL
- Day 3: React frontend + connect to backend
- Day 4: Deploy + test + push to GitHub

What You'll Learn (Interview Concepts)
While building, you'll understand:

- REST API design (POST, GET, PUT, DELETE)
- JWT authentication flow
- Spring Security basics
- Database relationships (User has many Notes)
- CORS configuration (frontend ↔ backend)
- Deployment process

## What each setting does (Interview Answer):

- ddl-auto=update: Auto-creates/updates tables based on your entities
- show-sql=true: Shows SQL queries in console (helpful for debugging)
- jwt.secret: Secret key for signing JWT tokens (must be long and secure)
- jwt.expiration: Token expires in 24 hours (86400000 milliseconds).

### Entity - layer

### user.java class used annotations

- @Entity: Tells Spring this is a database table
- @Id: Primary key
- @GeneratedValue: Auto-increment ID
- @Column(unique = true): Email must be unique (no duplicate accounts)
- @OneToMany: One user can have many notes (relationship)
- @PrePersist: Automatically sets createdAt when saving - (@PrePersist
  Runs right before inserting a new entity, commonly used to set createdAt.)
- @PreUpdate: Runs right before updating an existing entity, commonly used to set updatedAt.

### notes.java class used annotations

- `@ManyToOne`: Many notes belong to one user
- `@JoinColumn`: Foreign key column (user_id)
- `@JsonIgnore`: Don't send user object when returning notes (prevents circular reference)
- `@PreUpdate`: Auto-updates updatedAt timestamp on every save

### Repository layer

- JpaRepository<User, Long>: Gives you free CRUD methods (save, findById, delete, etc.)
- findByEmail: Spring automatically creates SQL query from method name
- findByUserId: Get all notes for a specific user,

### "Can you explain your Notes API project?" - on 19.nov
hjkl;
"I built a RESTful API using Spring Boot and MySQL.

- The application has two main entities:
- User and Note, with a one-to-many relationship where each user can have multiple notes.
- I used Spring Data JPA for database operations, which automatically generates SQL queries from repository methods.
- asdfgh sdfgh
- The database schema is managed by Hibernate with auto-update enabled for development."


### Interview
- What's the relationship between User and Note? (One-to-many, many-to-one, or many-to-many?)
One-to-many means one User can have many Notes. 
The foreign key (user_id) is stored in the notes table. From the Note side, it's many-to-one because many notes belong to one user. 
Many-to-many would need a junction table, like users_roles where one user has many roles and one role belongs to many users

  
What does @Entity do?
@Entity tells JPA that this class represents a database table. When the application runs, Hibernate reads these classes and creates corresponding tables in MySQL. 
Field names become column names automatically, unless we use @Column to customize them. @Table annotation lets us specify the exact table name.


Why do we use @JsonIgnore on the user field in Note entity?
@JsonIgnore prevents infinite recursion. When we return a Note as JSON, it would include the User object. That User has a list of Notes, which each have a User, creating a circular reference. 
@JsonIgnore breaks this cycle by excluding the user field from JSON serialization


What's the difference between @PrePersist and @PreUpdate?
"@PrePersist is a JPA lifecycle callback that runs automatically before an entity is first saved to the database.
We use it to set createdAt timestamp.
@PreUpdate runs before any update operation, so we use it to update the updatedAt timestamp whenever a note is modified."

| Annotation | Framework | When It Runs | What It Does |
| :--- | :--- | :--- | :--- |
| **`@JsonIgnore`** | Jackson | When converting a Java object to JSON/XML. | **Hides** a field from the output. |
| **`@PrePersist`** | JPA/Hibernate | **Before** an object is saved for the **first time** (creation). | Good for setting the **`creationDate`**. |
| **`@PreUpdate`** | JPA/Hibernate | **Before** an **existing** object is saved (update). | Good for setting the **`lastModifiedDate`**. |

### Why DTOs? 
- We don't send/receive Entity objects directly. DTOs control exactly what data goes in/out.

# Interview Q&A: JWT Authentication and Security

Below are technical interview questions and detailed answers regarding the implementation of JWT authentication, password handling, and security best practices in a typical application environment (e.g., using Spring Security).

---

### Q1: How does JWT authentication work in your application?

When a user registers or logs in, the backend generates a JWT token signed with a secret key. This token contains core information like the user's email and an expiration time.

The frontend stores this token (e.g., in memory or an `httpOnly` cookie) and sends it in the `Authorization` header as `'Bearer <token>'` for all subsequent protected requests.

My `JwtAuthenticationFilter` (which usually extends `OncePerRequestFilter` in Spring) intercepts every incoming request. It validates the token's signature using the secret key, extracts the user's email from the payload, loads the corresponding user details from the database, and finally sets the authentication object in the Spring Security context. This process allows the application to remain stateless, as the server doesn't need to maintain traditional session state.

### Q2: Why do you hash passwords? What algorithm do you use?

Storing plain text passwords is a critical security risk. If the database were ever compromised, attackers would gain immediate access to all user credentials.

I use **BCrypt**, which is the current standard for password hashing. BCrypt is specifically designed for passwords because:

1.  **It is one-way:** The original password cannot be retrieved from the hash.
2.  **It is slow by design:** This computational "slowness" (managed via iterations or "work factor") helps defend against brute-force and rainbow table attacks.
3.  **It handles salting automatically:** A unique, random salt is added to each password before hashing, ensuring that identical passwords stored by different users produce entirely different hashes.

### Q3: What's the difference between authentication and authorization?

*   **Authentication** verifies *WHO* you are. It is the process of confirming a user's identity, typically via a login mechanism using credentials like an email/password combination. In my application, JWT handles this process.
*   **Authorization** verifies *WHAT* you can do. It's the process of granting or denying permissions to specific resources or actions based on the user's roles (e.g., a standard user versus an admin).

In my current application, the JWT verifies the user's identity (authentication). I could implement role-based authorization in the future by storing roles within the User entity and enforcing checks using Spring Security annotations (like `@PreAuthorize`).

### Q4: Why disable CSRF in your security config?

CSRF (Cross-Site Request Forgery) protection is a defense mechanism primarily designed for applications that rely on **cookie-based** authentication and stateful sessions.

Since my application uses stateless **JWT tokens sent in custom HTTP headers** (the `Authorization` header), traditional CSRF attacks do not apply. Browsers do not automatically attach custom headers to cross-site requests the way they do with cookies. Disabling CSRF in the configuration is the correct practice for a RESTful API using JWT header authentication.

### Q5: What happens if a JWT token is stolen?

If an attacker obtains a valid token, they can impersonate the legitimate user until the token expires (which is set to 24 hours in my application).

To mitigate this risk:

*   We enforce **HTTPS/SSL** everywhere to encrypt traffic and prevent tokens from being intercepted over the network.
*   We set **reasonable expiration times** to limit the window of exposure.
*   Tokens are stored securely on the frontend (ideally in `httpOnly` cookies in production to mitigate XSS risks, rather than `localStorage`).
*   For critical production systems, we could implement a **token revocation** or **blacklisting** mechanism (usually via a cache like Redis) to immediately invalidate compromised tokens before they naturally expire.

### Q6: Explain your JwtAuthenticationFilter flow.

### The custom filter runs on every request before it reaches the main controller logic. The specific flow is:

1.  The filter checks the `Authorization` header for the `Bearer <token>` scheme.
2.  It extracts the raw JWT string.
3.  It validates the token using the configured secret key to ensure integrity and expiration time.
4.  If valid, it extracts the user's email (subject) from the token payload.
5.  It uses a `UserDetailsService` to load the full `UserDetails` object from the database based on that email.
6.  Finally, it creates an `Authentication` object (including the user details and authorities) and manually sets it into the Spring Security context, allowing the request to proceed to the controller with the necessary permissions established.


### Q7: Why do you hash passwords?
- "Storing plain text passwords is a security risk. If the database is breached, attackers get all passwords. I use BCrypt to hash passwords before storing them. BCrypt is a one-way function - you can't reverse it to get the original password. When a user logs in, I use passwordEncoder.matches() to compare the plain text password they enter with the hashed version in the database. BCrypt also adds a salt (random data) to each password, so even identical passwords produce different hashes."
### Q8: Can you unhash a BCrypt password?
- "No. BCrypt is a one-way hashing algorithm. You cannot reverse it. The only way to verify a password is to hash the input and compare it with the stored hash using the matches() method."

Summary
What you do in Postman:

Register: Send plain text password → Backend hashes it → Stores hash in DB
Login: Send SAME plain text password → Backend compares with stored hash → Success

You NEVER send the hashed password in requests.

[//]: # (------)
Use POST method
http://localhost:8080/api/auth/login :
{
"email": "testuser1@gmail.com",
"password": "password1"
}

http://localhost:8080/api/auth/register
{
"name": "testuser1",
"email": "testuser1@gmail.com",
"password": "password1"
}
[//]: # (------)
