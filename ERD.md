# Chat Application Entity Relationship Diagram (ERD)

## Database Schema

### Users Table
```sql
users
├── id (PK) - Integer, Auto-increment
├── username - String(50), Unique, Not Null
├── email - String(100), Unique, Not Null
├── display_name - String(100)
├── about - String(500)
├── password_hash - String, Not Null
├── profile_picture_url - String(255)
└── created_at - DateTime, Not Null
```

### Conversations Table
```sql
conversations
├── id (PK) - Integer, Auto-increment
├── user1_id (FK) - Integer, Not Null
├── user2_id (FK) - Integer, Not Null
├── last_message_text - String(500)
├── last_message_time - DateTime
├── last_message_sender_id - Integer
├── created_at - DateTime, Not Null
└── updated_at - DateTime
```

### Messages Table
```sql
messages
├── id (PK) - Integer, Auto-increment
├── conversation_id (FK) - Integer, Not Null
├── sender_id (FK) - Integer, Not Null
├── content - String(2000), Not Null
├── is_read - Boolean, Not Null
├── is_delivered - Boolean, Not Null
├── is_deleted - Boolean, Not Null
├── sent_at - DateTime, Not Null
├── delivered_at - DateTime
└── read_at - DateTime
```

### Message Attachments Table
```sql
message_attachments
├── id (PK) - Integer, Auto-increment
├── message_id (FK) - Integer, Not Null
├── file_url - String(255), Not Null
├── file_name - String(255), Not Null
├── file_type - String(50), Not Null
├── file_size - Long, Not Null
└── created_at - DateTime, Not Null
```

### Friendships Table
```sql
friendships
├── id (PK) - Integer, Auto-increment
├── user1_id (FK) - Integer, Not Null
├── user2_id (FK) - Integer, Not Null
└── created_at - DateTime, Not Null
```

### Friend Requests Table
```sql
friend_requests
├── id (PK) - Integer, Auto-increment
├── sender_id (FK) - Integer, Not Null
├── receiver_id (FK) - Integer, Not Null
├── status - Enum(PENDING, ACCEPTED, REJECTED), Not Null
└── created_at - DateTime, Not Null
```

## Entity Relationships

1. **User Relationships**
   - A User can have many Conversations (as user1 or user2)
   - A User can have many Messages (as sender)
   - A User can have many Friendships (as user1 or user2)
   - A User can have many Friend Requests (as sender or receiver)

2. **Conversation Relationships**
   - A Conversation belongs to two Users (user1 and user2)
   - A Conversation has many Messages
   - A Conversation has one last Message (tracked by last_message_text, last_message_time, and last_message_sender_id)

3. **Message Relationships**
   - A Message belongs to one Conversation
   - A Message belongs to one User (sender)
   - A Message can have many Message Attachments

4. **Message Attachment Relationships**
   - A Message Attachment belongs to one Message

5. **Friendship Relationships**
   - A Friendship connects two Users (user1 and user2)
   - Unique constraint on (user1_id, user2_id) to prevent duplicate friendships

6. **Friend Request Relationships**
   - A Friend Request connects two Users (sender and receiver)
   - Status tracks the state of the request (PENDING, ACCEPTED, REJECTED)

## Key Features of the Schema

1. **User Management**
   - Unique usernames and emails
   - Profile information including display name and about section
   - Profile picture support
   - Password hashing for security

2. **Conversation Management**
   - One-to-one conversations between users
   - Last message tracking for conversation previews
   - Timestamps for creation and updates

3. **Message System**
   - Support for text messages
   - Message status tracking (read, delivered, deleted)
   - Timestamps for sent, delivered, and read times
   - Support for message attachments

4. **Friendship System**
   - Bidirectional friendships
   - Prevention of duplicate friendships
   - Friend request system with status tracking

5. **Attachment System**
   - Support for various file types
   - File metadata tracking (name, type, size)
   - URL-based file storage

## Database Constraints

1. **Primary Keys**
   - All tables have auto-incrementing integer primary keys

2. **Foreign Keys**
   - All relationships are properly constrained with foreign keys
   - Cascade delete where appropriate (e.g., messages in a conversation)

3. **Unique Constraints**
   - Username and email in Users table
   - User pairs in Friendships table
   - Message attachments linked to specific messages

4. **Not Null Constraints**
   - Essential fields marked as Not Null
   - Timestamps for creation dates
   - Required relationship fields

## Indexes

1. **Primary Indexes**
   - All primary keys are automatically indexed

2. **Foreign Key Indexes**
   - All foreign key columns are indexed for better join performance

3. **Unique Indexes**
   - Username and email in Users table
   - User pairs in Friendships table

4. **Performance Indexes**
   - Last message time in Conversations for sorting
   - Sent time in Messages for chronological ordering
   - Status in Friend Requests for filtering 