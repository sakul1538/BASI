package com.example.tabnav_test.Listen;

import android.app.AlertDialog;
import android.content.Context;
import android.text.BoringLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class list_items_rcv_adapter extends RecyclerView.Adapter<list_items_rcv_adapter.ViewHolder>
{
    Context context;
    private String[] localDataSet = null;
    private String list_id = null;
    list_main_db_ops db_ops;
    private String items_id = "";
    private int Check_box_visibiltiy; //Not visible by default
    private boolean Check_box_status ; //Not checked by default
    public list_items_rcv_adapter(String[] a, String list_id, Context context)
    {
        this.Check_box_status = false;
        this.localDataSet = a;
        this.list_id =list_id;
        this.context = context;
    }

    @NonNull
    @Override
    public list_items_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        db_ops = new list_main_db_ops(context);
        items_id = db_ops.get_items_id(this.list_id);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        //Check if localdataset has data
        if(localDataSet[0].equals(""))
        {
            Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
            holder.background.setVisibility(View.GONE);
        }
        else
        {
            //Set the data
        holder.background.setVisibility(View.VISIBLE);
        String[] colum = localDataSet[position].split(",");
        String ID = colum[0];
        String NAME = colum[1];
        String PARENT_ID = colum[2];
        String POSITION = colum[3];
        String CHECK_FLAG = colum[4];

        holder.getItem_name().setText(NAME);


        holder.getItem_name().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update via dialog
                View alert_view_add_list = LayoutInflater.from(context).inflate(R.layout.update_item, null);

                EditText item_name = alert_view_add_list.findViewById(R.id.editTextText9);
                item_name.setText(NAME);
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(alert_view_add_list);
                alertDialogBuilder.setTitle("Bearbeiten");
                alertDialogBuilder.setPositiveButton("OK", (dialogInterface, i) ->
                {
                    //Get the new name from the dialog
                    String new_name = item_name.getText().toString();
                    //Update the item in the database
                    int s = db_ops.update_item(ID, new_name);
                    if (s > 0) {
                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                        //Reload the list
                        reload();
                    } else {
                        Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.cancel();
                });
                alertDialogBuilder.setNegativeButton("Abbrechen", (dialogInterface, i) ->
                {
                    dialogInterface.cancel();
                });
                alertDialogBuilder.show();
            }
        });

        if (CHECK_FLAG.equals("1")) {
            holder.background.setBackground(context.getResources().getDrawable(R.color.grün));
        } else {
            holder.background.setBackground(context.getResources().getDrawable(R.color.grey));
        }


        holder.getCheck_item().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check the status
                String status = db_ops.items_get_check_flag(ID);
                switch (status) {
                    case "0":
                        db_ops.set_item_check(ID, "1");
                        holder.background.setBackground(context.getResources().getDrawable(R.color.grün));
                        break;

                    case "1":

                        //Ask for confirmation
                        android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                        alertDialogBuilder.setTitle("Bestätigen");
                        alertDialogBuilder.setMessage("Item Zurücksetzen?");
                        alertDialogBuilder.setPositiveButton("Ja", (dialogInterface, i) ->
                        {
                            db_ops.set_item_check(ID, "0");
                            holder.background.setBackground(context.getResources().getDrawable(R.color.grey));
                            dialogInterface.cancel();
                            //Delete the item
                        });
                        alertDialogBuilder.setNegativeButton("Nein", (dialogInterface, i) ->
                        {
                            dialogInterface.cancel();
                        });
                        alertDialogBuilder.show();
                        break;

                    default:
                        Toast.makeText(context, "Error: Status unknown (-3)", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });


        holder.getList_item_check_box().setVisibility(Check_box_visibiltiy);
        holder.getList_item_check_box().setChecked(Check_box_status);

        holder.getDelete_item().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ask for confirmation
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Bestätigen");
                alertDialogBuilder.setMessage("Item Löschen?");
                alertDialogBuilder.setPositiveButton("Ja", (dialogInterface, i) ->
                {
                    int c= db_ops.delete_item(ID);
                    if(c>0)
                    {
                        Toast.makeText(context, "Gelöscht", Toast.LENGTH_SHORT).show();
                        reload();
                    }
                    else
                    {
                        Toast.makeText(context, "Delete failed (c<0", Toast.LENGTH_SHORT).show();
                    }

                    dialogInterface.cancel();
                    //Delete the item

                });
                alertDialogBuilder.setNegativeButton("Nein", (dialogInterface, i) ->
                {
                    dialogInterface.cancel();
                });
                alertDialogBuilder.show();
            }


        });
        holder.getAdd_to_fav_item().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //Add to favorites
                if(db_ops.check_fav_item_exist(ID)==0)
                {
                    db_ops.save_fav_item(NAME);
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(context, "Already in favorites", Toast.LENGTH_SHORT).show();
                }
            }

        });
        }
    }
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
    public void reload()
    {
        localDataSet = db_ops.get_items_form_list(items_id);
        notifyDataSetChanged();
    }
    public void refresh()
    {
        notifyDataSetChanged();
    }

    public void setCheck_box_visibiltiy(int Check_box_visibiltiy)
        {
        this.Check_box_visibiltiy = Check_box_visibiltiy;
        refresh();
    }
    public void set_all_checkboxes(Boolean Check_box_status)
    {
        this.Check_box_status = Check_box_status;
        Toast.makeText(context, "All checkboxes set to " + Check_box_status, Toast.LENGTH_SHORT).show();
        refresh();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
    TextView item_name;
    ImageButton check_item;
    ImageButton delete_item;
    ImageButton add_to_fav_item;
    LinearLayout background;
    CheckBox list_item_check_box;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            item_name = itemView.findViewById(R.id.textView100);
            check_item = itemView.findViewById(R.id.imageButton96);
            delete_item = itemView.findViewById(R.id.imageButton98);
            add_to_fav_item = itemView.findViewById(R.id.imageButton94);
            background = itemView.findViewById(R.id.item_background);
            list_item_check_box = itemView.findViewById(R.id.list_item_check_box);

        }
        public TextView getItem_name()
        {
            return item_name;
        }

        public ImageButton getCheck_item()
        {
            return check_item;
        }
        public ImageButton getDelete_item()
        {
            return delete_item;
        }
        public ImageButton getAdd_to_fav_item()
        {
            return add_to_fav_item;
        }
        public LinearLayout getBackground()
        {
            return background;
        }
        public CheckBox getList_item_check_box()
        {
            return list_item_check_box;
        }
    }
}
