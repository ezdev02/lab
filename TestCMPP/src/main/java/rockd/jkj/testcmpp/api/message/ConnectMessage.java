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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.common.ByteBuffer;

import common.Logger;
import rockd.jkj.testcmpp.api.sys.BindType;
import rockd.jkj.testcmpp.api.sys.CommandID;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: ConnectMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public class ConnectMessage extends AbstractMessage
{
	private static final long serialVersionUID = CommandID.CMPP_CONNECT;

	private static Logger logger = Logger.getLogger( ConnectMessage.class );

	private byte[] authenticatorSource = null;

	private byte bindType = BindType.BIND_TRX;

	private byte version;// = 0x30;

	private int timestamp;

	private String authenticatorSA;

	private String sharedSecret;


	public ConnectMessage(byte bindType)
	{
		this.authenticatorSA = DefaultConfig.getAuthenticatorSA();
		this.sharedSecret = DefaultConfig.getSharedSecret();
		this.bindType = bindType;

		commandId = CommandID.CMPP_CONNECT;

		String strNow = null;

		// SMIAS_NOTE: smias using the lapse seconds since 1970-01-01 GMT
		if (DefaultConfig.isSMIAS())
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat( "MMddHHmmss" );
			Date now = new Date();
			strNow = dateFormat.format( now );
			timestamp = (int) (new Date().getTime() / 1000);
		}
		else
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat( "MMddHHmmss" );
			Date now = new Date();
			strNow = dateFormat.format( now );
			timestamp = new Integer( strNow ).intValue();
		}

		// 9
		byte[] b0 = new byte[9];
		for ( int i = 0; i < 9; i++ )
		{
			b0[i] = 0;
		}

		try
		{

			ByteBuffer bb = ByteBuffer.allocate( 32 );
			bb.put( authenticatorSA.getBytes() );
			bb.put( b0 );
			bb.put( sharedSecret.getBytes() );

			if (DefaultConfig.isSMIAS())
			{
				bb.putInt( timestamp );
			}
			else
			{
				bb.put( strNow.getBytes() );
			}

			bb.flip();
			byte[] toMd5 = new byte[bb.limit()];
			bb.get( toMd5 );

			MessageDigest md = MessageDigest.getInstance( "MD5" );
			// String toMd5 = sourceAddr + "000000000" + sharedSecret + strNow;
			authenticatorSource = md.digest( toMd5 );
			logger.info( "authenticatorSource: " + ByteUtil.toHexForLog( authenticatorSource ) );
		}
		catch (NoSuchAlgorithmException e)
		{
			logger.error( "I don't know how to compute MD5!" );
			System.exit( 1 );
		}
	}


	/**
	 * @return Returns the authenticatorSource.
	 */
	public byte[] getAuthenticatorSource()
	{
		return authenticatorSource;
	}


	/**
	 * @param authenticatorSource
	 *            The authenticatorSource to set.
	 */
	public void setAuthenticatorSource(byte[] authenticatorSource)
	{
		this.authenticatorSource = authenticatorSource;
	}


	/**
	 * @return Returns the timestamp.
	 */
	public int getTimestamp()
	{
		return timestamp;
	}


	/**
	 * @param timestamp
	 *            The timestamp to set.
	 */
	public void setTimestamp(int timestamp)
	{
		this.timestamp = timestamp;
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
	public void setVersion(byte v)
	{
		version = v;
	}


	@Override
	public void encodeBody(ByteBuffer buf)
	{
		if (!DefaultConfig.isSMIAS())
		{
			buf.put( authenticatorSA.getBytes() );
			buf.put( authenticatorSource );

			if (bindType == BindType.BIND_TRX)
			{
				this.version = DefaultConfig.getSubVersion();
			}
			else
			{
				this.version = bindType;
			}

			buf.put( version );
			buf.putInt( timestamp );

		}
		else
		{ // SMIAS_NOTE:
			buf.put( ByteUtil.getCOctetBytes( authenticatorSA ) );
			buf.put( authenticatorSource );
			buf.put( bindType );
			this.version = DefaultConfig.getSubVersion();
			buf.put( version );
			buf.putInt( timestamp );
		}
	}


	/**
	 * For SP, it's not supported yet.
	 */
	@Override
	public void decodeBody(byte[] body)
	{
		logger.error( "NOT Supported!" );
	}


	@Override
	public String toString()
	{
		// it is a good practice to create toString() method on message classes.
		return super.toString() + " TotalLength: (" + totalLength + ')' + " AuthenticatorSource:(" + authenticatorSource + ')'
				+ " Version(" + version + ')' + " Timestamp(" + timestamp + ")";

	}
}
