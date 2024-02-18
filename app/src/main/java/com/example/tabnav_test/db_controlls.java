package com.example.tabnav_test;

public interface db_controlls
{

    long addOne(String proj_id,String key,String value);
    int updateOne( String proj_id,String name,String new_key,String new_value);
    int deletOne(String proj_id, String name);
    int deletAll(String proj_id);

    String[] getall(String proj_id);
    String[] getallkeys();
    String[] getvalues();
}
