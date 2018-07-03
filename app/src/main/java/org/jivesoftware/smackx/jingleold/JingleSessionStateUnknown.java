/**
 *
 * Copyright the original author or authors
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
package org.jivesoftware.smackx.jingleold;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingleold.listeners.JingleListener;
import org.jivesoftware.smackx.jingleold.listeners.JingleSessionListener;
import org.jivesoftware.smackx.jingleold.packet.Jingle;
import org.jivesoftware.smackx.jingleold.packet.JingleError;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jingle. 
 *  @author Jeff Williams
 *  @see JingleSessionState
 */
public class JingleSessionStateUnknown extends JingleSessionState {
    private static final Logger LOGGER = Logger.getLogger(JingleSessionStateUnknown.class.getName());

    private static JingleSessionStateUnknown INSTANCE = null;

    protected JingleSessionStateUnknown() {
        // Prevent instantiation of the class.
    }

    /**
     *  A thread-safe means of getting the one instance of this class.
     *  @return The singleton instance of this class.
     */
    public synchronized static JingleSessionState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JingleSessionStateUnknown();
        }
        return INSTANCE;
    }

    @Override
    public void enter() {
        // TODO Auto-generated method stub

    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub

    }

    @Override
    public IQ processJingle(JingleSession session, Jingle jingle, JingleActionEnum action) throws SmackException, InterruptedException {
        IQ response = null;

        switch (action) {
            case SESSION_INITIATE:
                response = receiveSessionInitiateAction(session, jingle);
                break;

            case SESSION_TERMINATE:
                response = receiveSessionTerminateAction(session, jingle);
                break;
            case TRANSPORT_INFO:
                Log.e("Rooster","-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--------------");
                response = receiveTransportInfoAction(session, jingle);
                break;


            default:
                // Anything other than session-initiate is an error.
                response = session.createJingleError(jingle, JingleError.MALFORMED_STANZA);
                break;
        }

        return response;
    }


    private IQ receiveTransportInfoAction(JingleSession session, Jingle inJingle) {

        //IQ response = session.createAck(jingle);
        //session.triggerMediaReceived(jingle.toString());
        //return response;

        List<JingleListener> listeners = session.getListenersList();
        for (JingleListener li : listeners) {
            if (li instanceof JingleSessionListener) {
                JingleSessionListener sli = (JingleSessionListener) li;
                //try {
                Log.e("shakeeb", "$$$$$$$$$$$$$$$$$$$ 2222222222222222222 $$$$$$$$$$$$$$$$$$$$$$$$$$");
                sli.sessionMediaReceived(session,inJingle.toString());
                Log.e("shakeeb", "$$$$$$$$$$$$$$$$$$$ 4444444444444444444 $$$$$$$$$$$$$$$$$$$$$$$$$$");

                //} catch (SmackException.NotConnectedException e) {
                //    e.printStackTrace();
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
                //}
            }
        }
        // According to XEP-166 the only thing we can do is ack.
        session.setSessionState(JingleSessionStateActive.getInstance());
        return session.createAck(inJingle);

    }


    /**
     * In the UNKNOWN state we received a <session-initiate> action.
     * This method processes that action.
     * @throws SmackException 
     * @throws InterruptedException
     */

    private IQ receiveSessionInitiateAction(JingleSession session, Jingle inJingle) throws SmackException, InterruptedException {

        IQ response = null;
        boolean shouldAck = true;

        // According to XEP-166 when we get a session-initiate we need to check for:
        //      1. Initiator unknown
        //      2. Receiver redirection
        //      3. Does not support Jingle
        //      4. Does not support any <description> formats
        //      5. Does not support any <transport> formats
        // If all of the above are OK then we send an IQ type = result to ACK the session-initiate.

        // 1. Initiator unknown
        // TODO

        // 2. Receiver redirection
        // TODO

        // 3. Does not support Jingle
        // Handled by Smack's lower layer.

        // 4. Does not support any <description> formats
        // TODO

        // 5. Does not support any <transport> formats
        // TODO

        if (!shouldAck) {

            response = session.createJingleError(inJingle, JingleError.NEGOTIATION_ERROR);

        } else {

            // Create the Ack
            response = session.createAck(inJingle);

            session.setSessionState(JingleSessionStatePending.getInstance());
            /*
            // Now set up all of the initial content negotiators for the session.
            for (JingleContent jingleContent : inJingle.getContentsList()) {
                // First create the content negotiator for this <content> section.
                ContentNegotiator contentNeg = new ContentNegotiator(session, jingleContent.getCreator(), jingleContent
                        .getName());

                // Get the media negotiator that goes with the <description> of this content.
                JingleDescription jingleDescription = jingleContent.getDescription();

                // Loop through each media manager looking for the ones that matches the incoming 
                // session-initiate <content> choices.
                // (Set the first media manager as the default, so that in case things don't match we can still negotiate.)
                JingleMediaManager chosenMediaManager = session.getMediaManagers().get(0);
                for (JingleMediaManager mediaManager : session.getMediaManagers()) {
                    boolean matches = true;
                    for (PayloadType mediaPayloadType : mediaManager.getPayloads()) {
                        for (PayloadType descPayloadType2 : jingleDescription.getPayloadTypesList()) {
                            if (mediaPayloadType.getId() != descPayloadType2.getId()) {
                                matches = false;
                            }
                        }
                        if (matches) {
                            chosenMediaManager = mediaManager;
                        }
                    }
                }

                // Create the media negotiator for this content description.
                contentNeg.setMediaNegotiator(new MediaNegotiator(session, chosenMediaManager, jingleDescription
                        .getPayloadTypesList(), contentNeg));

                // For each transport type in this content, try to find the corresponding transport manager.
                // Then create a transport negotiator for that transport.
                for (JingleTransport jingleTransport : jingleContent.getJingleTransportsList()) {
                    for (JingleMediaManager mediaManager : session.getMediaManagers()) {

                        JingleTransportManager transportManager = mediaManager.getTransportManager();
                        TransportResolver resolver = null;
                        try {
                            resolver = transportManager.getResolver(session);
                        } catch (XMPPException e) {
                            LOGGER.log(Level.WARNING, "exception", e);
                        }

                        if (resolver.getType().equals(TransportResolver.Type.rawupd)) {
                            contentNeg.setTransportNegotiator(new TransportNegotiator.RawUdp(session, resolver, contentNeg));
                        }
                        if (resolver.getType().equals(TransportResolver.Type.ice)) {
                            contentNeg.setTransportNegotiator(new TransportNegotiator.Ice(session, resolver, contentNeg));
                        }
                    }
                }

                // Add the content negotiator to the session.
                session.addContentNegotiator(contentNeg);
            }

            // Now setup to track the media negotiators, so that we know when (if) to send a session-accept.
            session.setupListeners();
            */
        }

        return response;
    }

    /**
     * Receive and process the <session-terminate> action.
     */
    private IQ receiveSessionTerminateAction(JingleSession session, Jingle jingle) {

        // According to XEP-166 the only thing we can do is ack.
        IQ response = session.createAck(jingle);

        try {
            session.terminate("Closed remotely");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "exception", e);
        }

        return response;
    }

}
