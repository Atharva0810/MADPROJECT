package com.example.madprojectmicro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class sqlitemanager extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteManager";

    // Database Name and Version
    private static final String DATABASE_NAME = "hotel_management.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ADMINS = "admins";
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_BOOKINGS = "bookings";

    // Common Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Admins Table Columns
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Customers Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_NATIONALITY = "nationality";
    private static final String KEY_AADHAR = "aadhar";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";

    // Rooms Table Columns
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_ROOM_TYPE = "room_type";
    private static final String KEY_NO_OF_BEDS = "no_of_beds";
    private static final String KEY_RATE = "rate";
    private static final String KEY_STATUS = "status";

    // Bookings Table Columns
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_ROOM_ID = "room_id";
    private static final String KEY_CHECK_IN_DATE = "check_in_date";
    private static final String KEY_CHECK_OUT_DATE = "check_out_date";
    private static final String KEY_NO_OF_DAYS = "no_of_days";
    private static final String KEY_BOOKING_STATUS = "status";
    private static final String KEY_PAYMENT_STATUS = "payment_status";
    private static final String KEY_PAYMENT_DATE = "payment_date";

    // Singleton instance
    private static sqlitemanager instance;

    // Private constructor for Singleton pattern
    private sqlitemanager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton getInstance method
    public static synchronized sqlitemanager getInstance(Context context) {
        if (instance == null) {
            instance = new sqlitemanager(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create admin table
        String CREATE_ADMINS_TABLE = "CREATE TABLE " + TABLE_ADMINS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
                + KEY_PASSWORD + " TEXT NOT NULL,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_ADMINS_TABLE);

        // Create customers table
        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_MOBILE + " TEXT NOT NULL,"
                + KEY_GENDER + " TEXT,"
                + KEY_NATIONALITY + " TEXT,"
                + KEY_AADHAR + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_CUSTOMERS_TABLE);

        // Create rooms table
        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ROOM_NUMBER + " TEXT UNIQUE NOT NULL,"
                + KEY_ROOM_TYPE + " TEXT NOT NULL,"
                + KEY_NO_OF_BEDS + " INTEGER DEFAULT 1,"
                + KEY_RATE + " REAL NOT NULL,"
                + KEY_STATUS + " TEXT DEFAULT 'available',"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        // Create bookings table
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CUSTOMER_ID + " INTEGER NOT NULL,"
                + KEY_ROOM_ID + " INTEGER NOT NULL,"
                + KEY_CHECK_IN_DATE + " DATE NOT NULL,"
                + KEY_CHECK_OUT_DATE + " DATE,"
                + KEY_NO_OF_DAYS + " INTEGER NOT NULL,"
                + KEY_BOOKING_STATUS + " TEXT DEFAULT 'checked_in',"
                + KEY_PAYMENT_STATUS + " TEXT DEFAULT 'pending',"
                + KEY_PAYMENT_DATE + " DATE,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + KEY_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        // Insert default admin
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_USERNAME, "admin");
        adminValues.put(KEY_PASSWORD, "mes@123");
        db.insert(TABLE_ADMINS, null, adminValues);

        // Insert default rooms
        for (int i = 1; i <= 20; i++) {
            ContentValues roomValues = new ContentValues();
            roomValues.put(KEY_ROOM_NUMBER, String.valueOf(i));
            roomValues.put(KEY_ROOM_TYPE, i <= 10 ? "Standard" : "Deluxe");
            roomValues.put(KEY_NO_OF_BEDS, i % 3 == 0 ? 2 : 1);
            roomValues.put(KEY_RATE, i <= 10 ? 1000.0 : 2000.0);
            roomValues.put(KEY_STATUS, "available");
            db.insert(TABLE_ROOMS, null, roomValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);

        // Create tables again
        onCreate(db);
    }

    // Enable foreign key constraints
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Authenticate admin user
    public void authenticateAdmin(String username, String password, AuthCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] columns = {KEY_ID};
            String selection = KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            Cursor cursor = db.query(TABLE_ADMINS, columns, selection, selectionArgs, null, null, null);
            boolean isAuthenticated = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            callback.onSuccess(isAuthenticated);
        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Get available room
    public void getAvailableRoom(String roomType, RoomCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            // Log the room type for debugging
            Log.d("RoomAllocation", "Searching for available room of type: " + roomType);

            // Modify the query to handle different room types more flexibly
            String[] columns = {KEY_ID, KEY_ROOM_NUMBER};
            String selection = KEY_ROOM_TYPE + " = ? AND " + KEY_STATUS + " = ?";
            String[] selectionArgs = {roomType, "available"};
            String orderBy = KEY_ROOM_NUMBER + " ASC";
            String limit = "1";

            Cursor cursor = db.query(TABLE_ROOMS, columns, selection, selectionArgs, null, null, orderBy, limit);

            if (cursor != null && cursor.moveToFirst()) {
                int roomNoIndex = cursor.getColumnIndex(KEY_ROOM_NUMBER);
                String roomNo = cursor.getString(roomNoIndex);
                cursor.close();

                // Additional validation to ensure room number is not null or zero
                if (roomNo != null && !roomNo.isEmpty() && !"0".equals(roomNo)) {
                    int roomNumber = Integer.parseInt(roomNo);
                    Log.d("RoomAllocation", "Found available room: " + roomNumber);
                    callback.onRoomFound(roomNumber);
                } else {
                    Log.e("RoomAllocation", "Invalid room number found");
                    callback.onFailure("No valid rooms available");
                }
            } else {
                // If no rooms of the specified type are available, try a more flexible approach
                Log.d("RoomAllocation", "No rooms of type " + roomType + " available. Trying alternative types.");

                // First, check if we have any rooms, period
                String countQuery = "SELECT COUNT(*) FROM " + TABLE_ROOMS + " WHERE " + KEY_STATUS + " = 'available'";
                Cursor countCursor = db.rawQuery(countQuery, null);

                if (countCursor != null && countCursor.moveToFirst()) {
                    int availableRoomsCount = countCursor.getInt(0);
                    countCursor.close();

                    if (availableRoomsCount > 0) {
                        // We have available rooms, but not of the specified type
                        callback.onFailure("No rooms available of type " + roomType + ". Please choose a different room type.");
                    } else {
                        // Absolutely no rooms available
                        callback.onFailure("No rooms are currently available. Please try again later.");
                    }
                } else {
                    // Error in counting rooms
                    callback.onFailure("Error checking room availability");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding available room", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Add customer and allocate room
    public void addCustomer(Customer customer, AddCustomerCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        long customerId = 0;
        try {
            db.beginTransaction();

            // Insert customer
            ContentValues customerValues = new ContentValues();
            customerValues.put(KEY_NAME, customer.getName());
            customerValues.put(KEY_MOBILE, customer.getMobileNo());
            customerValues.put(KEY_GENDER, customer.getGender());
            customerValues.put(KEY_NATIONALITY, customer.getNationality());
            customerValues.put(KEY_AADHAR, customer.getAadhar());
            customerValues.put(KEY_ADDRESS, customer.getAddress());
            customerValues.put(KEY_EMAIL, customer.getEmail());
            customerValues.put(KEY_PHONE, customer.getPhone());

            customerId = db.insert(TABLE_CUSTOMERS, null, customerValues);
            if (customerId == -1) {
                throw new Exception("Failed to add customer");
            }

            // Improved room lookup: Verify room exists and is available
            String[] columns = {KEY_ID, KEY_STATUS};
            String selection = KEY_ROOM_NUMBER + " = ?";
            String[] selectionArgs = {String.valueOf(customer.getRoomNoAllocated())};

            Cursor cursor = db.query(TABLE_ROOMS, columns, selection, selectionArgs, null, null, null);

            if (cursor == null || !cursor.moveToFirst()) {
                throw new Exception("Room " + customer.getRoomNoAllocated() + " not found");
            }

            int statusIndex = cursor.getColumnIndex(KEY_STATUS);
            String roomStatus = cursor.getString(statusIndex);

            if (!"available".equals(roomStatus)) {
                cursor.close();
                throw new Exception("Room " + customer.getRoomNoAllocated() + " is not available");
            }

            int roomIdIndex = cursor.getColumnIndex(KEY_ID);
            long roomId = cursor.getLong(roomIdIndex);
            cursor.close();

            // Calculate check-in date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String checkInDateStr = dateFormat.format(customer.getCheckInDate());

            // Create booking
            ContentValues bookingValues = new ContentValues();
            bookingValues.put(KEY_CUSTOMER_ID, customerId);
            bookingValues.put(KEY_ROOM_ID, roomId);
            bookingValues.put(KEY_CHECK_IN_DATE, checkInDateStr);
            bookingValues.put(KEY_NO_OF_DAYS, customer.getNoOfDays());

            long bookingId = db.insert(TABLE_BOOKINGS, null, bookingValues);
            if (bookingId == -1) {
                throw new Exception("Failed to create booking");
            }

            // Update room status
            ContentValues roomValues = new ContentValues();
            roomValues.put(KEY_STATUS, "occupied");

            int rowsAffected = db.update(TABLE_ROOMS, roomValues, KEY_ID + " = ?",
                    new String[]{String.valueOf(roomId)});
            if (rowsAffected == 0) {
                throw new Exception("Failed to update room status");
            }

            db.setTransactionSuccessful();
            callback.onSuccess(customer.getRoomNoAllocated());
        } catch (Exception e) {
            Log.e(TAG, "Error adding customer", e);
            // Remove the newly added customer if any of the subsequent steps fail
            if (customerId != -1) {
                db.delete(TABLE_CUSTOMERS, KEY_ID + " = ?", new String[]{String.valueOf(customerId)});
            }
            callback.onFailure(e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Checkout customer
    public void checkoutCustomer(int roomNumber, CheckoutCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            // Validate room number
            if (roomNumber <= 0) {
                throw new Exception("Invalid room number");
            }

            // Get room ID with additional validation
            String[] roomColumns = {KEY_ID};
            String roomSelection = KEY_ROOM_NUMBER + " = ? AND " + KEY_STATUS + " = ?";
            String[] roomSelectionArgs = {String.valueOf(roomNumber), "occupied"};

            Cursor roomCursor = db.query(TABLE_ROOMS, roomColumns, roomSelection, roomSelectionArgs, null, null, null);
            if (roomCursor == null || !roomCursor.moveToFirst()) {
                if (roomCursor != null) {
                    roomCursor.close();
                }
                throw new Exception("Room " + roomNumber + " is not occupied or does not exist");
            }

            int roomIdIndex = roomCursor.getColumnIndex(KEY_ID);
            long roomId = roomCursor.getLong(roomIdIndex);
            roomCursor.close();

            // Verify active booking exists
            String[] bookingColumns = {KEY_ID, KEY_CUSTOMER_ID};
            String bookingSelection = KEY_ROOM_ID + " = ? AND " + KEY_BOOKING_STATUS + " = ?";
            String[] bookingSelectionArgs = {String.valueOf(roomId), "checked_in"};

            Cursor bookingCursor = db.query(TABLE_BOOKINGS, bookingColumns, bookingSelection, bookingSelectionArgs, null, null, null);
            if (bookingCursor == null || !bookingCursor.moveToFirst()) {
                if (bookingCursor != null) {
                    bookingCursor.close();
                }
                throw new Exception("No active booking found for room " + roomNumber);
            }

            int bookingIdIndex = bookingCursor.getColumnIndex(KEY_ID);
            int customerIdIndex = bookingCursor.getColumnIndex(KEY_CUSTOMER_ID);
            long bookingId = bookingCursor.getLong(bookingIdIndex);
            long customerId = bookingCursor.getLong(customerIdIndex);
            bookingCursor.close();

            // Update booking status
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentDate = dateFormat.format(new Date());

            ContentValues bookingValues = new ContentValues();
            bookingValues.put(KEY_BOOKING_STATUS, "checked_out");
            bookingValues.put(KEY_CHECK_OUT_DATE, currentDate);

            int bookingRowsAffected = db.update(TABLE_BOOKINGS, bookingValues,
                    KEY_ID + " = ?", new String[]{String.valueOf(bookingId)});

            if (bookingRowsAffected == 0) {
                throw new Exception("Failed to update booking status");
            }

            // Update room status
            ContentValues roomValues = new ContentValues();
            roomValues.put(KEY_STATUS, "available");

            int roomRowsAffected = db.update(TABLE_ROOMS, roomValues,
                    KEY_ID + " = ?", new String[]{String.valueOf(roomId)});

            if (roomRowsAffected == 0) {
                throw new Exception("Failed to update room status");
            }

            db.setTransactionSuccessful();

            // Generate bill number (booking ID + timestamp)
            String billNo = "BILL-" + bookingId + "-" + System.currentTimeMillis();

            // Log checkout details for debugging
            Log.d("RoomCheckout", "Room " + roomNumber + " checked out. Customer ID: " + customerId + ", Bill No: " + billNo);

            callback.onSuccess(roomNumber, billNo);
        } catch (Exception e) {
            Log.e(TAG, "Error checking out customer", e);
            callback.onFailure(e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void getCheckedInRoom(int customerId, RoomCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT r." + KEY_ROOM_NUMBER +
                    " FROM " + TABLE_BOOKINGS + " b " +
                    " JOIN " + TABLE_ROOMS + " r ON b." + KEY_ROOM_ID + " = r." + KEY_ID +
                    " WHERE b." + KEY_CUSTOMER_ID + " = ? " +
                    " AND b." + KEY_BOOKING_STATUS + " = 'checked_in'";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(customerId)});

            if (cursor != null && cursor.moveToFirst()) {
                int roomNumberIndex = cursor.getColumnIndex(KEY_ROOM_NUMBER);
                int roomNumber = cursor.getInt(roomNumberIndex);
                cursor.close();

                callback.onRoomFound(roomNumber);
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                callback.onFailure("No active room found for this customer");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding customer's room", e);
            callback.onFailure(e.getMessage());
        }
    }
    // Get all customers
    public void getAllCustomers(CustomerListCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Customer> customers = new ArrayList<>();

        try {
            // Modified query to join with bookings to check occupancy status
            String selectQuery = "SELECT c.*, " +
                    "(SELECT COUNT(*) FROM " + TABLE_BOOKINGS + " b " +
                    "WHERE b." + KEY_CUSTOMER_ID + " = c." + KEY_ID + " " +
                    "AND b." + KEY_BOOKING_STATUS + " = 'checked_in') as is_occupied, " +
                    "(SELECT r." + KEY_ROOM_TYPE + " FROM " + TABLE_BOOKINGS + " b " +
                    "JOIN " + TABLE_ROOMS + " r ON b." + KEY_ROOM_ID + " = r." + KEY_ID + " " +
                    "WHERE b." + KEY_CUSTOMER_ID + " = c." + KEY_ID + " " +
                    "AND b." + KEY_BOOKING_STATUS + " = 'checked_in' LIMIT 1) as room_type, " +
                    "(SELECT r." + KEY_ROOM_NUMBER + " FROM " + TABLE_BOOKINGS + " b " +
                    "JOIN " + TABLE_ROOMS + " r ON b." + KEY_ROOM_ID + " = r." + KEY_ID + " " +
                    "WHERE b." + KEY_CUSTOMER_ID + " = c." + KEY_ID + " " +
                    "ORDER BY b." + KEY_ID + " DESC LIMIT 1) as room_number " +
                    "FROM " + TABLE_CUSTOMERS + " c " +
                    "ORDER BY c." + KEY_ID + " DESC";

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    customer.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                    customer.setMobileNo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_MOBILE)));

                    // Handle nullable fields
                    int emailIndex = cursor.getColumnIndex(KEY_EMAIL);
                    if (emailIndex != -1 && !cursor.isNull(emailIndex)) {
                        customer.setEmail(cursor.getString(emailIndex));
                    }

                    int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
                    if (phoneIndex != -1 && !cursor.isNull(phoneIndex)) {
                        customer.setPhone(cursor.getString(phoneIndex));
                    }

                    int genderIndex = cursor.getColumnIndex(KEY_GENDER);
                    if (genderIndex != -1 && !cursor.isNull(genderIndex)) {
                        customer.setGender(cursor.getString(genderIndex));
                    }

                    int nationalityIndex = cursor.getColumnIndex(KEY_NATIONALITY);
                    if (nationalityIndex != -1 && !cursor.isNull(nationalityIndex)) {
                        customer.setNationality(cursor.getString(nationalityIndex));
                    }

                    int aadharIndex = cursor.getColumnIndex(KEY_AADHAR);
                    if (aadharIndex != -1 && !cursor.isNull(aadharIndex)) {
                        customer.setAadhar(cursor.getString(aadharIndex));
                    }

                    int addressIndex = cursor.getColumnIndex(KEY_ADDRESS);
                    if (addressIndex != -1 && !cursor.isNull(addressIndex)) {
                        customer.setAddress(cursor.getString(addressIndex));
                    }

                    // Get room information
                    int roomTypeIndex = cursor.getColumnIndex("room_type");
                    if (roomTypeIndex != -1 && !cursor.isNull(roomTypeIndex)) {
                        customer.setRoomType(cursor.getString(roomTypeIndex));
                    }

                    int roomNumberIndex = cursor.getColumnIndex("room_number");
                    if (roomNumberIndex != -1 && !cursor.isNull(roomNumberIndex)) {
                        String roomNumber = cursor.getString(roomNumberIndex);
                        if (roomNumber != null && !roomNumber.isEmpty()) {
                            customer.setRoomNoAllocated(Integer.parseInt(roomNumber));
                        }
                    }

                    // Set occupied status based on the query
                    int isOccupiedIndex = cursor.getColumnIndex("is_occupied");
                    if (isOccupiedIndex != -1) {
                        int isOccupied = cursor.getInt(isOccupiedIndex);
                        customer.setOccupiedOrNot(isOccupied > 0);

                        // Log the status for debugging
                        Log.d(TAG, "Customer " + customer.getName() + " occupied status: " + (isOccupied > 0));
                    }

                    customers.add(customer);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            callback.onSuccess(customers);
        } catch (Exception e) {
            Log.e(TAG, "Error getting customers", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Get all hotels (rooms)
    public void getAllHotels(HotelListCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Hotel> hotels = new ArrayList<>();

        try {
            String selectQuery = "SELECT * FROM " + TABLE_ROOMS + " ORDER BY " + KEY_ROOM_NUMBER;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Hotel hotel = new Hotel();
                    hotel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                    hotel.setRoomNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_NUMBER)));
                    hotel.setRoomType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_TYPE)));
                    hotel.setNoOfBeds(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NO_OF_BEDS)));
                    hotel.setRate(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_RATE)));
                    hotel.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));

                    hotels.add(hotel);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            callback.onSuccess(hotels);
        } catch (Exception e) {
            Log.e(TAG, "Error getting hotels", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Get bill details
    public void getBillDetails(int customerId, int roomId, BillDetailsCallback callback) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT c.name, r.room_number, b.check_in_date, b.check_out_date, " +
                    "r.rate, JULIANDAY(b.check_out_date) - JULIANDAY(b.check_in_date) as total_days " +
                    "FROM " + TABLE_BOOKINGS + " b " +
                    "JOIN " + TABLE_CUSTOMERS + " c ON b.customer_id = c.id " +
                    "JOIN " + TABLE_ROOMS + " r ON b.room_id = r.id " +
                    "WHERE b.customer_id = ? AND b.room_id = ? AND b.status = 'checked_out'";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(customerId), String.valueOf(roomId)});

            if (cursor != null && cursor.moveToFirst()) {
                String customerName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String roomNumber = cursor.getString(cursor.getColumnIndexOrThrow("room_number"));
                String checkInDate = cursor.getString(cursor.getColumnIndexOrThrow("check_in_date"));
                String checkOutDate = cursor.getString(cursor.getColumnIndexOrThrow("check_out_date"));
                double roomRate = cursor.getDouble(cursor.getColumnIndexOrThrow("rate"));
                int totalDays = cursor.getInt(cursor.getColumnIndexOrThrow("total_days"));

                // Ensure at least 1 day is charged
                if (totalDays <= 0) totalDays = 1;

                double totalAmount = roomRate * totalDays;

                cursor.close();
                callback.onSuccess(customerName, roomNumber, checkInDate, checkOutDate, roomRate, totalDays, totalAmount);
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                callback.onFailure("No bill details found for this customer and room");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting bill details", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Update payment status
    public void updatePaymentStatus(int customerId, int roomId, PaymentCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            // Get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            // Update booking payment status
            String query = "UPDATE " + TABLE_BOOKINGS + " SET " + KEY_PAYMENT_STATUS + " = 'paid', " +
                    KEY_PAYMENT_DATE + " = ? " +
                    "WHERE " + KEY_CUSTOMER_ID + " = ? AND " + KEY_ROOM_ID + " = ? AND " +
                    KEY_BOOKING_STATUS + " = 'checked_out'";

            db.execSQL(query, new String[]{currentDate, String.valueOf(customerId), String.valueOf(roomId)});

            db.setTransactionSuccessful();
            callback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, "Error updating payment status", e);
            callback.onFailure(e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public void updatePaymentStatus(int billId, PaymentCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            // Get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            // Update booking payment status
            ContentValues values = new ContentValues();
            values.put(KEY_PAYMENT_STATUS, "paid");
            values.put(KEY_PAYMENT_DATE, currentDate);

            int rowsAffected = db.update(TABLE_BOOKINGS, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(billId)});

            if (rowsAffected == 0) {
                throw new Exception("Failed to update payment status");
            }

            db.setTransactionSuccessful();
            callback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, "Error updating payment status", e);
            callback.onFailure(e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // Initialize database tables (for first run)
    public void initializeDatabaseTables(InitCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Check if tables exist and have data
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ROOMS, null);
            boolean hasData = false;

            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasData = count > 0;
                cursor.close();
            }

            // If no data, initialize with default values
            if (!hasData) {
                db.beginTransaction();

                // Insert default admin if not exists
                ContentValues adminValues = new ContentValues();
                adminValues.put(KEY_USERNAME, "admin");
                adminValues.put(KEY_PASSWORD, "mes@123");
                db.insert(TABLE_ADMINS, null, adminValues);

                // Insert default rooms
                for (int i = 1; i <= 20; i++) {
                    ContentValues roomValues = new ContentValues();
                    roomValues.put(KEY_ROOM_NUMBER, String.valueOf(i));
                    roomValues.put(KEY_ROOM_TYPE, i <= 10 ? "Standard" : "Deluxe");
                    roomValues.put(KEY_NO_OF_BEDS, i % 3 == 0 ? 2 : 1);
                    roomValues.put(KEY_RATE, i <= 10 ? 1000.0 : 2000.0);
                    roomValues.put(KEY_STATUS, "available");
                    db.insert(TABLE_ROOMS, null, roomValues);
                }

                db.setTransactionSuccessful();
                db.endTransaction();
            }

            callback.onSuccess();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
            callback.onFailure(e.getMessage());
        }
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(boolean isAuthenticated);
        void onFailure(String errorMessage);
    }

    public interface RoomCallback {
        void onRoomFound(int roomNo);
        void onFailure(String errorMessage);
    }

    public interface AddCustomerCallback {
        void onSuccess(int roomNo);
        void onFailure(String errorMessage);
    }

    public interface CheckoutCallback {
        void onSuccess(int roomNo, String billNo);
        void onFailure(String errorMessage);
    }

    public interface CustomerListCallback {
        void onSuccess(List<Customer> customers);
        void onFailure(String errorMessage);
    }

    public interface HotelListCallback {
        void onSuccess(List<Hotel> hotels);
        void onFailure(String errorMessage);
    }

    public interface BillDetailsCallback {
        void onSuccess(String customerName, String roomNumber, String checkInDate,
                       String checkOutDate, double roomRate, int totalDays, double totalAmount);
        void onFailure(String errorMessage);
    }

    public interface PaymentCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface BillListCallback {
        void onSuccess(List<Bill> bills);
        void onFailure(String errorMessage);
    }

    public interface InitCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
