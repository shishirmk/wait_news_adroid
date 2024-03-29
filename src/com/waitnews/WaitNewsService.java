package com.waitnews;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WaitNewsService extends Service {
    private static final int REQUEST_QUEUE_CAPACITY = 1000;
    
    private long requestId = 0;
    private LinkedBlockingQueue<WaitNewsServiceRequestQueueItem> requestQueue = 
            new LinkedBlockingQueue<WaitNewsServiceRequestQueueItem>(REQUEST_QUEUE_CAPACITY);
    
    /**
     * A WaitNewsServiceRequestQueueItem represents the requests objects
     * that go into the service request queue maintained by WaitNewsService.
     * Each WaitNewsServiceRequestQueueItem contains the WaitNewsServiceRequest
     * sent by the client, the callback associated with the Request, and the
     * unique request ID associated with the request.
     */
    final private class WaitNewsServiceRequestQueueItem {
        long requestId;
        public WaitNewsServiceRequest request;
        public WaitNewsServiceInt requestCallBack;
        public WaitNewsServiceRequestQueueItem(long requestId,
                                               WaitNewsServiceRequest request,
                                               WaitNewsServiceInt requestCallBack) {
            this.requestId = requestId;
            this.request = request;
            this.requestCallBack = requestCallBack;
        }
    }
    
    /** 
     * Callback interface which has to be implemented by WaitNewsService 
     * clients to receive a response to their request. 
     */ 
    public interface WaitNewsServiceInt {
        /**
         * WaitNewsService client must implement this function to 
         * receive the response to their requests. WaitNewsService 
         * invokes this function when it has the response for the 
         * request identified by requestId. 
         * 
         * @param requestId Unique Id identifying the request. This
         *                  is assigned by the Service when request
         *                  is first sent to the Service.
         *                  (See WaitNewsService.getSearchResults)
         * @param results   Response to the request identified by
         *                  the requestId.
         */
        public void handleResponse(long requestId, String results);
    }

    public class WaitNewsServiceBinder extends Binder {
        WaitNewsService getService() {
            return WaitNewsService.this;
        }
    }

    private final IBinder mBinder = new WaitNewsServiceBinder();
    private Thread mServiceThread = null;

    @Override
    public void onCreate() {
        mServiceThread = new Thread(new Runnable(){
            @Override
            public void run() {
                WaitNewsRestClient client = new WaitNewsRestClient();
                while (true) {
                    try {
                        WaitNewsServiceRequestQueueItem queueItem = requestQueue.take();
                        queueItem.requestCallBack.handleResponse(
                            queueItem.requestId,
                            client.execute(queueItem.request)
                        );
                    } catch (Exception e) {
                    	// Don't handle this currently.. Just log!
                        Log.d(WaitNewsService.class.toString(), "REST Query failed!");
                    }
                }
            }
        });
        mServiceThread.start();
    }

    @Override
    public void onDestroy() {
        // TODO: Look up if any actions necessary on destroy.
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public long sendRequest(WaitNewsServiceRequest request, 
                            WaitNewsServiceInt callBackObj) {
        Log.d(WaitNewsService.class.toString(), callBackObj.toString());
        try {
            requestQueue.put(new WaitNewsServiceRequestQueueItem(requestId++,
                                                                 request,
                                                                 callBackObj));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
        return requestId;
    }
}
