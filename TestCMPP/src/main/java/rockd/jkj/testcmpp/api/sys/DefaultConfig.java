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

package rockd.jkj.testcmpp.api.sys;

import java.net.InetSocketAddress;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import common.Logger;

/**
 * CMPP配置信息
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: DefaultConfig.java,v 0.2 2007/05/15 13:45:29 
 */
public class DefaultConfig
{

	private static Logger logger = Logger.getLogger( DefaultConfig.class );

	private static XMLConfiguration xmlConfig = null;

	private static String protocol = "CMPP";

	private static byte protocolVersion = 0x20;

	private static byte subVersion = 0x20;

	private static String protocolVariation = "Standard";

	private static int timeout = 30;

	private static int idle = 30;

	private static String tpda = "";

	/**
	 * SP's Enterprise ID. <br/>corresponding to "SP_Id" in CMPP. Used in .
	 */
	private static String spid = "";

	private static String serviceId = "";

	/**
	 * Authenticator Source Address. <br/> Normally it will be the same as spid.
	 * But some implementation use a different one, so it's here. used in
	 * "Source_Addr" in CMPP_CONNECT.
	 */
	private static String authenticatorSA = "";

	private static String sharedSecret = "";

	private static String testAccount = "";

	private static String bindType = "";

	private static byte feeUserType = 0;

	private static String feeUser = "";

	static
	{
		try
		{
			xmlConfig = new XMLConfiguration( "config.xml" );

			// get CMPP specific configuration.
			protocolVariation = xmlConfig.getString( "cmpp.protocol_variation" );
			subVersion = xmlConfig.getByte( "cmpp.version" );
			bindType = xmlConfig.getString( "cmpp.bind_type" );

			timeout = xmlConfig.getInt( "cmpp.timeout" );
			idle = xmlConfig.getInt( "cmpp.idle" );

			tpda = xmlConfig.getString( "cmpp.tpda" );
			spid = xmlConfig.getString( "cmpp.spid" );
			serviceId = xmlConfig.getString( "cmpp.service_id" );

			authenticatorSA = xmlConfig.getString( "cmpp.authenticator_sa" );
			sharedSecret = xmlConfig.getString( "cmpp.shared_secret" );

			testAccount = xmlConfig.getString( "cmpp.test_account" );

			feeUserType = xmlConfig.getByte( "cmpp.fee_user_type" );
			feeUser = xmlConfig.getString( "cmpp.fee_user" );

		}
		catch (ConfigurationException cex)
		{
			logger.error( cex, cex );
			logger.fatal( "Config System is not available. Shutting down..." );
			System.exit( 1 );
		}
	}


	/**
	 * Authenticator Source Address. <br/> Normally it will be the same as spid.
	 * But some implementation use a different one, so it's here. used in
	 * "Source_Addr" in CMPP_CONNECT.
	 */
	public static String getProtocolVariation()
	{
		return protocolVariation;
	}


	/**
	 * Check if SMIAS Variation is used.
	 * 
	 * @return
	 */
	public static boolean isSMIAS()
	{
		boolean ret = false;
		if (protocolVariation.equals( "SMIAS" ))
		{
			ret = true;
		}
		return ret;
	}


	public static byte getProtocolVersion()
	{
		return protocolVersion;
	}


	public static void setProtocolVersion(byte protocolVersion)
	{
		DefaultConfig.protocolVersion = protocolVersion;
	}


	public static String getAuthenticatorSA()
	{
		return authenticatorSA;
	}


	public static String getBindType()
	{
		return bindType;
	}


	public static int getIdle()
	{
		return idle;
	}


	public static String getServiceId()
	{
		return serviceId;
	}


	public static String getSharedSecret()
	{
		return sharedSecret;
	}


	public static String getSpid()
	{
		return spid;
	}


	public static byte getSubVersion()
	{
		return subVersion;
	}


	public static String getTestAccount()
	{
		return testAccount;
	}


	public static int getTimeout()
	{
		return timeout;
	}


	public static String getTpda()
	{
		return tpda;
	}


	public static InetSocketAddress getSocketAddress(String usage)
	{
		InetSocketAddress sa = null;

		for ( int i = 0; i < 5; i++ )
		{
			String param = "cmpp.socket_address(" + i + ")[@applyto]";
			String applyto = xmlConfig.getString( param );
			if (applyto == null || applyto.equals( "" ))
				break;
			if (applyto.indexOf( usage ) >= 0)
			{
				String param1 = "cmpp.socket_address(" + i + ").host";
				String param2 = "cmpp.socket_address(" + i + ").port";
				String host = xmlConfig.getString( param1 );
				int port = xmlConfig.getInt( param2 );
				sa = new InetSocketAddress( host, port );
				break;
			}
		}

		return sa;
	}


	public static byte getFeeUserType()
	{
		return feeUserType;
	}


	public static String getFeeUser()
	{
		return feeUser;
	}


	public static String getProtocol()
	{
		return protocol;
	}


	public static XMLConfiguration getXmlConfig()
	{
		return xmlConfig;
	}
}
