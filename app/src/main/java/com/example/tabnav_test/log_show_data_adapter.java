package com.example.tabnav_test;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class log_show_data_adapter extends Adapter<log_show_data_adapter.ViewHolder>
{
    private String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR="0";
    Basic_funct bsf;

    public log_show_data_adapter(String[] localDataSet)
    {
        this.localDataSet = localDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context =parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.log_show_data_layout, parent, false);
        spinnerops =new log_fav(context);
        bsf=new Basic_funct();

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        String dataset [];
        String flag_state=null;
        int posi = position;
        String note_encoded = null;
        dataset = localDataSet[position].split(",");

        try {
            viewHolder.datum.setText(bsf.convert_date(dataset[2],"format_database_to_readable"));
        } catch (Exception e)
        {
            viewHolder.datum.setText(dataset[2]);
            exmsg("050220231110",e);
            e.printStackTrace();
        }

        viewHolder.zeit.setText(dataset[3]);
        viewHolder.kategorie.setText(dataset[4]);

        try {
            viewHolder.notiz.setText(bsf.URLdecode(dataset[5]));
        } catch (Exception e)
        {
            viewHolder.notiz.setText("");
            exmsg("050220231111",e);
            e.printStackTrace();
        }

        flag_state = spinnerops.log_get_flag_value(dataset[0],dataset[1],"FAV_FLAG");
        //Hin und herschalten (Toogeln)
        switch(flag_state)
        {

            case "TRUE":

                viewHolder.log_set_unset_star.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow));

                break;

            case "FALSE":
                viewHolder.log_set_unset_star.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_200));

                break;
            default:

        }

        flag_state = spinnerops.log_get_flag_value(dataset[0],dataset[1],"CHECK_FLAG");
        //Hin und herschalten (Toogeln)
        switch(flag_state)
        {

            case "TRUE":

                viewHolder.log_set_unset_check.setBackgroundColor( ContextCompat.getColor(context, R.color.hellgrün));
                break;

            case "FALSE":
                viewHolder.log_set_unset_check.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));

                break;
            default:

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
                        int response= spinnerops.delet_log_entry(dataset[0],dataset[1]);
                        Toast.makeText(context.getApplicationContext(),String.valueOf(response),Toast.LENGTH_SHORT).show();
                        localDataSet = spinnerops.getalllogdata(RROJ_NR);
                        notifyItemRemoved(posi);
                        notifyItemRangeChanged(posi,localDataSet.length);

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

        viewHolder.log_set_unset_star.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Bestätige:");
                alertDialogBuilder.setMessage("Ausgewählter Eintrag aus den Favoriten entfernen/hinzüfügen?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String flag_state =spinnerops.log_set_unset_flags(dataset[0],dataset[1],"FAV_FLAG");
                        //Hin und herschalten (Toogeln)
                        switch(flag_state)
                        {

                            case "TRUE":
                                view.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow));
                                break;
                            case "FALSE":
                                view.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_200));

                                break;
                            default:

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

        viewHolder.log_set_unset_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Bestätige:");

                String flag_state =  spinnerops.log_get_flag_value(dataset[0],dataset[1],"CHECK_FLAG");

                Toast.makeText(context, flag_state, Toast.LENGTH_SHORT).show();

                switch(flag_state)
                {
                    case "TRUE":
                        alertDialogBuilder.setMessage("Ausgewählter Eintrag zu unerledigt abändern?");
                        break;
                    case "FALSE":
                        alertDialogBuilder.setMessage("Ausgewählter Eintrag zu erledigt abändern?");
                        break;
                    default:
                }

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String flag_state =spinnerops.log_set_unset_flags(dataset[0],dataset[1],"CHECK_FLAG");
                        //Hin und herschalten (Toogeln)
                        switch(flag_state)
                        {
                            case "TRUE":
                                view.setBackgroundColor(ContextCompat.getColor(context, R.color.hellgrün));
                                break;
                            case "FALSE":
                                view.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200));

                                break;
                            default:
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
                ClipData clip = ClipData.newPlainText("LOG", bsf.URLdecode(dataset[5]));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Notiz in die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView datum;
        private final TextView zeit;
        private final TextView notiz;
        private final TextView kategorie;
        private final ImageButton delete_entry;
        private final ImageButton log_set_unset_star;
        private final ImageButton log_set_unset_check;
        private final ImageButton log_clipboard;
        public TransitionValues rec_view_background;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            datum = (TextView) itemView.findViewById(R.id.textView3);
            zeit = (TextView) itemView.findViewById(R.id.textView10);
            notiz = (TextView) itemView.findViewById(R.id.textView16);
            kategorie = (TextView) itemView.findViewById(R.id.textView20);
            delete_entry = (ImageButton) itemView.findViewById(R.id.show_log_delet_entry);
            log_set_unset_star = (ImageButton) itemView.findViewById(R.id.log_set_star);
            log_set_unset_check = (ImageButton) itemView.findViewById(R.id.log_check_button);
            log_clipboard = (ImageButton) itemView.findViewById(R.id.log_clipboard_button);






        }

        public TextView getTextView()
        {
            return notiz;
        }

    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: log_show_data_adapter ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }





}
