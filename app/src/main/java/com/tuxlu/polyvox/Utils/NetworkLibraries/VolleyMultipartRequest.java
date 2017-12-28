package com.tuxlu.polyvox.Utils.NetworkLibraries;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tuxlu.polyvox.Utils.API.APIRequest;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tuxlu on 27/12/17.
 * based on Belal on 10/24/2017
   from https://www.simplifiedcoding.net/upload-image-to-server/
 */

public class VolleyMultipartRequest extends APIRequest.APIJsonObjectRequest {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private List<DataPart> content;
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private Response.Listener<JSONObject> mListener;
    private Response.ErrorListener mErrorListener;
    private Map<String, String> mHeaders;


    public VolleyMultipartRequest(int method, String url, List<DataPart> nContent,
                                  Response.Listener<JSONObject> listener,
                                  Response.ErrorListener errorListener, String token, Context context) {

        super(method, url, null, listener, errorListener, token, context);
        content = nContent;
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return (mHeaders != null) ? mHeaders : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // populate text payload
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dos, params, getParamsEncoding());
            }

            dataParse(dos);

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @throws IOException
     */
    private void dataParse(DataOutputStream dataOutputStream) throws IOException {
        if (content == null)
            return;
        for (int i=0; i<content.size(); i++)
            buildDataPart(dataOutputStream, content.get(i));
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @throws IOException
     */
    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                dataFile.name + "\"; filename=\"" + dataFile.fileName + "\"" + lineEnd);
        if (dataFile.type != null && !dataFile.type.trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.type + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.content);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    public static class DataPart {
        public String name;
        public String fileName;
        public String type;
        private byte[] content;

        public DataPart() {
        }

        public DataPart(String nName, String nFileName, String nType, byte[] nData) {
            name = nName;
            fileName = nFileName;
            type = nType;
            content = nData;
        }
    }
}