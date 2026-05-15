# Hotel Management System
### Comprehensive Feature & Workflow Documentation
> 2nd Semester University Project · Java · Swing GUI · OOP · File I/O

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [OOP Concept Implementation](#2-oop-concept-implementation)
3. [Entity Definitions](#3-entity-definitions)
4. [Features and Workflows](#4-features-and-workflows)
5. [File Handling](#5-file-handling-data-storage)
6. [Exception Handling](#6-exception-handling)
7. [UI Layout and Navigation](#7-ui-layout-and-navigation)
8. [End-to-End Workflow Narratives](#8-end-to-end-workflow-narratives)

---

## 1. Project Overview

The Hotel Management System (HMS) is a desktop GUI application built using Java Swing. It is designed as an educational project to demonstrate core Object-Oriented Programming principles through a real-world use case. The system supports complete CRUD management of hotel rooms, guests, and bookings, with data persisted to local CSV files — no database required.

### 1.1 Objectives

- Demonstrate Encapsulation, Inheritance, Abstraction, and Polymorphism clearly in code structure
- Provide a functional desktop application that survives restarts through file-based storage
- Implement CRUD operations for three core entities: Rooms, Guests, and Bookings
- Practice proper Java package organisation, exception handling, and file I/O

### 1.2 Technology Stack

| Component | Choice |
|---|---|
| Language | Java (JDK 8 or later) |
| GUI Framework | Java Swing (`javax.swing.*`) |
| Data Storage | CSV files via `BufferedReader` / `BufferedWriter` |
| Build Tool | Manual `javac` or any IDE (IntelliJ / Eclipse / VS Code) |
| Architecture | Layered: UI → Service → Repository → File |

### 1.3 Module Summary

| Module | Package | Responsibility |
|---|---|---|
| Models | `com.hotel.model` | Entity classes with OOP hierarchy |
| Repositories | `com.hotel.repository` | Read/write CSV files |
| Services | `com.hotel.service` | Business rules and validation |
| UI Panels | `com.hotel.ui.panels` | Swing-based management pages |
| Utilities | `com.hotel.util` | FileHandler, IDGenerator, Validator |
| Exceptions | `com.hotel.exception` | Custom runtime exceptions |

---

## 2. OOP Concept Implementation

Each of the four required OOP concepts is intentionally and clearly demonstrated in specific parts of the system.

---

### 2.1 Encapsulation

Encapsulation is applied in all model classes. Every field is declared `private`, and access is controlled exclusively through public getter and setter methods. This protects data integrity and hides internal state from other layers.

**Example — Room class:**

```
FIELDS (all private):
    roomId          : String
    roomNumber      : String
    roomType        : String
    pricePerNight   : double
    isAvailable     : boolean
    createdAt       : String

METHODS (all public):
    getRoomId()                      → String
    setRoomId(String id)             → void
    getPricePerNight()               → double
    setPricePerNight(double price)   → void
    isAvailable()                    → boolean
    setAvailable(boolean status)     → void
```

> **Rule:** Never access model fields directly from the UI or Service layers. Always go through getters and setters.

---

### 2.2 Inheritance

`Room` is the abstract parent class. Three concrete subclasses extend it, each adding type-specific fields and behaviour. `Guest` and `Booking` both extend a common `BaseEntity` class for shared ID and timestamp fields.

**Hierarchy — Base entity:**

```
abstract class BaseEntity
    ├── abstract class Room  extends BaseEntity
    │       ├── class StandardRoom  extends Room
    │       ├── class DeluxeRoom    extends Room
    │       └── class Suite         extends Room
    ├── class Guest    extends BaseEntity
    └── class Booking  extends BaseEntity
```

**What each Room subclass adds:**

| Class | Additional Fields / Behaviour |
|---|---|
| `StandardRoom` | `amenities` (String) — e.g. "Fan, TV, Bathroom" |
| `DeluxeRoom` | `hasAC` (boolean), `hasMinibar` (boolean) |
| `Suite` | `floorNumber` (int), `hasJacuzzi` (boolean), `maxOccupancy` (int) |

---

### 2.3 Abstraction

Abstraction is achieved through two mechanisms:

1. The abstract class `BaseEntity` forces all entities to implement `display()`
2. The interface `Manageable<T>` defines a contract for all service classes without exposing how operations are performed internally

**Abstract class — BaseEntity:**

```
abstract class BaseEntity:
    PRIVATE id         : String
    PRIVATE createdAt  : String

    PUBLIC getId()       → String
    PUBLIC getCreatedAt() → String
    PUBLIC ABSTRACT display() → void    ← every entity MUST implement this
```

**Interface — Manageable\<T\>:**

```
interface Manageable<T>:
    add(T entity)            throws HotelException
    getById(String id)       throws HotelException  → T
    getAll()                 → List<T>
    update(T entity)         throws HotelException
    delete(String id)        throws HotelException
```

> **Rule:** The UI layer only interacts with the `Manageable` interface. It never calls repository methods directly.

---

### 2.4 Polymorphism

Polymorphism is demonstrated in two ways:

- **Method overriding** — each room type overrides `display()` with its own output
- **Polymorphic usage** — the service loop that processes rooms works with any `Room` subtype without knowing the concrete class

**Method overriding — display():**

```
// In StandardRoom:
OVERRIDE display():
    PRINT "Standard Room | No: " + roomNumber + " | Price: " + pricePerNight

// In DeluxeRoom:
OVERRIDE display():
    PRINT "Deluxe Room | AC: " + hasAC + " | Minibar: " + hasMinibar

// In Suite:
OVERRIDE display():
    PRINT "Suite | Floor: " + floorNumber + " | Jacuzzi: " + hasJacuzzi
```

**Polymorphic usage in service layer:**

```
rooms = roomService.getAll()           // returns List<Room>

FOR EACH room IN rooms:
    room.display()                     // correct override called based on actual type
                                       // no if/else needed — Java handles it
```

---

## 3. Entity Definitions

### 3.1 Room

Rooms are the core asset of the hotel. The system supports three types, all inheriting from the abstract `Room` class.

**Base Room fields (all subtypes share these):**

| Field | Type | Description |
|---|---|---|
| `roomId` | String | Auto-generated unique ID (e.g. `R-001`) |
| `roomNumber` | String | Display number shown to staff (e.g. `101`, `202`) |
| `roomType` | String | One of: `STANDARD`, `DELUXE`, `SUITE` |
| `pricePerNight` | double | Charge per night in BDT |
| `isAvailable` | boolean | `true` if no active booking exists |
| `createdAt` | String | Timestamp of record creation |

**StandardRoom adds:**

| Field | Type | Description |
|---|---|---|
| `amenities` | String | Comma-separated list, e.g. `"Fan, TV, Bathroom"` |

**DeluxeRoom adds:**

| Field | Type | Description |
|---|---|---|
| `hasAC` | boolean | Whether the room has air conditioning |
| `hasMinibar` | boolean | Whether the room has a minibar |

**Suite adds:**

| Field | Type | Description |
|---|---|---|
| `floorNumber` | int | Floor the suite is located on |
| `hasJacuzzi` | boolean | Whether the suite has a jacuzzi |
| `maxOccupancy` | int | Maximum number of guests allowed |

---

### 3.2 Guest

| Field | Type | Description |
|---|---|---|
| `guestId` | String | Auto-generated (e.g. `G-001`) |
| `fullName` | String | Guest full name |
| `phone` | String | 11-digit mobile number starting with `01` |
| `nidOrPassport` | String | National ID or Passport number — must be unique |
| `address` | String | Optional home address |
| `createdAt` | String | Date guest was registered |

---

### 3.3 Booking

| Field | Type | Description |
|---|---|---|
| `bookingId` | String | Auto-generated (e.g. `B-001`) |
| `guestId` | String | References a registered Guest |
| `roomId` | String | References a Room |
| `checkInDate` | String | Format: `YYYY-MM-DD` |
| `checkOutDate` | String | Format: `YYYY-MM-DD` |
| `totalBill` | double | Computed: `nights × pricePerNight` |
| `status` | String | `ACTIVE` or `CANCELLED` |
| `createdAt` | String | Booking creation timestamp |

> **Note:** `totalBill` is calculated at runtime — not manually entered. It is derived from the room price and the date range.

---

## 4. Features and Workflows

---

### 4.1 Room Management

The Room Management panel lets staff add, view, edit, and delete hotel rooms.

#### 4.1.1 Add New Room

**Workflow:**

1. Staff selects room type from a dropdown (Standard / Deluxe / Suite)
2. Additional fields appear dynamically based on the selected type
3. Staff fills in: room number, price per night, and type-specific fields
4. Staff clicks **Add Room**
5. System validates all fields (see Validation Rules below)
6. On success — ID generated, record saved, table refreshed, form cleared
7. On failure — inline error message shown below the form

**Pseudo code:**

```
FUNCTION addRoom(formData):
    IF any required field is empty THEN
        SHOW error "All fields are required"
        RETURN

    IF roomNumber already exists THEN
        SHOW error "Room number already taken"
        RETURN

    newRoom = createRoomByType(formData.type, formData)
    newRoom.id          = IDGenerator.generate("R", "data/rooms.csv")
    newRoom.isAvailable = true
    newRoom.createdAt   = today's date

    roomRepository.save(newRoom)
    refreshTable()
    clearForm()
```

#### 4.1.2 View All Rooms

A `JTable` displays all rooms. Loaded from file on panel open and refreshed after any operation.

**Columns:** Room ID · Room No. · Type · Price/Night · Status (Available / Booked) · Actions

**Filters available:**
- Filter by Type — All / Standard / Deluxe / Suite
- Filter by Availability — All / Available / Booked

#### 4.1.3 Edit Room

Clicking **Edit** on a table row opens the form pre-filled with that room's data. Room ID and Type are read-only. Only price and type-specific fields can be changed.

**Pseudo code:**

```
FUNCTION editRoom(roomId, updatedData):
    existingRoom = roomRepository.findById(roomId)

    IF existingRoom is null THEN
        SHOW error "Room not found"
        RETURN

    existingRoom.pricePerNight = updatedData.price
    // update type-specific fields as needed...

    roomRepository.update(existingRoom)
    refreshTable()
    resetFormToAddMode()
```

#### 4.1.4 Delete Room

A room can only be deleted if it has no `ACTIVE` booking.

**Pseudo code:**

```
FUNCTION deleteRoom(roomId):
    IF bookingService.hasActiveBooking(roomId) THEN
        SHOW error "Cannot delete: room has an active booking"
        RETURN

    SHOW confirmation dialog "Delete room R-XXX?"
    IF confirmed THEN
        roomRepository.deleteById(roomId)
        refreshTable()
```

> **Important:** Room availability (`isAvailable`) is never set manually by staff. It is automatically controlled by `BookingService` when a booking is created or cancelled.

#### 4.1.5 Validation Rules — Room

| Field | Rule |
|---|---|
| Room Number | Required, numeric only, must be unique |
| Room Type | Must select one of: STANDARD, DELUXE, SUITE |
| Price Per Night | Required, must be a positive decimal number |
| Floor Number *(Suite only)* | Required, must be integer ≥ 1 |
| Max Occupancy *(Suite only)* | Required, must be integer between 1 and 10 |

---

### 4.2 Guest Management

Guest Management handles registration and maintenance of guest profiles. Every booking must reference a registered guest.

#### 4.2.1 Register New Guest

**Workflow:**

1. Staff fills in: full name, phone, NID or passport number, address (optional)
2. Staff clicks **Register Guest**
3. System checks for duplicate NID/passport
4. On success — guest saved with auto-generated ID and current date

**Pseudo code:**

```
FUNCTION registerGuest(formData):
    IF name, phone, or nid is empty THEN
        SHOW error "Required fields are missing"
        RETURN

    IF NOT isValidPhone(formData.phone) THEN
        SHOW error "Phone must be 11 digits starting with 01"
        RETURN

    IF guestRepository.existsByNid(formData.nid) THEN
        SHOW error "NID already registered"
        RETURN

    guest         = new Guest(formData)
    guest.id      = IDGenerator.generate("G", "data/guests.csv")
    guest.createdAt = today's date

    guestRepository.save(guest)
    refreshTable()
    clearForm()
```

#### 4.2.2 View, Edit, Delete Guest

**View:** Table columns — Guest ID · Name · Phone · NID · Address · Registered Date · Actions

**Edit:** All fields editable except Guest ID and NID (NID is identity — cannot change).

**Delete:** Guest cannot be deleted if they have any `ACTIVE` booking. System checks before allowing deletion.

**Pseudo code — Delete:**

```
FUNCTION deleteGuest(guestId):
    IF bookingService.hasActiveBookingForGuest(guestId) THEN
        SHOW error "Cannot delete: guest has an active booking"
        RETURN

    SHOW confirmation dialog
    IF confirmed THEN
        guestRepository.deleteById(guestId)
        refreshTable()
```

#### 4.2.3 Validation Rules — Guest

| Field | Rule |
|---|---|
| Full Name | Required, letters and spaces only, minimum 3 characters |
| Phone | Required, exactly 11 digits, must start with `01` |
| NID / Passport | Required, must be unique across all guests |
| Address | Optional, maximum 200 characters |

---

### 4.3 Booking Management

Booking is the central feature. It connects guests and rooms, tracks stay dates, computes bills, and controls room availability state automatically.

#### 4.3.1 Create Booking

**Workflow:**

1. Staff selects a guest from a searchable dropdown (shows Name + ID)
2. Staff selects a room from a dropdown — only `AVAILABLE` rooms are shown
3. Staff enters check-in date and check-out date
4. System auto-calculates and displays: number of nights + total bill
5. Staff clicks **Confirm Booking**
6. System validates dates and re-checks room availability
7. On success — booking saved with status `ACTIVE`, room marked `isAvailable = false`

**Pseudo code:**

```
FUNCTION createBooking(guestId, roomId, checkIn, checkOut):
    IF checkIn is in the past THEN
        SHOW error "Check-in cannot be in the past"
        RETURN

    IF checkOut <= checkIn THEN
        SHOW error "Check-out must be after check-in"
        RETURN

    room = roomService.getById(roomId)

    IF NOT room.isAvailable THEN
        THROW RoomNotAvailableException("Room is no longer available")

    nights    = calculateNights(checkIn, checkOut)
    totalBill = nights * room.pricePerNight

    booking           = new Booking()
    booking.id        = IDGenerator.generate("B", "data/bookings.csv")
    booking.guestId   = guestId
    booking.roomId    = roomId
    booking.checkIn   = checkIn
    booking.checkOut  = checkOut
    booking.totalBill = totalBill
    booking.status    = "ACTIVE"
    booking.createdAt = today's date

    bookingRepository.save(booking)
    roomService.setAvailability(roomId, false)

    refreshTable()
    refreshRoomDropdown()
```

#### 4.3.2 View All Bookings

**Columns:** Booking ID · Guest Name · Room No. · Check-in · Check-out · Nights · Total Bill · Status · Actions

**Filters available:**
- Filter by Status — All / Active / Cancelled

#### 4.3.3 Cancel Booking

Only `ACTIVE` bookings can be cancelled. On cancellation, the room is automatically set back to available.

**Pseudo code:**

```
FUNCTION cancelBooking(bookingId):
    booking = bookingRepository.findById(bookingId)

    IF booking is null THEN
        SHOW error "Booking not found"
        RETURN

    IF booking.status == "CANCELLED" THEN
        SHOW error "Booking is already cancelled"
        RETURN

    SHOW confirmation dialog "Cancel booking B-XXX?"
    IF confirmed THEN
        booking.status = "CANCELLED"
        bookingRepository.update(booking)
        roomService.setAvailability(booking.roomId, true)
        refreshTable()
```

#### 4.3.4 Validation Rules — Booking

| Rule | Detail |
|---|---|
| Guest must exist | `guestId` must match a registered guest |
| Room must be available | `room.isAvailable` must be `true` at time of booking |
| Check-in not in past | `checkIn` must be ≥ today's date |
| Check-out after check-in | `checkOut` must be > `checkIn` by at least 1 day |
| No double booking | Same room cannot have two `ACTIVE` bookings simultaneously |

---

## 5. File Handling (Data Storage)

All data is stored in plain CSV files inside a `/data/` directory. The `FileHandler` utility class handles all read and write operations, keeping I/O logic out of service and model classes.

### 5.1 File Structure

| File | Content |
|---|---|
| `data/rooms.csv` | All room records — one row per room |
| `data/guests.csv` | All guest records — one row per guest |
| `data/bookings.csv` | All booking records — one row per booking |

### 5.2 CSV Formats

**rooms.csv:**

```
roomId,roomNumber,roomType,pricePerNight,isAvailable,extraData,createdAt
R-001,101,STANDARD,2500.00,true,"Fan,TV,Bathroom",2024-01-15
R-002,201,DELUXE,4500.00,false,AC=true|Minibar=true,2024-01-15
R-003,301,SUITE,8000.00,true,Floor=3|Jacuzzi=true|MaxOcc=4,2024-01-16
```

**guests.csv:**

```
guestId,fullName,phone,nidOrPassport,address,createdAt
G-001,Rahim Uddin,01711234567,1234567890123,Dhaka,2024-01-20
G-002,Salma Begum,01811112222,9876543210987,Chittagong,2024-01-21
```

**bookings.csv:**

```
bookingId,guestId,roomId,checkInDate,checkOutDate,totalBill,status,createdAt
B-001,G-001,R-001,2024-02-01,2024-02-05,10000.00,ACTIVE,2024-01-25
B-002,G-002,R-002,2024-02-10,2024-02-12,9000.00,CANCELLED,2024-01-28
```

### 5.3 FileHandler — Pseudo Code

**Read all records:**

```
FUNCTION readAll(filePath):
    lines = empty list

    TRY
        reader = new BufferedReader(new FileReader(filePath))
        skip first line (header)

        FOR each line in reader:
            lines.add(line.split(","))

        RETURN lines

    CATCH IOException:
        THROW FileReadException("Could not read " + filePath)
    FINALLY
        close reader
```

**Write all records (full overwrite):**

```
FUNCTION writeAll(filePath, headerLine, records):
    TRY
        writer = new BufferedWriter(new FileWriter(filePath))
        writer.write(headerLine)
        writer.newLine()

        FOR each record in records:
            writer.write(record.toCsvRow())
            writer.newLine()

    CATCH IOException:
        THROW FileWriteException("Could not write to " + filePath)
    FINALLY
        close writer
```

**Ensure file exists on startup:**

```
FUNCTION ensureFileExists(filePath, headerLine):
    create /data/ directory if it does not exist

    IF file does not exist THEN
        create file
        write headerLine as first line
```

> **Note:** Update and Delete are not done in-place. The full list is read into memory, modified, then written back entirely. This is the standard approach for file-based storage without a database.

### 5.4 IDGenerator — Pseudo Code

IDs follow the format `PREFIX-NNN` — e.g. `R-001`, `G-042`, `B-100`.

```
FUNCTION generate(prefix, filePath):
    rows = FileHandler.readAll(filePath)

    IF rows is empty THEN
        RETURN prefix + "-001"

    lastRow    = rows.get(rows.size() - 1)
    lastId     = lastRow[0]                  // e.g. "R-007"
    number     = parseInt(lastId.split("-")[1])
    nextNumber = number + 1

    RETURN prefix + "-" + padLeft(nextNumber, 3, '0')
    // e.g. 7 → "007", 10 → "010", 100 → "100"
```

---

## 6. Exception Handling

All runtime errors are caught and surfaced to the user through the GUI rather than crashing the application. Custom exception classes extend `RuntimeException` and carry meaningful messages.

### 6.1 Custom Exception Classes

| Class | When thrown |
|---|---|
| `HotelException` | Base class for all custom exceptions |
| `RoomNotAvailableException` | Booking a room that is not available |
| `DuplicateRecordException` | Adding a room number or NID that already exists |
| `RecordNotFoundException` | ID lookup returns no result |
| `FileReadException` | CSV file cannot be opened or read |
| `FileWriteException` | Writing to a CSV file fails |
| `InvalidInputException` | Form data fails validation |

### 6.2 Class Structure

```
EVERY custom exception:
    extends HotelException
    constructor(String message):
        super(message)

HotelException:
    extends RuntimeException
    constructor(String message):
        super(message)
```

### 6.3 Handling Pattern in UI

All service method calls in the UI layer are wrapped in `try-catch`. Errors are shown in a status label below the form — never as a raw stack trace.

```
TRY
    bookingService.createBooking(guestId, roomId, checkIn, checkOut)
    showSuccessLabel("Booking created successfully!")
    refreshTable()

CATCH RoomNotAvailableException e:
    showErrorLabel("Room is no longer available. Please select another.")

CATCH InvalidInputException e:
    showErrorLabel(e.getMessage())

CATCH HotelException e:
    showErrorLabel("Something went wrong: " + e.getMessage())
```

> **Rule:** Never show a raw Java stack trace in the GUI. Always catch exceptions at the UI layer and display a clean, human-readable message in red text.

---

## 7. UI Layout and Navigation

### 7.1 Main Frame Structure

`MainFrame` is a `JFrame` using `BorderLayout`. A fixed left sidebar handles navigation. The center switches between panels using `CardLayout`.

| Region | Component |
|---|---|
| `NORTH` | Header bar — system name + current date label |
| `WEST` | Sidebar with navigation buttons |
| `CENTER` | `CardLayout` panel — switches between management pages |
| `SOUTH` | Status bar — last action message |

### 7.2 Sidebar Navigation Buttons

- Dashboard
- Room Management
- Guest Management
- Booking Management

### 7.3 Management Panel Layout

Each management panel uses a consistent two-section layout:

| Section | Contents |
|---|---|
| **TOP — Form Section** | Input fields, dropdowns, buttons (Add / Save / Clear) |
| **BOTTOM — Table Section** | `JTable` with all records, filter controls, row-level action buttons |

> **Note:** The same form is used for both Add and Edit. When **Edit** is clicked on a row, the form pre-fills and the button label changes from `Add Room` to `Save Changes`. A **Cancel Edit** button resets back to Add mode.

### 7.4 Dashboard Quick Stats

The dashboard shows four metric cards loaded fresh from file each time it opens:

- Total Rooms
- Available Rooms
- Total Guests  
- Active Bookings

---

## 8. End-to-End Workflow Narratives

### 8.1 First-Time Setup Flow

1. Application starts — `MainFrame` initialises
2. `FileHandler.ensureFileExists()` is called for all three CSV files
3. `/data/` directory is created if it does not exist
4. Each CSV file is created with its header row if it does not exist
5. Dashboard loads — all stats show `0`
6. System is ready for use

### 8.2 Full Booking Workflow (Happy Path)

1. Staff opens **Guest Management** → registers Rahim Uddin → `G-001` created
2. Staff opens **Room Management** → adds Standard Room No. 101, price 2500 → `R-001` created (`isAvailable = true`)
3. Staff opens **Booking Management** → selects guest `G-001` → selects room `R-001` (appears in dropdown because available)
4. Staff sets check-in: `2024-03-01`, check-out: `2024-03-04` → system displays: **3 nights × 2,500 = BDT 7,500**
5. Staff clicks **Confirm Booking** → `B-001` saved with status `ACTIVE`
6. Room `R-001` → `isAvailable = false` → no longer appears in the available rooms dropdown
7. Dashboard **Active Bookings** count increases to `1`

### 8.3 Cancellation Workflow

1. Staff opens **Booking Management** → finds `B-001` in table (status: ACTIVE)
2. Staff clicks **Cancel** on that row → confirmation dialog appears
3. Staff confirms → booking status set to `CANCELLED`
4. Room `R-001` → `isAvailable = true` → reappears in available rooms dropdown
5. Dashboard **Active Bookings** count decreases

### 8.4 Error Scenario — Room Already Booked

1. `B-001` is ACTIVE for Room `R-001`
2. Staff attempts to create a new booking for Room `R-001`
3. `BookingService.createBooking()` checks availability → room is not available
4. `RoomNotAvailableException` is thrown
5. UI catches it → displays: *"Room is no longer available. Please select another room."*
6. Form stays open — staff selects a different room without losing other input

### 8.5 Error Scenario — Duplicate NID

1. Guest `G-001` is registered with NID `1234567890123`
2. Staff attempts to register another guest with the same NID
3. `GuestService.add()` calls `guestRepository.existsByNid()` → returns `true`
4. `DuplicateRecordException` thrown
5. UI displays: *"NID already registered. Please check the guest records."*

### 8.6 Data Persistence Check

1. Staff adds rooms, guests, and bookings during a session
2. Application is closed
3. Application is reopened
4. All data reloads from CSV files automatically on startup
5. Dashboard stats reflect the correct saved state
