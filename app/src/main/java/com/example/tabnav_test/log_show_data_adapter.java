package com.example.tabnav_test;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.tabnav_test.Log.log_database_ops;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class log_show_data_adapter extends Adapter<log_show_data_adapter.ViewHolder>
{
    public String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR="0";
    Basic_funct bsf;
    log_database_ops log;
    projekt_ops projekt;

    public log_show_data_adapter(String[] localDataSet)
    {
        this.localDataSet = localDataSet;
        spinnerops =new log_fav(context);
        bsf=new Basic_funct();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context =parent.getContext();
        par = parent;
        log = new log_database_ops(context);
        projekt = new projekt_ops(context);
        View view = LayoutInflater.from(context).inflate(R.layout.log_show_data_layout, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        ContentValues data = new ContentValues();
        if(this.localDataSet[position] != null )
        {
            String[] colum_names = new String[]{"ID","PROJEKT_NR","DATE","TIME","NOTE" ,"CHECK_FLAG" ,"FAV_FLAG"};

            try {
                String[] entry_data = this.localDataSet[position].split(",");

                int item_pos=0;
                for(String c : colum_names)
                {
                   data.put(c, entry_data[item_pos]);
                   item_pos++;
                }
                Log.d("BASI", data.toString());

                try {
                    viewHolder.datum.setText(bsf.convert_date(data.get("DATE").toString(),"format_database_to_readable"));
                } catch (Exception e)
                {
                    viewHolder.datum.setText(data.get("DATE").toString());
                    exmsg("050220231110",e);
                    e.printStackTrace();
                }

                viewHolder.zeit.setText(data.get("TIME").toString());

                try {
                    viewHolder.notiz.setText(bsf.URLdecode(data.get("NOTE").toString()));
                } catch (Exception e)
                {
                    viewHolder.notiz.setText("");
                    exmsg("050220231111",e);
                    e.printStackTrace();
                }

                viewHolder.delete_entry.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Bestätige:");
                        alertDialogBuilder.setMessage("Element aus der Datenbank entfernen?");
                        alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                try {
                                    if (log.delet(data.get("ID").toString()))
                                    {
                                        Toast.makeText(context, "Eintrag gelöscht!", Toast.LENGTH_SHORT).show();
                                        localDataSet = log.get_entrys(projekt.projekt_get_selected_id());
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,localDataSet.length);
                                    } else
                                    {
                                        Toast.makeText(context, "Error: response false\n Eintrag wurde nicht gelöscht! ", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialogBuilder.show();
                    }});

                viewHolder.log_update_entry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        LayoutInflater inflater  = LayoutInflater.from(context);
                        View update_dialog_view = inflater.inflate(R.layout.log_show_data_layout_update_dialog,par,false);

                        TextInputEditText log_update_text_Edit = update_dialog_view.findViewById(R.id.log_update_text_Edit);

                        log_update_text_Edit.setText(bsf.URLdecode(data.get("NOTE").toString()));

                        AlertDialog.Builder update_dialog = new AlertDialog.Builder(context);

                        update_dialog.setTitle("Eintrag ändern");
                        update_dialog.setView(update_dialog_view);
                        update_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                try {

                                    data.put("NOTE",bsf.URLencode(log_update_text_Edit.getText().toString()));
                                    log.udpate(data);
                                    localDataSet  = log.get_entrys(projekt.projekt_get_selected_id());
                                    notifyDataSetChanged();

                                } catch (Exception e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        update_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        update_dialog.show();
                    }
                });


                if(log.get_check(data.get("ID").toString())==true)
                {
                    viewHolder.log_set_unset_check.setBackgroundColor(ContextCompat.getColor(context, R.color.grün));
                }
                else
                {
                    viewHolder.log_set_unset_check.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                }

                viewHolder.log_set_unset_check.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Boolean state = log.get_check(data.get("ID").toString());

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Bestätige:");

                        if (state)
                        {
                            alertDialogBuilder.setMessage("Ausgewählter Eintrag zu unerledigt abändern?");
                        } else if (!(state))
                        {
                            alertDialogBuilder.setMessage("Ausgewählter Eintrag zu erledigt abändern?");
                        }

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            //Hin und herschalten (Toogeln)
                            if (state) {
                                if (log.set_check(data.get("ID").toString(), false)) {
                                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                                } else {
                                    Toast.makeText(context, "Error: Write Response false", Toast.LENGTH_SHORT).show();
                                }
                            } else if (!(state)) {


                                if (log.set_check(data.get("ID").toString(), true)) {
                                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.grün));

                                } else {
                                    Toast.makeText(context, "Error: Write Response false", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.show();
                    }
                });


                if(log.get_flav_flag(data.get("ID").toString())==true)
                {
                    viewHolder.log_set_unset_star.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                }
                else
                {
                    viewHolder.log_set_unset_star.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                }

                viewHolder.log_set_unset_star.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String entry_id = data.get("ID").toString();

                        Boolean status = log.get_flav_flag(entry_id);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Bestätige:");

                        if(status == true)
                        {
                            alertDialogBuilder.setMessage("Ausgewählter Eintrag aus den Favoriten entfernen?");

                        }
                        else
                        {
                            alertDialogBuilder.setMessage("Ausgewählter Eintrag aus den Favoriten hinzüfügen?");
                        }

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (status == true)
                                {
                                    if(log.set_fav_flag(entry_id,false)  == true)
                                    {
                                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Error: Write Response false", Toast.LENGTH_SHORT).show();
                                    }

                                } else if (status== false)
                                {

                                    if(log.set_fav_flag(entry_id,true)  == true)
                                    {
                                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Error: Write Response false", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialogBuilder.show();

                    }
                });


                viewHolder.log_clipboard.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("LOG", remove_private_notes(bsf.URLdecode(data.get("NOTE").toString())));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Notiz in die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.log_share_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,remove_private_notes(bsf.URLdecode(data.get("NOTE").toString())));
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        context.startActivity(shareIntent,null);
                    }
                });


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }
    public String remove_private_notes(String input)
    {
        if(input.contains("#"))
        {
            String[] part = input.split("#");
            return part[2];
        }
        else
        {
            return input;
        }
    }




    private  void refresh_dataset(String [] new_dataset)
    {
        this.localDataSet = new_dataset;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView datum;
        private final TextView zeit;
        private final TextView notiz;

        private final ImageButton delete_entry;
        private final ImageButton log_update_entry;
        private final ImageButton log_set_unset_star;
        private final ImageButton log_set_unset_check;
        private final ImageButton log_clipboard;
        private final ImageButton log_share_button;
        public TransitionValues rec_view_background;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            datum = itemView.findViewById(R.id.textView3);
            zeit = itemView.findViewById(R.id.textView10);
            notiz = itemView.findViewById(R.id.textView16);
            delete_entry = itemView.findViewById(R.id.show_log_delet_entry);
            log_update_entry = itemView.findViewById(R.id.log_update_entry);
            log_set_unset_star = itemView.findViewById(R.id.log_set_star);
            log_set_unset_check = itemView.findViewById(R.id.log_check_button);
            log_clipboard = itemView.findViewById(R.id.log_clipboard_button);
            log_share_button = itemView.findViewById(R.id.log_share_button);


        }

        public TextView getTextView()
        {
            return notiz;
        }

    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: log_show_data_adapter ->","ID: "+msg+" Message:" + e.getMessage());
        e.printStackTrace();
    }





}
