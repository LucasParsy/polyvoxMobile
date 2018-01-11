package com.tuxlu.polyvox.Utils.UIElements

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.NetworkLibraries.VolleyMultipartRequest
import com.tuxlu.polyvox.Utils.PathUtils
import com.tuxlu.polyvox.Utils.ToastType
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.util_file_chooser.*
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by tuxlu on 10/01/18.
 */

private val FILE_SEND = 42
private val PHOTO_SEND = 64

private var fileName: String = ""
private var type: String = ""
private var byte: ByteArray? = null
private var hasSent = false


class FileChooser: Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.util_file_chooser, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fChooserFileView.visibility = View.GONE
        setFileChooserListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    private fun setFileChooserListeners() {
        fChooserButtonBack.setOnClickListener({ _ -> fChooserBackButtonListener() })
        buttonPhoto.setOnClickListener({ _ -> startFileIntent("image/*", PHOTO_SEND) })
        buttonFile.setOnClickListener({ _ -> startFileIntent("*/*", FILE_SEND) })
        fChooserSendButton.setOnClickListener({ _ ->
            if (byte == null)
                return@setOnClickListener

            val part = VolleyMultipartRequest.DataPart("file", fileName, type, byte)
            val body = java.util.ArrayList<VolleyMultipartRequest.DataPart>()
            body.add(part)

            //val url = APIUrl.BASE_URL + APIUrl.CHAT + friendAuthor.username
            val url = APIUrl.FAKE_BASE_URL + APIUrl.CHAT + APIUrl.FAKE_CHAT_NAME
            APIRequest.Multipartequest(this.activity, Request.Method.POST, url,
                    true, body, { _ ->
                byte = null
                type = ""
                fileName = ""
                hasSent = true
                fChooserBackButtonListener()
            }, { _ -> fChooserBackButtonListener() })
        })
    }

    private fun startFileIntent(type: String, id: Int) {
        val intent = Intent()
        intent.type = type
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"), id)
    }

    private fun fChooserBackButtonListener() {
        fChooserTypeView.visibility = View.VISIBLE
        fChooserFileView.visibility = View.GONE
        try{
            (activity as OnCloseListener).onClose(hasSent);
        }catch (cce : ClassCastException) {
        }
        hasSent = false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (data == null) {
                    UtilsTemp.showToast(this.activity, getString(R.string.file_invalid), ToastType.ERROR)
                    return
                }
                fileName = PathUtils.UriToFileName(data.data, this.activity)
                fChooserDocumentName.text = fileName
                type = activity.contentResolver.getType(data.data)
                if (requestCode == FILE_SEND) {
                    val file = File(PathUtils.getPath(this.activity, data.data))
                    if (!file.exists() || !file.canRead())
                        return UtilsTemp.showToast(this.activity, getString(R.string.file_invalid), ToastType.ERROR)

                    byte = file.readBytes()
                    fChooserDocumentSize.text = UtilsTemp.byteSizeToString(byte!!.size, true)
                }
                if (requestCode == PHOTO_SEND) {
                    val bm = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
                    fChooserDocumentSize.text = UtilsTemp.byteSizeToString(bm.byteCount, true)

                    fileIcon.setImageBitmap(bm)
                    val byteBuffer = ByteBuffer.allocate(bm.byteCount)
                    bm.copyPixelsToBuffer(byteBuffer)
                    byte = byteBuffer.array()
                }
            } catch (e: IOException) {
                UtilsTemp.showToast(this.activity, getString(R.string.file_invalid), ToastType.ERROR)
                //e.printStackTrace()
            }
            fChooserTypeView.visibility = View.GONE
            fChooserFileView.visibility = View.VISIBLE
        }
    }

    public interface OnCloseListener {
        public fun onClose(hasSent: Boolean);
    }
}