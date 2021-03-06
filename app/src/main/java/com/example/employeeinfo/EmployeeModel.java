package com.example.employeeinfo;

public class EmployeeModel {

    private int id;
    private String emp_name;
    private String emp_dept;
    private String emp_gender;

    public EmployeeModel(){ };

    public EmployeeModel(int id, String emp_name, String emp_dept,String emp_gender) {
        this.id = id;
        this.emp_name = emp_name;
        this.emp_dept = emp_dept;
        this.emp_gender = emp_gender;
    }

    public String getEmp_gender() {
        return emp_gender;
    }

    public void setEmp_gender(String emp_gender) {
        this.emp_gender = emp_gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_dept() {
        return emp_dept;
    }

    public void setEmp_dept(String emp_dept) {
        this.emp_dept = emp_dept;
    }
}
