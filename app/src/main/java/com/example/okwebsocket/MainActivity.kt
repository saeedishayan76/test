package com.example.okwebsocket

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.ByteString
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {
    lateinit var outP: TextView
    private  val TAG = "MainActivity"

    inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {

//            Log.i(TAG, "onOpen: connect")
//            webSocket.send("Hello")
//            webSocket.send("How Are You ?")
//            webSocket.close(1000, "closed")

//            webSocket.send("{\n" +
//                    "    \"op\": \"subscribe\",\n" +
//                    "    \"args\": [{ \"channel\": \"tickers\" }]\n" +
//                    "}");

//            webSocket.send(
//                "{\"op\": \"subscribe\", \"args\":[ \"spot/ticker:ETH-USDT\",\"spot/candle60s:ETH-USDT\" ]}"
//            )

            webSocket.send(
                "{\n" +
                        "  \"action\": \"subscribe\", \n" +
                        "  \"params\": {\n" +
                        "    \"symbols\": \"AAPL,RY,RY:TSX,EUR/USD,BTC/USD\"\n" +
                        "  }\n" +
                        "}"
            )

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            out(bytes.hex())
            Log.i(TAG, "onMessage: ${bytes.hex()}")
            val b: ByteArray = Base64.decode(bytes.hex(),Base64.DEFAULT)
            val originalString = String(b,StandardCharsets.UTF_8)

            Log.i(TAG, "onMessage: $originalString")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1001, null)
            out(code.toString() + reason)
            Log.i(TAG, "onClosing: ")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            out(text)
            Log.i(TAG, "onMessageStr: $text")

        }


        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            out("Error ${t.message}")
            Log.i(TAG, "onFailure: ${t.message}  ${response}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        outP = findViewById(R.id.txt)


        findViewById<Button>(R.id.btn).setOnClickListener {
            start()
        }

    }

    private fun start() {
        val client = OkHttpClient.Builder().build()
//        val request = Request.Builder()
//            .url("wss://wsaws.okex.com:8443/ws/v5/public") // 'wss' - для защищенного канала
//            .build()

        val request = Request.Builder()
            .url("wss://ws.twelvedata.com/v1/quotes/price?apikey=110d6d8623e149c084d4d5ad2304d30a") // 'wss' - для защищенного канала
            .build()
        val wsListener = EchoWebSocketListener ()
        val webSocket = client.newWebSocket(request, wsListener) // this provide to make 'Open ws connection'
        client.dispatcher().executorService().shutdown()
    }

    fun out(name: String) {
        runOnUiThread {
            outP.text = name
        }
    }
}