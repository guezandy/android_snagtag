package com.snagtag.interfaces;

import android.content.Intent;

public interface NFCHandler {
    
    /**
     * Read from an NFC tag; reacting to an intent generated from foreground
     * dispatch requesting a read
     * 
     * @param intent
     * @return content of nfctag (string)
     */
    public String processReadIntent(Intent intent);
    

}
