/*
* Copyright (C) 2013 Samsung System LSI
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.bluetooth.map;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.android.internal.util.FastXmlSerializer;

import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class BluetoothMapMessageListing {
    private boolean hasUnread = false;
    private static final String TAG = "BluetoothMapMessageListing";
    private static final boolean D = BluetoothMapService.DEBUG;

    private List<BluetoothMapMessageListingElement> list;

    public BluetoothMapMessageListing(){
     list = new ArrayList<BluetoothMapMessageListingElement>();
    }
    public void add(BluetoothMapMessageListingElement element) {
        list.add(element);
        /* update info regarding whether the list contains unread messages */
        if (element.getReadBool())
        {
            hasUnread = true;
        }
    }

    /**
     * Used to fetch the number of BluetoothMapMessageListingElement elements in the list.
     * @return the number of elements in the list.
     */
    public int getCount() {
        if(list != null)
        {
            return list.size();
        }
        return 0;
    }

    /**
     * does the list contain any unread messages
     * @return true if unread messages have been added to the list, else false
     */
    public boolean hasUnread()
    {
        return hasUnread;
    }


    /**
     *  returns the entire list as a list
     * @return list
     */
    public List<BluetoothMapMessageListingElement> getList(){
        return list;
    }

    /**
     * Encode the list of BluetoothMapMessageListingElement(s) into a UTF-8
     * formatted XML-string in a trimmed byte array
     *
     * @return a reference to the encoded byte array.
     * @throws UnsupportedEncodingException
     *             if UTF-8 encoding is unsupported on the platform.
     */
    public byte[] encode(boolean includeThreadId) throws UnsupportedEncodingException {
        StringWriter sw = new StringWriter();
        XmlSerializer xmlMsgElement = new FastXmlSerializer();
        try {
            xmlMsgElement.setOutput(sw);
            xmlMsgElement.startDocument("UTF-8", true);
            xmlMsgElement.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlMsgElement.startTag(null, "MAP-msg-listing");
            xmlMsgElement.attribute(null, "version", "1.0");
            // Do the XML encoding of list
            for (BluetoothMapMessageListingElement element : list) {
                element.encode(xmlMsgElement, includeThreadId); // Append the list element
            }
            xmlMsgElement.endTag(null, "MAP-msg-listing");
            xmlMsgElement.endDocument();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        } catch (IllegalStateException e) {
            Log.w(TAG, e);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
        return sw.toString().getBytes("UTF-8");
    }

    public void sort() {
        Collections.sort(list);
    }

    public void segment(int count, int offset) {
        count = Math.min(count, list.size() - offset);
        if (count > 0) {
            list = list.subList(offset, offset + count);
            if(list == null) {
                list = new ArrayList<BluetoothMapMessageListingElement>(); // Return an empty list
            }
        } else {
            if(offset > list.size()) {
               list = new ArrayList<BluetoothMapMessageListingElement>();
               Log.d(TAG, "offset greater than list size. Returning empty list");
            } else {
               list = list.subList(offset, list.size());
            }
        }
    }
}
