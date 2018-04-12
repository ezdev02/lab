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

package rockd.jkj.testcmpp.api.exampples;

import common.Logger;
import rockd.jkj.testcmpp.api.DeliverMessageDispose;
import rockd.jkj.testcmpp.api.message.DeliverMessage;
import rockd.jkj.testcmpp.api.util.ByteUtil;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: MessageProcessorEmptyImpl.java,v 0.2 2007/05/15 13:45:29 
 */
public class DeliverMessageDisposeImpl implements DeliverMessageDispose
{

	private static Logger logger = Logger.getLogger( DeliverMessage.class );


	@Override
	public void dispose(DeliverMessage m)
	{

		byte[] btContent = m.getMsg_Content();
		logger.info( "Received Message from:" + m.getSrcAddress() + " The content is: " + ByteUtil.toHexForLog( btContent ) );

		byte dcs = m.getDcs();

		try
		{
			if (dcs == 0)
			{
				logger.info( "Try to decoding the message using ASCII: " + new String( btContent ) );
			}
			else
				if (dcs == 15)
				{
					logger.info( "Try to decoding the message using GB2312: " + new String( btContent, "GB2312" ) );
				}
		}
		catch (Exception e)
		{
			logger.error( e, e );
		}
	}
}
