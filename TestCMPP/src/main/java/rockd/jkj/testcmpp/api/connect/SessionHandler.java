/*
 * ============================================================================
 * GNU Lesser General Public License
 * ============================================================================
 *
 * deer-cmpp  - Free Java cmpp library.
 * http://deer-cmpp.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package rockd.jkj.testcmpp.api.connect;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import common.Logger;
import rockd.jkj.testcmpp.api.DeliverMessageDispose;
import rockd.jkj.testcmpp.api.message.ActiveTestMessage;
import rockd.jkj.testcmpp.api.message.ActiveTestRespMessage;
import rockd.jkj.testcmpp.api.message.ConnectMessage;
import rockd.jkj.testcmpp.api.message.ConnectRespMessage;
import rockd.jkj.testcmpp.api.message.DeliverMessage;
import rockd.jkj.testcmpp.api.message.DeliverRespMessage;
import rockd.jkj.testcmpp.api.message.SubmitRespMessage;
import rockd.jkj.testcmpp.api.message.TerminateMessage;
import rockd.jkj.testcmpp.api.message.TerminateRespMessage;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.sys.SequenceID;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: CMPPSessionHandler.java,v 0.2 2007/05/15 13:45:29 
 */
public class SessionHandler extends IoHandlerAdapter
{

	private static Logger logger = Logger.getLogger( SessionHandler.class );

	private static int idle = DefaultConfig.getIdle();

	private String cmpp_status = STATUS_NOT_CONNECTED;

	byte bind_type = 0;

	public static final String STATUS_NOT_CONNECTED = "NOT_CONNECTED";

	public static final String STATUS_CONNECTING = "CONNECTING";

	public static final String STATUS_CONNECT_FAILURE = "CONNECT_FAILURE";

	public static final String STATUS_CONNECTED = "CONNECTED";

	public static final String STATUS_CLOSED = "CLOSED";

	public static final String STATUS_CONNECTING_TWICE = "CONNECTING_TWICE";

	private DeliverMessageDispose msgProcessor = null;


	public SessionHandler(DeliverMessageDispose processor, byte bind_type)
	{
		this.bind_type = bind_type;
		this.msgProcessor = processor;
	}


	// String authSA = "";

	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		//
	}


	@Override
	public void sessionOpened(IoSession session)
	{
		logger.debug( "Trying to create connect message..." );
		ConnectMessage m = new ConnectMessage( bind_type );

		m.setSequenceId( SequenceID.next() );
		session.write( m );
		session.setIdleTime( IdleStatus.BOTH_IDLE, idle );

		cmpp_status = STATUS_CONNECTING;
	}


	@Override
	public void messageReceived(IoSession session, Object message)
	{
		logger.debug( bind_type + " Received Message" + message );
		if (message instanceof ConnectRespMessage)
		{
			ConnectRespMessage m = (ConnectRespMessage) message;
			// If connect failed. Disconnect the session;
			if (m.getStatus() != 0)
			{
				logger.error( "CMPP SMSC Gateway Return connect failure." );
				cmpp_status = STATUS_CONNECT_FAILURE;
				session.close();
				return;
			}
			cmpp_status = STATUS_CONNECTED;
		}
		else
			if (message instanceof ActiveTestMessage)
			{
				ActiveTestMessage oldM = (ActiveTestMessage) message;

				ActiveTestRespMessage m = new ActiveTestRespMessage();
				m.setSequenceId( oldM.getSequenceId() );
				session.write( m );
			}
			else
				if (message instanceof ActiveTestRespMessage)
				{
				}
				else
					if (message instanceof TerminateMessage)
					{
						TerminateMessage oldM = (TerminateMessage) message;
						TerminateRespMessage m = new TerminateRespMessage();
						m.setSequenceId( oldM.getSequenceId() );
						session.write( m );
						session.close();
					}
					else
						if (message instanceof DeliverMessage)
						{
							DeliverMessage oldM = (DeliverMessage) message;
							byte result = 0;
							DeliverRespMessage resp = new DeliverRespMessage( oldM.getMsg_id(), result );
							resp.setSequenceId( oldM.getSequenceId() );
							session.write( resp );
							msgProcessor.dispose( oldM );

						}
						else
							if (message instanceof SubmitRespMessage)
							{
							}
		logger.info( "Successfuly received :" + message );
	}


	@Override
	public void messageSent(IoSession session, Object message)
	{
		logger.info( "Message Successfully sent out: " + bind_type + " " + message );
	}


	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
	{
		logger.error( "Some exception happened! I  have to close the session." );
		logger.error( cause, cause );
		session.close();
	}


	@Override
	public void sessionClosed(IoSession session)
	{
		logger.error( "Session is closed. Maybe it's closed by peer!" );

		if (!cmpp_status.equals( STATUS_CONNECT_FAILURE ))
			cmpp_status = STATUS_CLOSED;
	}


	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		if (cmpp_status.equalsIgnoreCase( STATUS_CONNECTED ))
		{
			ActiveTestMessage m = new ActiveTestMessage();
			m.setSequenceId( SequenceID.next() );
			session.write( m );
		}
		else
		{
			logger.info( "Session is idle, but the status is not connected." );
		}
	}


	/**
	 * @return Returns the cmpp_status.
	 */
	public String getCmpp_status()
	{
		return cmpp_status;
	}
}
