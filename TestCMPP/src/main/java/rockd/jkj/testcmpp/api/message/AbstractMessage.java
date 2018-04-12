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

import java.io.Serializable;

import org.apache.mina.common.ByteBuffer;

import rockd.jkj.testcmpp.api.sys.DefaultConfig;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: AbstractMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public abstract class AbstractMessage implements Serializable
{

	private static int headerLength = 12;

	static
	{
		// SMIAS-NOTE: Ther header of SMIAS message is 16 bytes.
		if (DefaultConfig.isSMIAS())
		{
			headerLength = 16;
		}
	}

	protected int commandId;

	protected int sequenceId;

	// SMIAS-NOTE:
	protected int commandStatus;

	protected int totalLength;


	// protected int bodyLength;

	public void encodeHeader(ByteBuffer buf)
	{
		// The total Length will be set later.
		buf.putInt( 0 );

		buf.putInt( commandId );

		// SMIAS-NOTE: command status is needed.
		if (DefaultConfig.isSMIAS())
			buf.putInt( commandStatus );

		buf.putInt( sequenceId );
	}


	public void decodeHeader(ByteBuffer buf)
	{
		totalLength = buf.getInt();
		commandId = buf.getInt();
		sequenceId = buf.getInt();
	}


	public abstract void encodeBody(ByteBuffer bt);


	public abstract void decodeBody(byte[] body);


	/**
	 * @return Returns the commandId.
	 */
	public int getCommandId()
	{
		return commandId;
	}


	/**
	 * @param commandId
	 *            The commandId to set.
	 */
	public void setCommandId(int commandId)
	{
		this.commandId = commandId;
	}


	/**
	 * @return Returns the sequenceId.
	 */
	public int getSequenceId()
	{
		return sequenceId;
	}


	/**
	 * @param sequenceId
	 *            The sequenceId to set.
	 */
	public void setSequenceId(int sequenceId)
	{
		this.sequenceId = sequenceId;
	}


	/**
	 * This is used for encode message.
	 * 
	 * @param totalLength
	 *            the totalLength to set.
	 */
	public void setTotalLength(int totalLength)
	{
		this.totalLength = totalLength;
	}


	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append( "TOTALLENGTH: " + totalLength );

		sb.append( " COMMAND ID:" + commandId );

		// SMIAS_NOTE:
		if (DefaultConfig.isSMIAS())
			sb.append( " COMMAND Status:" + commandStatus );

		sb.append( " SEQ:" + sequenceId );

		return sb.toString();
	}


	public void setCommandStatus(int commandStatus)
	{
		this.commandStatus = commandStatus;
	}


	public static int getHeaderLength()
	{
		return headerLength;
	}

}
