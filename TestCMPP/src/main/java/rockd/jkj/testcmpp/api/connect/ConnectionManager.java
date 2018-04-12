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

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoConnector;
import org.apache.mina.common.IoConnectorConfig;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;

import common.Logger;
import rockd.jkj.testcmpp.api.DeliverMessageDispose;
import rockd.jkj.testcmpp.api.connect.codec.CMPPProtocolCodecFactory;
import rockd.jkj.testcmpp.api.message.SubmitMessage;
import rockd.jkj.testcmpp.api.sys.BindType;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: ConnectionManager.java,v 0.2 2007/05/15 13:45:29 
 */
public class ConnectionManager
{
	private static Logger logger = Logger.getLogger( ConnectionManager.class );

	private String bindType = DefaultConfig.getBindType();

	private Hashtable htSessions = new Hashtable();

	private DeliverMessageDispose dmsgProcessor = null;


	private IoSession connectISMG(IoConnector ioConnector, SocketAddress sa, SessionHandler handler)
			throws InterruptedException, IOException
	{
		IoSession session = null;
		ConnectFuture connectFuture = ioConnector.connect( sa, (IoHandler) handler );
		connectFuture.join();

		// wait until tx is conncted.
		while (true)
		{
			logger.debug( "Waiting the connection to be connected..." );
			Thread.sleep( 500 );

			if (handler.getCmpp_status().equals( SessionHandler.STATUS_CONNECTED ))
			{
				logger.info( "Connected to ISMG." );
				break;
			}
			else
				if (handler.getCmpp_status().equals( SessionHandler.STATUS_CONNECT_FAILURE ))
				{
					logger.error( "ISMG returns error code. So exit." );
					System.exit( 1 );
				}
		}

		session = connectFuture.getSession();

		return session;

	}


	private IoSession getTxSession()
	{
		IoSession is = null;
		if (htSessions.get( "TX_SESSION" ) != null)
		{
			is = (IoSession) htSessions.get( "TX_SESSION" );
		}
		else
			if (htSessions.get( "BIDIRECTION_SESSION" ) != null)
			{
				is = (IoSession) htSessions.get( "BIDIRECTION_SESSION" );
			}
		return is;
	}


	// ������Ϣ
	public void sendMessage(SubmitMessage msg)
	{
		IoSession txSession = null;
		txSession = getTxSession();
		if (txSession == null)
		{
			logger.error( "Can't Get TX Session to send message." );
		}
		else
		{
			txSession.write( msg );
		}
	}


	public void initConnection() throws InterruptedException, IOException
	{
		// create connector
		IoConnector ioConnector = new SocketConnector();
		ioConnector.getFilterChain().addLast( "codec",
				(IoFilter) new ProtocolCodecFilter( new CMPPProtocolCodecFactory( false ) ) );
		ioConnector.getFilterChain().addLast( "logger", (IoFilter) new LoggingFilter() );
		((IoConnectorConfig) ioConnector.getDefaultConfig()).setConnectTimeout( DefaultConfig.getTimeout() );

		SocketAddress sa = null;

		if (bindType.equalsIgnoreCase( "TX+RX" ))
		{

			sa = DefaultConfig.getSocketAddress( "TX" );

			SessionHandler txHandler = new SessionHandler( dmsgProcessor, BindType.BIND_TX );
			IoSession txSession = connectISMG( ioConnector, sa, txHandler );
			htSessions.put( "TX_SESSION", txSession );

			sa = DefaultConfig.getSocketAddress( "RX" );

			SessionHandler rxHandler = new SessionHandler( dmsgProcessor, BindType.BIND_RX );
			IoSession rxSession = connectISMG( ioConnector, sa, rxHandler );
			htSessions.put( "RX_SESSION", rxSession );

		}
		else
			if (bindType.equalsIgnoreCase( "TX" ))
			{

				sa = DefaultConfig.getSocketAddress( "TX" );

				SessionHandler handler = new SessionHandler( dmsgProcessor, BindType.BIND_TX );
				IoSession session = connectISMG( ioConnector, sa, handler );
				htSessions.put( "TX_SESSION", session );

			}
			else
				if (bindType.equalsIgnoreCase( "RX" ))
				{

					sa = DefaultConfig.getSocketAddress( "RX" );

					SessionHandler handler = new SessionHandler( dmsgProcessor, BindType.BIND_RX );
					IoSession session = connectISMG( ioConnector, sa, handler );
					htSessions.put( "RX_SESSION", session );

				}
				else
					if (bindType.equalsIgnoreCase( "BIDIRECTION" ))
					{

						sa = DefaultConfig.getSocketAddress( "BIDIRECTION" );

						SessionHandler handler = new SessionHandler( dmsgProcessor, BindType.BIND_TRX );
						IoSession session = connectISMG( ioConnector, sa, handler );
						htSessions.put( "BIDIRECTION_SESSION", session );
					}
					else
					{
						logger.error( "NOW IT ONLY SUPPORT TX, RX, TX+RX and BIDIRECTION MODE!" );
					}

	}


