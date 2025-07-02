# Chat Application Data Flow Diagrams (DFD)

## Level 0 (Context Diagram)
```mermaid
graph LR
    User((User)) -->|Login/Register| ChatSystem[Chat System]
    User -->|Send Message| ChatSystem
    User -->|Manage Profile| ChatSystem
    User -->|Friend Request| ChatSystem
    ChatSystem -->|Messages| User
    ChatSystem -->|Notifications| User
    ChatSystem -->|Friend Status| User
```

## Level 1 (Top Level)
```mermaid
graph TD
    User((User)) -->|Login/Register| Auth[Authentication]
    User -->|Send Message| MessageHandler[Message Handler]
    User -->|Update Profile| ProfileManager[Profile Manager]
    User -->|Friend Request| FriendManager[Friend Manager]
    
    Auth -->|Validate| Database[(Database)]
    MessageHandler -->|Store| Database
    ProfileManager -->|Update| Database
    FriendManager -->|Manage| Database
    
    Database -->|User Data| Auth
    Database -->|Messages| MessageHandler
    Database -->|Profile Data| ProfileManager
    Database -->|Friend Data| FriendManager
    
    MessageHandler -->|Deliver| User
    ProfileManager -->|Confirm| User
    FriendManager -->|Notify| User
```

## Level 2 (Detailed Level)
```mermaid
graph TD
    subgraph Authentication
        Login[Login] --> Validate[Validate Credentials]
        Register[Register] --> CreateUser[Create User]
        CreateUser --> StoreUser[Store User Data]
    end
    
    subgraph Message Handling
        Compose[Compose Message] --> ValidateMessage[Validate Message]
        ValidateMessage --> StoreMessage[Store Message]
        StoreMessage --> NotifyRecipient[Notify Recipient]
        NotifyRecipient --> UpdateStatus[Update Message Status]
    end
    
    subgraph Profile Management
        EditProfile[Edit Profile] --> ValidateProfile[Validate Profile]
        ValidateProfile --> UpdateProfile[Update Profile]
        UpdateProfile --> StoreProfile[Store Profile]
    end
    
    subgraph Friend Management
        SendRequest[Send Friend Request] --> ValidateRequest[Validate Request]
        ValidateRequest --> StoreRequest[Store Request]
        StoreRequest --> NotifyUser[Notify User]
        AcceptRequest[Accept Request] --> UpdateFriendship[Update Friendship]
        UpdateFriendship --> StoreFriendship[Store Friendship]
    end
    
    subgraph Database
        UserDB[(User DB)]
        MessageDB[(Message DB)]
        ProfileDB[(Profile DB)]
        FriendDB[(Friend DB)]
    end
    
    StoreUser --> UserDB
    StoreMessage --> MessageDB
    StoreProfile --> ProfileDB
    StoreRequest --> FriendDB
    StoreFriendship --> FriendDB
    
    UserDB --> Validate
    MessageDB --> NotifyRecipient
    ProfileDB --> ValidateProfile
    FriendDB --> ValidateRequest
```

## Process Descriptions

### Authentication Processes
1. **Login**
   - Input: Username/Email and Password
   - Process: Validate credentials against database
   - Output: Authentication token or error message

2. **Register**
   - Input: User details (username, email, password)
   - Process: Create new user account
   - Output: Success message or validation errors

### Message Handling Processes
1. **Compose Message**
   - Input: Message content and recipient
   - Process: Validate and format message
   - Output: Prepared message for sending

2. **Message Delivery**
   - Input: Prepared message
   - Process: Store and deliver to recipient
   - Output: Delivery confirmation

### Profile Management Processes
1. **Edit Profile**
   - Input: Updated profile information
   - Process: Validate and update profile
   - Output: Updated profile confirmation

### Friend Management Processes
1. **Friend Request**
   - Input: Friend request details
   - Process: Validate and store request
   - Output: Request notification

2. **Accept Request**
   - Input: Request acceptance
   - Process: Update friendship status
   - Output: Friendship confirmation

## Data Stores
1. **User Database**
   - Stores user credentials and basic information
   - Maintains user authentication data

2. **Message Database**
   - Stores all messages and their status
   - Maintains message history

3. **Profile Database**
   - Stores user profile information
   - Maintains profile updates

4. **Friend Database**
   - Stores friendship relationships
   - Maintains friend request status

## External Entities
1. **User**
   - Initiates all processes
   - Receives notifications and updates
   - Manages their profile and connections 