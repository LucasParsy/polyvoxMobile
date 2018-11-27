package com.tuxlu.polyvox.Chat

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.android.volley.Request
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.tuxlu.polyvox.R
import com.tuxlu.polyvox.Room.Chat.CustomChat
import com.tuxlu.polyvox.Utils.API.APIRequest
import com.tuxlu.polyvox.Utils.API.APIUrl
import com.tuxlu.polyvox.Utils.Auth.AuthUtils
import com.tuxlu.polyvox.Utils.Fileloader
import com.tuxlu.polyvox.Utils.UIElements.FileChooser
import com.tuxlu.polyvox.Utils.UIElements.LoadingUtils
import com.tuxlu.polyvox.Utils.UIElements.MyAppCompatActivity
import com.tuxlu.polyvox.Utils.UtilsTemp
import kotlinx.android.synthetic.main.activity_private_chat.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by tuxlu on 08/01/18.
 */
class Chat : MyAppCompatActivity(), MessagesListAdapter.OnLoadMoreListener,
        MessagesListAdapter.OnMessageClickListener<ChatMessage>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        FileChooser.OnCloseListener {

    private lateinit var myAuthor: Author
    private lateinit var friendAuthor: Author
    private lateinit var adapter: MessagesListAdapter<ChatMessage>
    private var shouldLoadMore = true
    private var pagesLoaded = 1
    private var sentList = ArrayList<String>()
    private var numberMessageReceived = 0

    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name")
        var friendUrl = intent.getStringExtra("url")
        friendAuthor = Author(name, friendUrl)
        CustomChat.requestUserMessages(friendAuthor.username)

        setContentView(R.layout.activity_private_chat)
        val myName = AuthUtils.getUsername(this)
        var imageUrl: String = AuthUtils.getPictureUrl(this)
        if (UtilsTemp.isStringEmpty(imageUrl))
            imageUrl = ""
        myAuthor = Author(myName, imageUrl)



        title = name

        val holders = MessageHolders()
                .registerContentType(
                        FileContentChecker.CONTENT_TYPE_FILE,
                        IncomingFileMessageViewHolder::class.java,
                        R.layout.chatkit_file_message_incoming,
                        OutcomingFileMessageViewHolder::class.java,
                        R.layout.chatkit_file_message_outcoming,
                        FileContentChecker())

        adapter = MessagesListAdapter(myName, holders, ImageLoader(this))
        messagesList.setAdapter(adapter)
        adapter.setOnMessageClickListener(this)
        adapter.setLoadMoreListener(this)
        input.setAttachmentsListener(this)
        input.setInputListener(this)
        onLoadMore(1, 42)
        LoadingUtils.EndLoadingView(rootView)
        val delay: Long = 2000 //10 seconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                updateMessages()
                handler.postDelayed(this, delay)
            }
        }, 500)

        //val url = APIUrl.BASE_URL + APIUrl.CHAT + friendAuthor.username
        val url = APIUrl.FAKE_BASE_URL + APIUrl.CHAT + APIUrl.FAKE_CHAT_NAME
        val frag = FileChooser()
        val bundle = Bundle()
        bundle.putString("url", url)
        frag.arguments = bundle
        fragmentManager.beginTransaction().add(R.id.fileChooserFragment, frag).commit()
    }

    override fun onMessageClick(message: ChatMessage) {
        if (!message.imageUrl.isNullOrBlank())
            Fileloader.openFile("", message.imageUrl!!, "image/", this)
        if (!message.getFileUrl().isNullOrBlank())
            Fileloader.openFile("", message.getFileUrl()!!, "", this)
    }


    override fun onSubmit(message: CharSequence): Boolean {

        CustomChat.sendUserMessage(friendAuthor.username, message.toString())
        val message = ChatMessage(message.toString(), myAuthor, Date())
        sentList.add(message.id)
        adapter.addToStart(message, true)
        return true
        /*
        //val url = APIUrl.BASE_URL + APIUrl.CHAT_UPDATE + friendAuthor.username
        val url = APIUrl.FAKE_BASE_URL + APIUrl.CHAT_UPDATE + APIUrl.FAKE_CHAT_NAME
        APIRequest.JSONrequest(this, Request.Method.POST, url,
                true, null, { _ ->
            //works only for text messages, not images!
            val message = ChatMessage(message.toString(), myAuthor, Date())
            sentList.add(message.id)
            adapter.addToStart(message, true)
        }
                , null)
        return true
        */
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        return
        /*
        if (!shouldLoadMore || !AuthUtils.hasAccount(this))
            return
        //val url = APIUrl.BASE_URL + APIUrl.CHAT + friendAuthor.username
        val url = APIUrl.FAKE_BASE_URL + APIUrl.CHAT + APIUrl.FAKE_CHAT_NAME
        val body = JSONObject()
        body.put(APIUrl.CHAT_PARAM, pagesLoaded)
        APIRequest.JSONrequest(this, Request.Method.GET, url,
                true, body, { response ->
            if (pagesLoaded == 1)
                LoadingUtils.EndLoadingView(rootView)

            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)

            //todo: verify if should flip condition with real API
            shouldLoadMore = !topObj.getBoolean("isLastMessages")
            val list = parseMessages(topObj)
            adapter.addToEnd(list, true)
            pagesLoaded++
        }
                , { _ ->
            if (pagesLoaded == 1)
                LoadingUtils.EndLoadingView(rootView)
        })
        */
    }

    fun updateMessages() {
        val finalMessages = ArrayList<ChatMessage>()
        var received = CustomChat.getUserMessages(friendAuthor.username)
        val maxSize = received.size
        if (maxSize <= numberMessageReceived)
            return

        received = received.subList(numberMessageReceived, maxSize)
        numberMessageReceived = maxSize
        for (elem in received) {
            val auth = if (elem.username == friendAuthor.username) friendAuthor else myAuthor
            finalMessages.add(ChatMessage(elem.message, auth, Date(elem.timestamp)))
            adapter.addToStart(ChatMessage(elem.message, auth, Date(elem.timestamp)), true)
        }
        //adapter.addToStart(finalMessages, false)
        /*
        //val url = APIUrl.BASE_URL + APIUrl.CHAT_UPDATE + friendAuthor.username
        val url = APIUrl.FAKE_BASE_URL + APIUrl.CHAT_UPDATE + APIUrl.FAKE_CHAT_NAME
        APIRequest.JSONrequest(this, Request.Method.GET, url,
                true, null, { response ->
            val topObj = response.getJSONObject(APIUrl.SEARCH_USER_JSONOBJECT)

            val list = parseMessages(topObj)
            adapter.deleteByIds(sentList.toTypedArray())
            sentList.clear()
            for (obj in list)
                adapter.addToStart(obj, true)
        }
                , { _ -> })
*/
    }

    fun parseMessages(topObj: JSONObject): ArrayList<ChatMessage> {
        var list = ArrayList<ChatMessage>()
        //todo: verify if should flip condition with real API
        val messagesArray = topObj.getJSONArray("messages")
        for (i in 0..(messagesArray.length() - 1)) {
            val elem = messagesArray.getJSONObject(i)
            val type = elem.getString("type")

            var text = ""
            if (type != "image") //todo: verify if File has title or not in final API
                text = elem.getString("text")

            var author = myAuthor
            if (!elem.getBoolean("isMe"))
                author = friendAuthor

            val ft = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date: Date = ft.parse(elem.getString("date"))


            var url: String? = null
            if (type == "image" || type == "file")
                url = elem.getString("url")
            if (type == "file") {
                val size = elem.getInt("size")
                //text = Fileloader.getUrlLastSegment(url!!)
                list.add(ChatMessage(text, author, date, null, size, url))
                continue
            }
            list.add(ChatMessage(text, author, date, url))
        }
        return list
    }

    override fun onAddAttachments() {
        UtilsTemp.hideKeyboard(this)
        fileChooserLayout.visibility = View.VISIBLE
    }

    override fun onClose(hasSent: Boolean) {
        fileChooserLayout.visibility = View.INVISIBLE
        if (hasSent)
            updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
