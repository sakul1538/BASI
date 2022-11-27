package com.example.tabnav_test;

public interface db_controlls
{

    public long addOne(String proj_id,String key,String value);
    public int updateOne( String proj_id,String name,String new_key,String new_value);
    public int deletOne(String proj_id, String name);
    public int deletAll(String proj_id);

    public String[] getall(String proj_id);
    public String[] getallkeys();
    public String[] getvalues();
}
