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

import java.util.ArrayList;
import java.util.List;

import common.Logger;
import rockd.jkj.testcmpp.api.DeliverMessageDispose;
import rockd.jkj.testcmpp.api.connect.ConnectionManager;
import rockd.jkj.testcmpp.api.message.SubmitMessage;
import rockd.jkj.testcmpp.api.sys.DefaultConfig;
import rockd.jkj.testcmpp.api.sys.SequenceID;

/**
 * 
 * @author luomingjie (luomingjie@users.sourceforge.net ; luyiluo@126.com)
 * @version $Id: DefaultConfig.java,v 0.2 2007/05/15 13:45:29 
 */
public class Client
{

	public static void main(String[] args)
	{

		Logger logger = Logger.getLogger( Client.class );

		try
		{
			DeliverMessageDispose dmsgProcessor = new DeliverMessageDisposeImpl();
			ConnectionManager cm = new ConnectionManager( dmsgProcessor );
			cm.initConnection();
			Thread.sleep( 6000 );

			String msisdn = DefaultConfig.getTestAccount();
			List dest_list = new ArrayList();
			dest_list.add( msisdn );
			String content = "Hello World";
			SubmitMessage msg = new SubmitMessage( dest_list, content.getBytes() );
			msg.setSequenceId( SequenceID.next() );
			cm.sendMessage( msg );
			logger.info( msg );
			cm.checkStatus();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
