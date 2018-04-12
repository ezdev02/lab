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

package rockd.jkj.testcmpp.api.message;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.mina.common.ByteBuffer;

import common.Logger;
import rockd.jkj.testcmpp.api.sys.CommandID;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: ConnectRespMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public class ConnectRespMessage extends AbstractMessage
{
	private static final long serialVersionUID = CommandID.CMPP_CONNECT_RESP;

	private static Logger logger = Logger.getLogger( ConnectRespMessage.class );

	private byte[] authenticatorISMG = null;

	private byte[] clientAuthenticatorSource = null;

	private byte version = 0x30;

	private int status;

	private String sharedSecret = "654321";


	public ConnectRespMessage()
	{
		commandId = CommandID.CMPP_CONNECT_RESP;
	}


	/**
	 * @return Returns the authenticatorSource.
	 */
	public void computeAuthenticatorISMG()
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			String toMd5 = status + sharedSecret;
			authenticatorISMG = md.digest( toMd5.getBytes() );
			logger.info( "authenticatorSource: " + ByteUtil.toHexForLog( clientAuthenticatorSource ) );
		}
		catch (NoSuchAlgorithmException e)
		{
			logger.error( "I don't know how to compute MD5!" );
			System.exit( 1 );
		}
	}


	/**
	 * @return Returns the timestamp.
	 */
	public int getTimestamp()
	{
		return status;
	}


	/**
	 * @param timestamp
	 *            The timestamp to set.
	 */
	public void setTimestamp(int timestamp)
	{
		this.status = timestamp;
	}


	/**
	 * @return Returns the version.
	 */
	public byte getVersion()
	{
		return version;
	}


	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(byte version)
	{
		this.version = version;
	}


	@Override
	public void encodeBody(ByteBuffer buf)
	{
		// buf.putInt(status);
		// byte[] bt = new byte[16];
		// computeAuthenticatorISMG();
		// buf.put(authenticatorISMG);
		// buf.put(version);
		logger.error( "Not supported yet" );
	}


	@Override
	public void decodeBody(byte[] body)
	{
		if (!DefaultConfig.isSMIAS())
		{
			int LEN_STATUS = 4;
			if (body.length == 18)
			{
				LEN_STATUS = 1;
				status = body[0];
			}
			else
			{
				status = ByteUtil.byte2Int( body, 0 );
			}

			authenticatorISMG = new byte[16];
			System.arraycopy( body, LEN_STATUS, authenticatorISMG, 0, 16 );

			version = body[LEN_STATUS + 16];

		}
		else
		{ // SMIAS_NOTE:
			status = commandStatus;
			if (commandStatus == 0)
			{
				// version = body[0];
				// TODO: Need to check the response from ISMG.
				// authenticatorISMG = new byte[16];
				// System.arraycopy(body, 1, authenticatorISMG, 0, 16);
			}
		}
	}


	@Override
	public String toString()
	{
		return super.toString() + " TotalLength: (" + totalLength + ')' + " AuthenticatorSource:(" + authenticatorISMG + ')'
				+ " Version(" + version + ')' + " Status(" + status + ")";
	}


	/**
	 * @param authenticatorSource
	 *            The authenticatorSource to set.
	 */
	public void setClientAuthenticatorSource(byte[] authenticatorSource)
	{
		this.clientAuthenticatorSource = authenticatorSource;
	}


	/**
	 * @return Returns the status.
	 */
	public int getStatus()
	{
		return status;
	}
}
