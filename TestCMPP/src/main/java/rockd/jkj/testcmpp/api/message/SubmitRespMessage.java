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

import org.apache.mina.common.ByteBuffer;

import rockd.jkj.testcmpp.api.sys.CommandID;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: SubmitRespMessage.java,v 0.2 2007/05/15 13:45:29 
 */
public class SubmitRespMessage extends AbstractMessage
{
	private static final long serialVersionUID = CommandID.CMPP_SUBMIT_RESP;


	public SubmitRespMessage()
	{
		commandId = CommandID.CMPP_SUBMIT_RESP;
	}


	@Override
	public void encodeBody(ByteBuffer bt)
	{
		System.exit( 1 );
		// byte reserved = 0;
		// bt.put(reserved);
	}


	// TODO:
	@Override
	public void decodeBody(byte[] body)
	{
	}

}
