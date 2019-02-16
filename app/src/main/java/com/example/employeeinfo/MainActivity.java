package com.example.employeeinfo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.employeeinfo.Utils.RecyclerTouchListener;
import com.example.employeeinfo.db.EmployeeDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noContentAvailable;
    private EmployeeAdapter mAdapter;
    private EmployeeDatabase db;
    private List<EmployeeModel> employeeInfoList = new ArrayList<>();
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEmployeeDialog();
            }
        });

        db = new EmployeeDatabase(this);
        employeeInfoList.addAll(db.getAllInfo());
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        noContentAvailable = (TextView)findViewById(R.id.no_info_available);
        mAdapter = new EmployeeAdapter(this, employeeInfoList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        toogleEmptyList();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //openEditDeleteDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                openEditDeleteDialog(position);
            }
        }));
    }

    private void openEditDeleteDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showEditEmployeeDialog(position);
                } else {
                    deleteEmployeeInfo(position);
                }
            }
        });
        builder.show();
    }

    private void deleteEmployeeInfo(int position) {
        Log.d("Position","Deleting item from pos: "+ position);
        if(position > -1) {
            if (db != null) {
                db.deleteEmployeeInfo(employeeInfoList.get(position));
            }
            employeeInfoList.remove(position);
            Toast.makeText(this, "Deleted from Position: " + position, Toast.LENGTH_SHORT).show();
            mAdapter.notifyItemRemoved(position);
            toogleEmptyList();
        }
    }

    private void showEditEmployeeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.edit_emp_dialog, null);
        builder.setView(view);
        final EditText edit_name = (EditText)view.findViewById(R.id.edit_name);
        final EditText edit_dept = (EditText)view.findViewById(R.id.edit_dept);
        final EditText edit_gender = (EditText)view.findViewById(R.id.edit_gender);
        builder.setTitle("Update Details");
        builder.setCancelable(false);
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateEmployee(position,edit_name.getText().toString(),edit_dept.getText().toString(),edit_gender.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelClick(dialog);
            }
        });
        builder.show();
    }

    private void updateEmployee(int position, String name, String dept,String gender) {
        EmployeeModel model;
        if (position > -1){
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(dept) && !TextUtils.isEmpty(gender)) {
                model = employeeInfoList.get(position);
                model.setEmp_name(name);
                model.setEmp_dept(dept);
                model.setEmp_gender(gender);
                if (model != null) {
                    int id = db.updateEmployeeInfo(model);
                    if (id < 0) {
                        Toast.makeText(this, "Data not updated", Toast.LENGTH_SHORT).show();
                    } else {
                        employeeInfoList.set(position, model);
                        mAdapter.notifyItemChanged(position);
                        toogleEmptyList();
                        Toast.makeText(this, "Data updated at position: " + position, Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(this, "Please enter valid data", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void toogleEmptyList() {
        if (db.getEmployeesCount() > 0){
            Log.d("Count","Total data: "+db.getEmployeesCount());
            noContentAvailable.setVisibility(View.GONE);
        }else {
            noContentAvailable.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEmployeeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.new_emp_dialog, null);
        builder.setView(view);
        final EditText enter_name = (EditText)view.findViewById(R.id.new_name);
        final EditText enter_dept = (EditText)view.findViewById(R.id.new_dept);
        final EditText enter_gender = (EditText)view.findViewById(R.id.new_gender);
        builder.setTitle("Enter Details");
        builder.setCancelable(false);
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                addEmployee(enter_name.getText().toString(),enter_dept.getText().toString(),enter_gender.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancelClick(dialog);
            }
        });
        builder.show();
    }

    private void addEmployee(String name, String department,String gender) {
        long id = -1;
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(department) && !TextUtils.isEmpty(gender)) {
            id = db.insertInfo(name, department,gender);
            if (id == -1) {
                toast = Toast.makeText(this, "Data not inserted", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(this, "Data inserted", Toast.LENGTH_SHORT);
                toast.show();

                EmployeeModel model = db.getEmployeeData(id);
                if (model != null){
                    employeeInfoList.add(0,model);
                    mAdapter.notifyDataSetChanged();
                    toogleEmptyList();
                }
            }
        }
    }


    private void onCancelClick(DialogInterface dialog) {
        dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
