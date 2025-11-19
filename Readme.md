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

"I built a RESTful API using Spring Boot and MySQL.

- The application has two main entities:
- User and Note, with a one-to-many relationship where each user can have multiple notes.
- I used Spring Data JPA for database operations, which automatically generates SQL queries from repository methods.
- asdfgh sdfgh
- The database schema is managed by Hibernate with auto-update enabled for development."
