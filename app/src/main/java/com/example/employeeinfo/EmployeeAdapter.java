package com.example.employeeinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    Context context;
    List<EmployeeModel> employeeList = new ArrayList<>();

    public EmployeeAdapter(Context context,List<EmployeeModel> list) {
        this.context  = context;
        employeeList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
       View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.info_list,parent,false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        EmployeeModel model = employeeList.get(i);
        viewHolder.name.setText(model.getEmp_name());
        viewHolder.department.setText(model.getEmp_dept());
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,department;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.employee_name);
            department = (TextView)itemView.findViewById(R.id.employee_dept);
        }

    }
}
