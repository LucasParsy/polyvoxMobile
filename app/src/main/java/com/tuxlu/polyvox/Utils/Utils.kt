package com.tuxlu.polyvox.Utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIUrl
import es.dmoral.toasty.Toasty
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by tuxlu on 28/11/17.
 */


enum class ToastType {
    NORMAL, ERROR, SUCCESS
}

class UtilsTemp {

    companion object {
        //@ColorInt val color = Color.parseColor("#DBDBDB")

        @JvmStatic
        fun sendMail(context: Context) {
            com.tuxlu.polyvox.Utils.API.APIRequest.JSONrequest(context, Request.Method.GET, APIUrl.BASE_URL + APIUrl.MAIL_SEND, true, null,
                    { _ ->
                        showToast(context, context.getString(R.string.confirmation_mail_sent_short), ToastType.SUCCESS)
                    }, { error ->
                //Todo: Gestion d'erreur si tout se passe mal
                error.printStackTrace()
            });
        }

        //todo: move theses function in more appropriate file
        @JvmStatic
        fun shareContent(context: Context, body: String, url: String) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body)
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url)
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)))
        }

        //todo: move theses function in more appropriate file

        //todo: move theses function in more appropriate file
        @JvmStatic
        fun showToast(context: Context, text: String, type: ToastType = ToastType.NORMAL, icon: Int = -1) {

            var toast: Toast? = null


            if (type == ToastType.ERROR)
                toast = Toasty.error(context, text, Toast.LENGTH_LONG, true)

            if (type == ToastType.SUCCESS)
                toast = Toasty.success(context, text, Toast.LENGTH_LONG, true)

            if (type == ToastType.NORMAL && icon != -1)
            toast = Toasty.normal(context, text, ContextCompat.getDrawable(context, icon))
            if (type == ToastType.NORMAL && icon == -1)
                toast = Toasty.normal(context, text)

            /*
            change color toast
            if (type == ToastType.NORMAL && icon != -1)
                toast = Toasty.custom(context, text, ContextCompat.getDrawable(context, icon), color, Toast.LENGTH_LONG, true, true)
            if (type == ToastType.NORMAL && icon == -1)
                toast = Toasty.custom(context, text, null, color, Toast.LENGTH_LONG, false, true)
            */

            //custom id created by the lib
            val v = toast!!.view.findViewById<TextView>(R.id.toast_text)
            if (v != null) {
                v.gravity = Gravity.CENTER
                //if (type == ToastType.NORMAL)
                //v.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
            toast.show()
        }

        //todo: move theses function in more appropriate file
        @JvmStatic
        fun setNotificationSilentState(context: Context, mode: Int): Boolean {
            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager? ?: return false

            //state: true -> silent, false -> normal
            if (manager.ringerMode == mode)
                return true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? ?: return false

                if (!notificationManager.isNotificationPolicyAccessGranted) {

                    val intent = Intent(
                            Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

                    context.startActivity(intent)
                    return false
                }
            }
            manager.ringerMode = mode
            return true

            /*
                la fonction prend un int "mode" en paramètre.
                pour mettre en silencieux l'appli, puis revenir au mode précedemment défini,
                il faut d'abord enregistrer le mode actuel.

                ex: avant d'appeler cette fonction pour mute:

                AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (manager != null);
                int mode  = manager.getRingerMode();
                setNotificationSilentState(this, AudioManager.RINGER_MODE_SILENT)
                ...
                //revenir au mode précédent:
                setNotificationSilentState(this, mode)


                Todo: comment bien faire: prendre l'activité qui appelle la fonction, et un int en paramètres
                lancer l'activité de demande de permission, qui va appeler une callback sur l'activité originelle
                redemander la permission dans cette callback.

                activity.startActivityForResult(intent), REQUESTCODENUMBER);

                @Override
                        ask again for permission
                    }
                }
                */


        }

        /*
                    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // Check which request we're responding to
                        if (requestCode == REQUESTCODENUMBER) {
        */
        @JvmStatic
        fun bitmapToFile(myBmp: Bitmap, filename: String, context: Context) {
            val f = File(context.cacheDir, filename + ".png")
            try {
                f.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }

            val bos = ByteArrayOutputStream()
            myBmp.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmap = bos.toByteArray()

            //write the bytes in file
            try {
                val fos = FileOutputStream(f)
                fos.write(bitmap)
                fos.flush()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}