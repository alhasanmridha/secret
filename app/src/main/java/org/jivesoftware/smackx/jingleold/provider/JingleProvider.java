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

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.jingleold.JingleActionEnum;
import org.jivesoftware.smackx.jingleold.packet.Jingle;
import org.jivesoftware.smackx.jingleold.packet.JingleContent;
import org.jivesoftware.smackx.jingleold.packet.JingleContentInfo;
import org.jivesoftware.smackx.jingleold.packet.JingleDescription;
import org.jivesoftware.smackx.jingleold.packet.JingleTransport;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParser;

/**
 * The JingleProvider parses Jingle packets.
 * 
 * @author Alvaro Saurin
 */
public class JingleProvider extends IQProvider<Jingle> {

    /**
     * Parse a iq/jingle element.
     * @throws Exception
     */
    @Override
    public synchronized Jingle parse(XmlPullParser parser, int intialDepth)
                    throws Exception {

        Jingle jingle = new Jingle();
        String sid = "";
        JingleActionEnum action;
        Jid initiator = null;
        Jid responder = null;
        boolean done = false;
        JingleContent currentContent = null;

        // Sub-elements providers
        JingleContentProvider jcp = new JingleContentProvider();
        JingleDescriptionProvider jdpAudio = new JingleDescriptionProvider.Audio();
        JingleDescriptionProvider jSDP = new JingleDescriptionProvider.WebRTCSDP();
        JingleDescriptionProvider jCandidate = new JingleDescriptionProvider.WebRTCCandidate();
        JingleTransportProvider jtpRawUdp = new JingleTransportProvider.RawUdp();
        JingleTransportProvider jtpIce = new JingleTransportProvider.Ice();
        ExtensionElementProvider<?> jmipAudio = new JingleContentInfoProvider.Audio();

        int eventType;
        String elementName;
        String namespace;

        // Get some attributes for the <jingle> element
        sid = parser.getAttributeValue("", "sid");
        action = JingleActionEnum.getAction(parser.getAttributeValue("", "action"));
        initiator = ParserUtils.getJidAttribute(parser, "initiator");
        responder = ParserUtils.getJidAttribute(parser, "responder");

        jingle.setSid(sid);
        jingle.setAction(action);
        jingle.setInitiator(initiator);
        jingle.setResponder(responder);

        // Start processing sub-elements
        while (!done) {
            eventType = parser.next();
            elementName = parser.getName();
            namespace = parser.getNamespace();

            if (eventType == XmlPullParser.START_TAG) {

                // Parse some well know subelements, depending on the namespaces
                // and element names...
                Log.e("newtag","====>"+elementName+"=====>"+namespace+" "+ parser.getAttributeValue("urn:xmpp:jingle:apps:sdp","sdp"));

                if (elementName.equals(JingleContent.NODENAME)) {
                    // Add a new <content> element to the jingle

                    currentContent = jcp.parse(parser);
                    Log.e("newtag","====>"+elementName+"=====>"+namespace+" "+ currentContent.toXML());
                    jingle.addContent(currentContent);
                    Log.e("newtag","shakeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeb11111111111");
                } else if (elementName.equals(JingleDescription.NODENAME) && namespace.equals(JingleDescription.WebRTCSDP.NAMESPACE)){
                    Log.e("newtag","shakeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeb22222222222");
                    currentContent.setDescription(jSDP.parse(parser));
                }else if (elementName.equals(JingleDescription.NODENAME) && namespace.equals(JingleDescription.WebRTCCandidate.NAMESPACE)){
                    Log.e("newtag","shakeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeb33333333333");
                    currentContent.setDescription(jCandidate.parse(parser));
                }
                else if (elementName.equals(JingleDescription.NODENAME) && namespace.equals(JingleDescription.Audio.NAMESPACE)) {
                    // Set the <description> element of the <content>
                    currentContent.setDescription(jdpAudio.parse(parser));
                } else if (elementName.equals(JingleTransport.NODENAME)) {
                    // Add all of the <transport> elements to the <content> of the jingle

                    // Parse the possible transport namespaces
                    if (namespace.equals(JingleTransport.RawUdp.NAMESPACE)) {
                        currentContent.addJingleTransport(jtpRawUdp.parse(parser));
                    } else if (namespace.equals(JingleTransport.Ice.NAMESPACE)) {
                        currentContent.addJingleTransport(jtpIce.parse(parser));
                    } else {
                        throw new SmackException("Unknown transport namespace \"" + namespace + "\" in Jingle packet.");
                    }
                } else if (namespace.equals(JingleContentInfo.Audio.NAMESPACE)) {
                    jingle.setContentInfo((JingleContentInfo) jmipAudio.parse(parser));
                } else {
                    throw new SmackException("Unknown combination of namespace \"" + namespace + "\" and element name \""
                            + elementName + "\" in Jingle packet.");
                }

            } else if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equals(Jingle.getElementName())) {
                    done = true;
                }
            }
        }

        return jingle;
    }
}
