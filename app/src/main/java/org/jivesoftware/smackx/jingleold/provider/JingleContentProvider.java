/**
 *
 * Copyright 2003-2005 Jive Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.jingleold.provider;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smackx.jingleold.packet.JingleContent;
import org.jivesoftware.smackx.jingleold.packet.JingleDescription;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Jingle <content> provider.
 * 
 * @author Jeff Williams
 */
public class JingleContentProvider extends ExtensionElementProvider<JingleContent> {

    /**
     * Parse a JingleContent extension.
     */
    @Override
    public JingleContent parse(XmlPullParser parser, int initialDepth) {
        String elementName = parser.getName();
        String creator = parser.getAttributeValue("", JingleContent.CREATOR);
        String name = parser.getAttributeValue("", JingleContent.NAME);
        int endtag = 0;
        String sdp = "";
        String candidate = "";

        if (name.equals("sdp"))
        {

            int eventType;

            try {
                while((eventType = parser.getEventType())!= XmlPullParser.END_DOCUMENT) {

                    String tname = parser.getName();
                    String tnamespace = parser.getNamespace();
                    String ttext = parser.getText();

                    //if(eventType==XmlPullParser.START_TAG) {

                    if ((ttext != null) && (!ttext.trim().isEmpty()) ) {
                        sdp = ttext;
                        //Log.e("<><><><>shakeeb<><><><>", tname + " <><><><> " + tnamespace + "<><><><><><><>" + ttext);
                    }


                    if(eventType== XmlPullParser.END_TAG) {
                        ++endtag;
                        if (endtag==2) break;
                    }
                    try {
                        parser.next();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            // Try to get an Audio content info
            JingleContent jcon = new JingleContent(creator, name);
            jcon.setDescription(new JingleDescription.WebRTCSDP(sdp));
            return jcon;

        }
        if (name.equals("candidates"))
        {

            int eventType;

            try {
                while((eventType = parser.getEventType())!= XmlPullParser.END_DOCUMENT) {

                    String tname = parser.getName();
                    String tnamespace = parser.getNamespace();
                    String ttext = parser.getText();

                    //if(eventType==XmlPullParser.START_TAG) {

                    if ((ttext != null) && (!ttext.trim().isEmpty()) ) {
                        candidate = ttext;
                        //Log.e("<><><><>shakeeb<><><><>", tname + " <><><><> " + tnamespace + "<><><><><><><>" + ttext);
                    }


                    if(eventType== XmlPullParser.END_TAG) {
                        ++endtag;
                        if (endtag==2) break;
                    }
                    try {
                        parser.next();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            // Try to get an Audio content info
            JingleContent jcon = new JingleContent(creator, name);
            jcon.setDescription(new JingleDescription.WebRTCCandidate(candidate));
            return jcon;

        }
        // parser.

/*
        int eventType;
        try {
            while((eventType = parser.getEventType())!=XmlPullParser.END_DOCUMENT){

                String tname = parser.getName();
                String tnamespace = parser.getNamespace();
                String ttext = parser.getText();

                //if(eventType==XmlPullParser.START_TAG) {

                if("description".equals(parser.getName()) && (ttext != null)) {
                    Log.e("<><><><>shakeeb<><><><>",tname + " <><><><> "+tnamespace + "<><><><><><><>"+ ttext );
                }

                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        // Try to get an Audio content info
        JingleContent jcon = new JingleContent(creator, name);
        jcon.setDescription(new JingleDescription.WebRTCSDP(sdp));
        return jcon;

    }

}
