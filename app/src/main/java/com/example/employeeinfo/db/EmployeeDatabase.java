package com.example.employeeinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.employeeinfo.EmployeeModel;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EmployeeInfo.db";
    //private static final int DATABASE_VERSION = 1;

    private static final int DATABASE_VERSION = 2;   // Incrementing the version

    public static final String TABLE_NAME = "employee";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DEPARTMENT = "department";

    // Adding new Column
    public static final String COLUMN_GENDER = "gender";

    public static final String TEMPORARY_TABLE_NAME = "employee_backup";        // Temporary table name

    Context mCtx;

    public EmployeeDatabase(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
        mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create table as you want it to be after onUpgrade() runs. ie: 4 columns.

        final String SQL_CREATE_EMP_INFO_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_DEPARTMENT + " TEXT,"
                        + COLUMN_GENDER + " TEXT"                   // New Column added
                        + ")";

        db.execSQL(SQL_CREATE_EMP_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply discard the data and start over

        /*
        Before adding gender column
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);   */

        // Changes after adding gender column
        // Create temporary table to hold data
        db.execSQL(
                "CREATE TABLE " + TEMPORARY_TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_DEPARTMENT + " TEXT"
                        + ")"
        );

        //insert data from old table into temp table
        db.execSQL("INSERT INTO employee_backup SELECT id, name, department FROM employee ");

        //drop the old table now that your data is safe in the temporary one
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);

        //recreate the table this time with all 4 columns
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_DEPARTMENT + " TEXT,"
                        + COLUMN_GENDER + " TEXT"
                        + ")"
        );

        //fill it up using null for the gender column
        db.execSQL("INSERT INTO employee SELECT id, name, department, null FROM employee_backup");

        //then drop the temporary table
        db.execSQL("DROP TABLE IF EXISTS "+   TEMPORARY_TABLE_NAME);

        onCreate(db);
    }

    public long insertInfo(String name,String department,String gender) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` will be inserted automatically.
        // no need to add it
        values.put( COLUMN_NAME, name);
        values.put(COLUMN_DEPARTMENT,department);

        values.put(COLUMN_GENDER,gender);  // To insert value for new column

        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public EmployeeModel getEmployeeData(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DEPARTMENT,COLUMN_GENDER},   //New column added
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        EmployeeModel employeeModel = new EmployeeModel(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DEPARTMENT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)));   //For new column

        // close the db connection
        cursor.close();

        return employeeModel;
    }

    public List<EmployeeModel> getAllInfo() {
        List<EmployeeModel> employee = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EmployeeModel model = new EmployeeModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                model.setEmp_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                model.setEmp_dept(cursor.getString(cursor.getColumnIndex(COLUMN_DEPARTMENT)));
                model.setEmp_gender(cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)));   //For new column

                employee.add(model);
            } while (cursor.moveToNext());
        }

        //  cursor.close();
        // close db connection
        db.close();

        // return notes list
        return employee;
    }

    public int getEmployeesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int updateEmployeeInfo(EmployeeModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, model.getEmp_name());
        values.put(COLUMN_DEPARTMENT,model.getEmp_dept());
        values.put(COLUMN_GENDER,model.getEmp_gender());  // For new Column

        // updating row
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(model.getId())});
    }

    public void deleteEmployeeInfo(EmployeeModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(model.getId())});
        db.close();
    }

}
