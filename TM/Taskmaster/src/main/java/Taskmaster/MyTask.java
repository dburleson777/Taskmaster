/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Taskmaster;

/**
 *
 * @author David
 */
import java.io.Serializable;
import java.time.LocalDate;

public class MyTask implements Serializable{
    private String name;
    private String desc;
    private LocalDate deadline;
    private static final long serialVersionUID = 1L;
    
    public MyTask(){
        name = "";
        desc = "";
        deadline = null;
    }
    
    public MyTask(String n, String d, LocalDate dead){
        name = n;
        desc = d;
        deadline = dead;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDesc(){
        return desc;
    }
    
    public LocalDate getDeadline(){
        return deadline;
    }
    
    public void setName(String n){
        name = n;
    }
    
    public void setDesc(String d){
        desc = d;
    }
    
    public void setDate(LocalDate d){
        deadline = d;
    }
}