	/**
	 * reconnect all connections.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void reconnect() throws InterruptedException, IOException
	{
		logger.info( "Try to reconnect..." );

		logger.info( "Try to stop all connection." );
		Enumeration keys = htSessions.keys();
		while (keys.hasMoreElements())
		{
			IoSession sess = (IoSession) htSessions.get( keys.nextElement() );
			if (sess != null)
				sess.close();

		}

		logger.info( "Sleeping 10 seconds to try to connect again." );
		Thread.sleep( 10000 );
		initConnection();
	}


	/**
	 * Check all connections' status. If some exception happened, reconnect.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void checkStatus() throws InterruptedException, IOException
	{
		while (true)
		{
			logger.info( "Checking if all connection are alive..." );

			boolean isOK = true;

			if (bindType.equalsIgnoreCase( "TX+RX" ))
			{
				IoSession tx = (IoSession) htSessions.get( "TX_SESSION" );
				if (!verifyIoSession( tx ))
					isOK = false;

				IoSession rx = (IoSession) htSessions.get( "RX_SESSION" );
				if (!verifyIoSession( rx ))
					isOK = false;

			}
			else
				if (bindType.equalsIgnoreCase( "BIDIRECTION" ))
				{
					IoSession rx = (IoSession) htSessions.get( "BIDIRECTION_SESSION" );
					if (!verifyIoSession( rx ))
						isOK = false;
				}
				else
					if (bindType.equalsIgnoreCase( "TX" ))
					{
						IoSession rx = (IoSession) htSessions.get( "TX_SESSION" );
						if (!verifyIoSession( rx ))
							isOK = false;
					}
					else
						if (bindType.equalsIgnoreCase( "RX" ))
						{
							IoSession rx = (IoSession) htSessions.get( "RX_SESSION" );
							if (!verifyIoSession( rx ))
								isOK = false;
						}

			if (isOK)
			{
				logger.info( "All connection are alive." );
			}
			else
			{
				logger.info( "Not all connections are alive. Try to reconnecting..." );
				reconnect();
			}

			Thread.sleep( 120000 );

		}
	}


	/**
	 * Check if a CMPP connection IoSession is valid.
	 * 
	 * @param rx
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private boolean verifyIoSession(IoSession rx) throws InterruptedException, IOException
	{
		if ((rx == null) || rx.isClosing())
			return false;

		SessionHandler rxHandler = (SessionHandler) rx.getHandler();
		if (!rxHandler.getCmpp_status().equalsIgnoreCase( "CONNECTED" ))
		{
			return false;
		}

		return true;
	}


	public ConnectionManager(DeliverMessageDispose dmsgProcessor) throws ConfigurationException
	{
		this.dmsgProcessor = dmsgProcessor;

	}
}
