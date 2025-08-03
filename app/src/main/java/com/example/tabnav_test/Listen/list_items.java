package com.example.tabnav_test.Listen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class list_items extends AppCompatActivity
{
    String items_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        //Get extras from intent
        Bundle extras = getIntent().getExtras();
        String list_id = extras.getString("list_id");
        String list_name = extras.getString("list_name");
        String list_note = extras.getString("list_details");

        list_main_db_ops db_ops =new list_main_db_ops(getApplicationContext());
        items_id= db_ops.get_items_id(list_id);

        Log.d("BASI","items_id: "+items_id);
        RecyclerView list_items_rcv = findViewById(R.id.list_items_rcv);
        String items = db_ops.get_items_form_list(items_id);
        Log.d("BASI","items: "+items);
        String[] a = new String[0];
        if(!items.isEmpty())
        {

            a = items.split(",");
        }
                list_items_rcv_adapter items_adapter = new list_items_rcv_adapter(a,list_id,getApplicationContext());
                list_items_rcv.setAdapter(items_adapter);
                list_items_rcv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        AutoCompleteTextView autoCompleteTextView4 = findViewById(R.id.autoCompleteTextView4);


        ImageButton add_item = findViewById(R.id.imageButton91);
        ImageButton cancel_item = findViewById(R.id.imageButton92);


        add_item.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String value = autoCompleteTextView4.getText().toString();
                if(!value.isEmpty())
                {
                   long i = db_ops.add_item(items_id,value);
                   if(i > 0)
                   {
                      Toast.makeText(list_items.this, "Item added", Toast.LENGTH_SHORT).show();
                   }
                   else
                   {
                        Toast.makeText(list_items.this, "Item not added", Toast.LENGTH_SHORT).show();
                   }
                }
                else
                {
                    Toast.makeText(list_items.this, "Text leer", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}