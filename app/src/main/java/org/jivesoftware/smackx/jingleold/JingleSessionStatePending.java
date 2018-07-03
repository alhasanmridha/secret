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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jingle. 
 *  @author Jeff Williams
 *  @see JingleSessionState
 */

public class JingleSessionStatePending extends JingleSessionState {
    private static final Logger LOGGER = Logger.getLogger(JingleSessionStatePending.class.getName());

    private static JingleSessionStatePending INSTANCE = null;

    protected JingleSessionStatePending() {
        // Prevent instantiation of the class.
    }

    /**
     *  A thread-safe means of getting the one instance of this class.
     *  @return The singleton instance of this class.
     */
    public synchronized static JingleSessionState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JingleSessionStatePending();
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
    public IQ processJingle(JingleSession session, Jingle jingle, JingleActionEnum action) {
        IQ response = null;

        switch (action) {

            case CONTENT_ACCEPT:
                response = receiveContentAcceptAction(jingle);
                break;

            case CONTENT_MODIFY:
                break;

            case CONTENT_REMOVE:
                break;

            case SESSION_ACCEPT:
              //  Log.e("Rooster","Call connected ============================>SessionStatePending");
                response = receiveSessionAcceptAction(session, jingle);
                break;

            case SESSION_INFO:
                break;

            case SESSION_TERMINATE:
                response = receiveSessionTerminateAction(session, jingle);
                break;

            case TRANSPORT_INFO:
                Log.e("Rooster","-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--------------");
                response = receiveTransportInfoAction(session, jingle);
                break;

            default:
                // Anything other action is an error.
                // response = createJingleError(inJingle, JingleError.OUT_OF_ORDER);
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
     * Receive and process the <session-accept> action.
     */
    private IQ receiveContentAcceptAction(Jingle inJingle) {

        // According to XEP-167 the only thing we can do is ack.
        // setSessionState(JingleSessionStateEnum.ACTIVE);
        // return createAck(inJingle);

        // This is now handled by the media negotiator for the matching <content> segment.
        return null;
    }

    /**
     * Receive and process the <session-accept> action.
     */
    private IQ receiveSessionAcceptAction(JingleSession session, Jingle inJingle) {

        List<JingleListener> listeners = session.getListenersList();
        for (JingleListener li : listeners) {
            if (li instanceof JingleSessionListener) {
                JingleSessionListener sli = (JingleSessionListener) li;
                try {
                    sli.sessionEstablished(null,null,null, session);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // According to XEP-166 the only thing we can do is ack.
        session.setSessionState(JingleSessionStateActive.getInstance());
        return session.createAck(inJingle);
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
