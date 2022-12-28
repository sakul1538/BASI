package com.example.tabnav_test;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Pick_image extends ActivityResultContract<Integer, Uri>
{
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, @NonNull Integer ringtoneType)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent result)
    {
        if (resultCode != Activity.RESULT_OK || result == null)
        {
            return null;
        }
        Log.d("BADI_URI","test");
        return result.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
    }
}
