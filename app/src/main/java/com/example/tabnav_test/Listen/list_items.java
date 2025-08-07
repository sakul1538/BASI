package com.example.tabnav_test.Listen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class list_items extends AppCompatActivity {
    String items_id = "";

    public list_items()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);


        // ----------------------------------------------------------------- Variablen
        // Get extras from intent
        Bundle extras = getIntent().getExtras();
        String list_id = extras.getString("list_id");
        String list_name = extras.getString("list_name");
        String list_note = extras.getString("list_details");

        // ----------------------------------------------------------------- Variablen String, char
        // ----------------------------------------------------------------- Variablen byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen Boolean
        // ----------------------------------------------------------------- Instanzen
        list_main_db_ops db_ops = new list_main_db_ops(getApplicationContext());
        items_id = db_ops.get_items_id(list_id);

        // ----------------------------------------------------------------- TextView

        // ----------------------------------------------------------------- AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView4 = findViewById(R.id.autoCompleteTextView4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, db_ops.get_fav_items());
        autoCompleteTextView4.setAdapter(adapter);
        // ----------------------------------------------------------------- EditText
        EditText piece_value = findViewById(R.id.editTextNumberDecimal6);
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        ImageButton add_item = findViewById(R.id.add_item);
        ImageButton textinput_clear = findViewById(R.id.textinput_clear);
        ImageButton show_spec_details = findViewById(R.id.show_spec_details);
        ImageButton set_color = findViewById(R.id.set_color);
        ImageButton items_filter = findViewById(R.id.items_filter); //Filterdialog
        ImageButton items_show_checkboxes = findViewById(R.id.items_show_checkboxes); //Show ImageButtons for selecting
        ImageButton items_select_none = findViewById(R.id.items_select_none); // Select none of the items
        ImageButton items_select_all = findViewById(R.id.items_select_all); // Select all of the items
        ImageButton items_selected_action = findViewById(R.id.items_selected_action); // Action on selected items in a Submenu or Dialog
        // ----------------------------------------------------------------- ImageView
        // ----------------------------------------------------------------- ListView
        // ----------------------------------------------------------------- RecyclerView
        RecyclerView list_items_rcv = findViewById(R.id.list_items_rcv);
        // ----------------------------------------------------------------- Spinner
        Spinner piece_unit = findViewById(R.id.spinner5);
        // ----------------------------------------------------------------- CheckBox
        // ----------------------------------------------------------------- RadioButton
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- SeekBar
        // ----------------------------------------------------------------- ProgressBar
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- ScrollView
        // ----------------------------------------------------------------- Layouts
        LinearLayout item_spec_detail_layout = findViewById(R.id.item_spec_detail_layout);
        // ----------------------------------------------------------------- END

        //Set data
        String[] items = db_ops.get_items_form_list(items_id);
        list_items_rcv_adapter items_adapter = new list_items_rcv_adapter(items, list_id, getApplicationContext());
        list_items_rcv.setAdapter(items_adapter);
        list_items_rcv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Listener
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_item:

                        String value = autoCompleteTextView4.getText().toString();

                        if (!value.isEmpty()) {
                            if (item_spec_detail_layout.getVisibility() == View.VISIBLE) {
                                if (!piece_value.getText().toString().isEmpty()) {
                                    value = value + " [" + piece_value.getText().toString() + " " + piece_unit.getSelectedItem().toString() + "]";
                                }
                            }

                            long i = db_ops.add_item(items_id, value);

                            if (i > 0) {
                                Toast.makeText(list_items.this, "Item added", Toast.LENGTH_SHORT).show();
                                //Refresh the list
                                items_adapter.reload();
                                autoCompleteTextView4.setText("");
                                piece_value.setText("");
                                piece_unit.setSelection(0);

                            } else {
                                Toast.makeText(list_items.this, "Item not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(list_items.this, "Text leer", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.textinput_clear:
                        autoCompleteTextView4.setText("");
                        piece_value.setText("");
                        piece_unit.setSelection(0);

                        break;

                    case R.id.show_spec_details:

                        if (item_spec_detail_layout.getVisibility() == View.GONE) {
                            piece_value.setText("");
                            piece_unit.setSelection(0);
                            item_spec_detail_layout.setVisibility(View.VISIBLE);
                        } else {
                            item_spec_detail_layout.setVisibility(View.GONE);
                            piece_value.setText("");
                            piece_unit.setSelection(0);
                        }

                        break;

                    case R.id.set_color:

                        //Show Color Choser
                        Dialog dialog = new Dialog(list_items.this);
                        dialog.setContentView(R.layout.color_chooser);
                        //buttons

                        //tODO add colors
                        /*Farben hinzufügen
                        In Datenbank erweitern mit spalte Color
                        Farben aus Datenbank laden
                        Farben in Liste anzeigen
                        Favoriten ebenfalls mit Color ergänzen, auf wunsch des users via Dialog*/


                        ImageButton red = dialog.findViewById(R.id.imageButton95);
                        ImageButton orange = dialog.findViewById(R.id.imageButton97);
                        ImageButton yellow = dialog.findViewById(R.id.imageButton101);
                        red.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                autoCompleteTextView4.setText("RED");
                            }
                        });


                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        dialog.setCancelable(true);
                        dialog.show();
                        break;

                    case R.id.items_filter:
                        fallback();

                        break;
                    case R.id.items_show_checkboxes:
                        toogle_checkboxes_menu(items_select_all, items_select_none, items_selected_action, items_adapter);

                        break;
                    case R.id.items_select_none:
                        items_adapter.set_all_checkboxes(false);
                        break;

                    case R.id.items_select_all:
                        items_adapter.set_all_checkboxes(true);
                        break;

                    case R.id.items_selected_action:
                        fallback();

                        break;

                    default:
                        Toast.makeText(list_items.this, "Error: Button not found", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        add_item.setOnClickListener(listener);
        textinput_clear.setOnClickListener(listener);
        show_spec_details.setOnClickListener(listener);
        set_color.setOnClickListener(listener);
        items_filter.setOnClickListener(listener);
        items_show_checkboxes.setOnClickListener(listener);
        items_select_none.setOnClickListener(listener);
        items_select_all.setOnClickListener(listener);
        items_selected_action.setOnClickListener(listener);

    }

    private void toogle_checkboxes_menu(ImageButton items_select_all, ImageButton items_select_none, ImageButton items_selected_action, list_items_rcv_adapter items_adapter)
    {
        if (items_select_none.getVisibility() == View.GONE)
        {
            items_select_all.setVisibility(View.VISIBLE);
            items_select_none.setVisibility(View.VISIBLE);
            items_selected_action.setVisibility(View.VISIBLE);
            items_adapter.setCheck_box_visibiltiy(View.VISIBLE);
        }
        else
        {
            items_select_all.setVisibility(View.GONE);
            items_select_none.setVisibility(View.GONE);
            items_selected_action.setVisibility(View.GONE);
            items_adapter.setCheck_box_visibiltiy(View.GONE);
        }

    }

    public void fallback()
    {
        Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show();
    }
    

}