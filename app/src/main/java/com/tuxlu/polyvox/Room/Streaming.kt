package com.tuxlu.polyvox.Room


import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.support.annotation.RequiresApi
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.WindowManager
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.UtilsTemp
import com.tuxlu.polyvox.Utils.websockets.WebSocketClient
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.net.URI
import java.util.concurrent.Semaphore
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import kotlinx.android.synthetic.main.activity_room.*
import android.util.SparseIntArray
import android.view.TextureView
import com.tuxlu.polyvox.Utils.ToastType


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class Streaming(private val act: Room, private val token: String): WebSocketClient.Listener {

    private val socket = WebSocketClient(URI(APIUrl.STREAM_URL + token), this, "")
    private var recorders = Pair(MediaRecorder(), MediaRecorder())

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private val cameraOpenCloseLock = Semaphore(1)
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var videoSize : Size? = null
    private var previewSurface: Surface? = null;

    private val files = Pair(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/recorder1.webm"),
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/recorder2.webm"))


    //private val files = Pair(File(act.filesDir.absolutePath + "/recorder1"),
            //File(act.filesDir.absolutePath + "/recorder2"))

    private var backgroundHandler: Handler? = null

    private val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(nDev: CameraDevice) { cameraDevice = nDev }

        override fun onDisconnected(nDev: CameraDevice) {
            cameraDevice?.close()
            cameraDevice = null
            Log.wtf("STREAMING", "camera disconnected")
        }
        override fun onError(cameraDevice: CameraDevice, error: Int) { Log.wtf("STREAMING", "error " + error.toString()) }
    }


    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture,
                                               width: Int, height: Int) {
            val tex = act.recorderSurface.surfaceTexture
            val previewSize = Size(act.recorderSurface.width, act.recorderSurface.height)
            tex.setDefaultBufferSize(previewSize.width, previewSize.height)
            previewSurface = Surface(tex)
        }
        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            previewSurface = null
            return true
        }
        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

    }

    private var sensorOrientation = 0
    private val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    private val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
    private val DEFAULT_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }
    private val INVERSE_ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 270)
        append(Surface.ROTATION_90, 180)
        append(Surface.ROTATION_180, 90)
        append(Surface.ROTATION_270, 0)
    }



    init
    {
        act.recorderSurface.surfaceTextureListener = mSurfaceTextureListener;

        try {
            val manager = initCamera()
            val cameraId = manager.cameraIdList[1]
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: throw RuntimeException("Cannot get available preview/video sizes")
            sensorOrientation = characteristics.get(SENSOR_ORIENTATION)
            videoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            //if (!act.recorderSurface.isAvailable)
            //return

        } catch (e: Exception) {Log.wtf("STREAMING", e.message)}

    }

    private fun initCamera() : CameraManager
    {
        val manager = act.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList[1]
        if (UtilsTemp.checkPermission(act, android.Manifest.permission.CAMERA))
            manager.openCamera(cameraId, stateCallback, null)
        return manager
    }

    @SuppressLint("MissingPermission")
    fun start() : Boolean {
        if (!UtilsTemp.checkPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
        !UtilsTemp.checkPermission(act, android.Manifest.permission.CAMERA) ||
        !UtilsTemp.checkPermission(act, android.Manifest.permission.RECORD_AUDIO))
            return false
        act.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        act.stopPlayer()

        files.first.createNewFile()
        //files.second.createNewFile()


        if (cameraDevice == null)
        {
            initCamera()
            return false
        }

        if (previewSurface == null)
            return false
        //socket.connect()
        //act.recorderSurface.visibility = View.VISIBLE

        prepareRecorder(recorders.first, files.first.absolutePath)
        recorders.first.start()
        return true
    }

    private fun chooseVideoSize(choices: Array<Size>) = choices.firstOrNull {
        it.width == it.height * 16 / 9 && it.width <= 1080 } ?: choices[choices.size - 1]


    private fun prepareRecorder(recorder: MediaRecorder, filename: String) {


        val rotation = act.windowManager.defaultDisplay.rotation
        when (sensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES ->
                recorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation))
            SENSOR_ORIENTATION_INVERSE_DEGREES ->
                recorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation))
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
        //recorder.setAudioChannels(2)
        //recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        recorder.setPreviewDisplay(previewSurface!!)
        recorder.setVideoSize(videoSize!!.width, videoSize!!.height)
        recorder.setOutputFile(filename)
        //recorder.setVideoEncodingBitRate(10000000);
        //recorder.setAudioEncodingBitRate(10000000)
        //recorder.setMaxDuration(1000)
        recorder.prepare()




        val surfaces = ArrayList<Surface>().apply {
            add(previewSurface!!)
            add(recorder.surface)
        }

        previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(surfaces[0])
            addTarget(surfaces[1])
        }
        cameraDevice?.createCaptureSession(surfaces,
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("STREAMING", "onConfigureFailed " + session.toString())
                    }
                }, backgroundHandler)
    }

    private fun updatePreview() {
        if (cameraDevice == null)
            return
        previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        captureSession?.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler)

    }

    fun stop()
    {
        captureSession?.close();
        cameraDevice?.close()
        act.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        act.recorderSurface.visibility = View.INVISIBLE
        stopRecorder(recorders.first, files.first)
        UtilsTemp.showToast(act.baseContext, "Vidéo enregistrée dans /Download/recorder1.webm" ,
                ToastType.SUCCESS)
        //socket.disconnect()
        //recorders.first.stop()
        //recorders.first.release()
        //recorders.second.stop()
        //recorders.second.release()
    }


    private fun stopRecorder(recorder: MediaRecorder, file: File)
    {
        recorder.stop()
        recorder.reset()
        var bArray = ByteArray(file.length().toInt())
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bArray, 0, bArray.size)
            buf.close()
        } catch (e: Exception) { Log.w("STREAMING", e.message) }
        //sendMessage(bArray)
    }

    private fun sendMessage(data: ByteArray){ socket.send(data)}
    override fun onMessage(text: String) {}
    override fun onMessage(data: ByteArray) {}
    override fun onDisconnect(code: Int, reason: String?) {}
    override fun onConnect() {}
    override fun onError(error: Exception?) {Log.wtf("STREAMING", error!!.message)}

}


/*
    fun uselessPreview() {
        previewRequestBuilder = cameraDevice!!.createCaptureRequest(TEMPLATE_PREVIEW)

        cameraDevice?.createCaptureSession(listOf(surfaces[0]),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("STREAMING", "onConfigureFailed " + session.toString())
                    }
                }, backgroundHandler)
    } catch (e: CameraAccessException) { Log.e("STREAMING", "Camera exception " + e.message) }
 }
*/
