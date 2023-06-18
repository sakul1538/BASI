package com.example.tabnav_test;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class log_favorite_settings_adapter extends Adapter<log_favorite_settings_adapter.ViewHolder>
{
    private String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR="0";
    Basic_funct bsf;

    public log_favorite_settings_adapter(String[] localDataSet)
    {
        this.localDataSet = localDataSet;
    }
    @NonNull
    @Override
    public log_favorite_settings_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context =parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.log_fav_rec_view, parent, false);
        spinnerops =new log_fav(context);
        bsf=new Basic_funct();

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull log_favorite_settings_adapter.ViewHolder viewHolder, int position)
    {
        viewHolder.getTextView().setText(localDataSet[position]);
        int posi = position;


        viewHolder.modify_button().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.log_fav_modify_dialog, par,false);

                    //EditText
                    EditText fav_mame =(EditText) promptsView.findViewById(R.id.log_fav_modify_edit);

                    //ImageButton
                    ImageButton log_modify_fav_reset = (ImageButton) promptsView.findViewById(R.id.log_modify_fav_reset);

                    fav_mame.setText(localDataSet[posi]);


                log_modify_fav_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        fav_mame.setText("");
                    }
                });

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    alertDialogBuilder.setTitle(R.string.log_fav_modify_title);

                    alertDialogBuilder.setPositiveButton("Ändern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try
                        {
                           int response = spinnerops.modifylogfav(RROJ_NR,localDataSet[posi],fav_mame.getText().toString());
                           if(response >0)
                           {
                               localDataSet[posi] =fav_mame.getText().toString();
                               notifyItemChanged(posi);
                               bsf.succes_msg("Eintrag wurde geändert!",context);
                           }
                           else
                           {
                               bsf.error_msg("Eintrag wurde nicht geändert! \nInterner Fehler oder existiert schon",context);
                           }
                        }
                        catch (Exception e)
                        {
                            exmsg("050220231230",e);
                            bsf.error_msg("Eintrag wurde nicht geändert! \nInterner Fehler oder existiert schon",context);
                            e.printStackTrace();
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();


                alertDialog.show();




              //  Toast.makeText(view.getContext(), "modify:"+localDataSet[posi],Toast.LENGTH_SHORT).show();

            }
        });

        viewHolder.delet_button().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String item_name= localDataSet[posi];
;


                AlertDialog.Builder alertDialogBuilder  = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Eintrag Löschen?");

                alertDialogBuilder.setMessage("\""+item_name+"\" aus der Datenbank entfernen?");

                alertDialogBuilder.setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try {
                            int response =spinnerops.deletlogfav(RROJ_NR,localDataSet[posi]);
                            if(response > 0)
                            {

                                localDataSet = spinnerops.getalllogfav(RROJ_NR); //Array Aktualisieren
                                notifyItemRemoved(posi);
                                notifyItemRangeChanged(posi,localDataSet.length);
                                bsf.succes_msg("Eintrag \""+item_name+"\" wurde gelöscht!",context);
                            }
                            else
                            {
                                bsf.error_msg("Eintrag \""+item_name+"\" wurde nicht geändert!\n\nAus folgenden möglichen Gründen: \n\n+Existiert nicht mehr\n+Doppelt vorhanden \n+Interner Fehler ",context);
                            }

                        } catch (Exception e) {
                            exmsg("050220231329",e);
                            bsf.error_msg("Eintrag  \""+item_name+"\" wurde nicht Gelöscht! \n Interner Fehler: \n"+e.getMessage().toString(),context);
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.show();
            }
        });
        viewHolder.get_fav_global_switch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                String response = spinnerops.favsetglobal(RROJ_NR, localDataSet[posi], b);
                Toast.makeText(compoundButton.getContext(), response, Toast.LENGTH_SHORT).show();
            }
        });

      // viewHolder.get_fav_global_switch().setChecked(spinnerops.favgetglobal(RROJ_NR, localDataSet[posi]));
    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView fav_name;
        private final ImageButton modify;
        private final ImageButton delet;
        private final Switch set_fav_global;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            fav_name = (TextView) itemView.findViewById(R.id.cat_name);
            modify = (ImageButton) itemView.findViewById(R.id.log_modify_cat);
            delet = (ImageButton) itemView.findViewById(R.id.log_modify_fav_reset);
            set_fav_global =(Switch) itemView.findViewById(R.id.set_cat_global);


        }

        public TextView getTextView()
        {
            return fav_name;
        }

        public ImageButton modify_button()
        {
            return modify;
        }
        public ImageButton delet_button()
        {
            return delet;
        }

        public Switch get_fav_global_switch()
        {
            return set_fav_global;
        }
    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: log_favorite_settings_adapter ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }




}
