package com.tuxlu.polyvox.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.tuxlu.polyvox.R
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
        fun getPath(nName: String): File {

            var extension: String = ""
            val point = nName.lastIndexOf(".")
            if (point != -1)
                extension = nName.substring(point)

            var name = nName
            if (point != -1)
                name = nName.substring(0, point)

            val path = File(Environment.getExternalStorageDirectory().toString() + "/Download/Polyvox/")
            path.mkdirs() // creates needed dirs
            var file = File(path, name + extension)
            var num = 1;
            while (file.exists()) {
                file = File(path, "$name($num)$extension")
                num++;
            }
            return file
        }


        //todo: move theses function in more appropriate file
        @JvmStatic
        fun shareContent(context: Context, body: String, url: String) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body)
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url)
            sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)))
        }

        @JvmStatic
        fun shareImage(context: Context, body: String, image: File) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/jpeg"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, body)
            sharingIntent.putExtra(Intent.EXTRA_STREAM, android.support.v4.content.FileProvider.getUriForFile(context, "com.tuxlu.fileprovider", image))
            sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_with)))
        }

        //todo: move theses function in more appropriate file
        @JvmStatic
        fun byteSizeToString(bytes: Int, si: Boolean): String {
            val unit = if (si) 1000 else 1024
            if (bytes < unit) {
                return (bytes / 8).toString() + " B"
            }
            val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
            val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
            return String.format("%.1f %sB", bytes / 8 / Math.pow(unit.toDouble(), exp.toDouble()), pre)
        }

        //todo: move theses function in more appropriate file
        @JvmStatic
        fun hideKeyboard(act: Activity) {
            val view = act.currentFocus;
            if (view != null) {
                val imm: InputMethodManager = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
                imm.hideSoftInputFromWindow(view.windowToken, 0);
            }
        }


        @JvmStatic
        fun checkPermission(context: Activity, perm: String) : Boolean {
            val permissionCheck = ContextCompat.checkSelfPermission(context, perm)

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, perm)) {
                    UtilsTemp.showToast(context, context.getString(R.string.accept_permission))
                }
                ActivityCompat.requestPermissions(context, arrayOf(perm), 729)
            return false;
            }
            return true;
        }


        //todo: move theses function in more appropriate file
        @JvmStatic
        @JvmOverloads
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
        @SuppressLint("InlinedApi")
        @JvmStatic
        fun setNotificationSilentState(context: Context, mode: Int): Boolean {
            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager? ?: return false

            //state: true -> silent, false -> normal
            if (manager.ringerMode == mode)
                return true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? ?: return false

                if (!notificationManager.isNotificationPolicyAccessGranted) {

                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
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

        @JvmStatic
        fun isStringEmpty(str: String): Boolean {
            return (str.isBlank() || str == "null")
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