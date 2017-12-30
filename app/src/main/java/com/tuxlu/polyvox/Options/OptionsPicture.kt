package com.tuxlu.polyvox.Options

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.android.volley.Request
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.GlideApp
import com.tuxlu.polyvox.Utils.NetworkLibraries.VolleyMultipartRequest
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_user_options_picture.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by tuxlu on 29/11/17.
 */

class OptionsPicture : MyAppCompatActivity() {

    private val SELECT_FILE : Int = 42
    private var bm: Bitmap? = null

    private fun showLayout()
    {
        mainView.visibility = View.VISIBLE
        LoadingUtils.EndLoadingView(rootView)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options_picture)
        mainView.visibility = View.INVISIBLE
        newProfilePicLayout.visibility = View.GONE

        val url = APIUrl.BASE_URL + APIUrl.INFO_CURRENT_USER
        APIRequest.JSONrequest(this, Request.Method.GET, url,
                true, null, { response ->
            val obj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)
            val imageUrl = obj.getString("picture")
            if (UtilsTemp.isStringEmpty(imageUrl)) {
                currentprofilePicText.text = resources.getString(R.string.profile_picture_no)
                currentprofilePic.visibility = View.GONE
            }
            else {
                GlideApp.with(this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_account_circle_black_24dp).into(currentprofilePic)
            }

            showLayout()
        }, {_ -> showLayout()})

    }

    @Suppress("UNUSED_PARAMETER")
    fun addButtonClick(v: View)
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
        //todo: normally, you have to check if you have a permission to "read external storage"
        //but I tried on my phone, without any permission, it worked great.
        //should check after to see if there are no problems on other devices.
    }

    @Suppress("UNUSED_PARAMETER")
    fun sendButtonClick(v: View)
    {
        if (bm == null)
            return

        val ratio = bm!!.width / 120 //arbitrary size value
        val byteArrayOutputStream = ByteArrayOutputStream()
        bm = Bitmap.createScaledBitmap(bm, bm!!.width / ratio, bm!!.height / ratio, false)
        bm?.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        val stream : ByteArray = byteArrayOutputStream.toByteArray()

        val part = VolleyMultipartRequest.DataPart("profilePic", "pic", "image/png", stream)
        val body = ArrayList<VolleyMultipartRequest.DataPart>()
        body.add(part)

        val url = APIUrl.BASE_URL + APIUrl.SEND_PICTURE
        APIRequest.Multipartequest(this, Request.Method.POST, url,
                true, body, { _ ->
            newProfilePicLayout.visibility = View.GONE
            addProfilePicButton.text = getString(R.string.profile_picture_add)
            currentprofilePic.setImageDrawable(newProfilePic.drawable)
            UtilsTemp.showToast(this, getString(R.string.user_info_updated), ToastType.SUCCESS)
        }, null)
        //send request
        //si bon
    }

    //startActivity(Intent(baseContext, Home::class.java

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
            {

                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, data.data)
                        newProfilePic.setImageBitmap(bm)
                        newProfilePicLayout.visibility = View.VISIBLE
                        addProfilePicButton.text = getString(R.string.profile_picture_change)
                    } catch (e: IOException) {
                        UtilsTemp.showToast(this, getString(R.string.profile_picture_invalid), ToastType.ERROR)
                        //e.printStackTrace()
                    }

                }
                else
                    UtilsTemp.showToast(this, getString(R.string.profile_picture_invalid), ToastType.ERROR)
            }
        }
    }
}
